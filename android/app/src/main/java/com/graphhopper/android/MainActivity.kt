package com.graphhopper.android

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Path
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast

import com.google.gson.Gson
import com.graphhopper.GHRequest
import com.graphhopper.GHResponse
import com.graphhopper.GraphHopper
import com.graphhopper.PathWrapper
import com.graphhopper.util.Constants
import com.graphhopper.util.Helper
import com.graphhopper.util.Parameters.Algorithms
import com.graphhopper.util.Parameters.Routing
import com.graphhopper.util.PointList
import com.graphhopper.util.ProgressListener
import com.graphhopper.util.StopWatch

import org.oscim.android.MapView
import org.oscim.android.canvas.AndroidGraphics
import org.oscim.backend.canvas.Bitmap
import org.oscim.backend.canvas.Color
import org.oscim.core.GeoPoint
import org.oscim.event.Gesture
import org.oscim.event.GestureListener
import org.oscim.event.MotionEvent
import org.oscim.layers.Layer
import org.oscim.layers.marker.ItemizedLayer
import org.oscim.layers.marker.MarkerItem
import org.oscim.layers.marker.MarkerSymbol
import org.oscim.layers.tile.buildings.BuildingLayer
import org.oscim.layers.tile.vector.VectorTileLayer
import org.oscim.layers.tile.vector.labeling.LabelLayer
import org.oscim.layers.vector.PathLayer
import org.oscim.layers.vector.geometries.Style
import org.oscim.theme.VtmThemes
import org.oscim.tiling.source.mapfile.MapFileTileSource

import java.io.File
import java.io.FilenameFilter
import java.io.InputStream
import java.lang.reflect.Array
import java.util.ArrayList
import java.util.Collections
import java.util.Scanner
import java.util.TreeMap

import de.ironjan.graphhopper.levelextension.LowLevelRouting
import de.ironjan.overpass.OverpassElement
import de.ironjan.overpass.OverpassResponse

open class MainActivity : Activity() {
    private var mapView: MapView? = null
    private var hopper: GraphHopper? = null
    private var start: GeoPoint? = null
    private var end: GeoPoint? = null
    private var localSpinner: Spinner? = null
    private var localButton: Button? = null
    private var remoteSpinner: Spinner? = null
    private var remoteButton: Button? = null
    @Volatile
    private var prepareInProgress = false
    @Volatile
    private var shortestPathRunning = false
    private var currentArea: String? = "berlin"
    private val fileListURL = "http://download2.graphhopper.com/public/maps/" + Constants.getMajorVersion() + "/"
    private val prefixURL = fileListURL
    private var downloadURL: String? = null
    private var mapsFolder: File? = null
    private var itemizedLayer: ItemizedLayer<MarkerItem>? = null
    private var pathLayer: PathLayer? = null
    private val TAG = MainActivity::class.java.simpleName
    private var opLayer: PathLayer? = null

    internal// only return true if already loaded
    val isReady: Boolean
        get() {
            if (hopper != null)
                return true

            if (prepareInProgress) {
                logUser("Preparation still in progress")
                return false
            }
            logUser("Prepare finished but GraphHopper not ready. This happens when there was an error while loading the files")
            return false
        }

    protected fun onLongPress(p: GeoPoint): Boolean {
        if (!isReady)
            return false

        if (shortestPathRunning) {
            logUser("Calculation still in progress")
            return false
        }

        if (start != null && end == null) {
            end = p
            shortestPathRunning = true
            itemizedLayer!!.addItem(createMarkerItem(p, R.drawable.marker_icon_red))
            mapView!!.map().updateMap(true)

            calcPath(start!!.latitude, start!!.longitude, end!!.latitude,
                    end!!.longitude)
        } else {
            start = p
            end = null
            // remove routing layers
            mapView!!.map().layers().remove(pathLayer)
            itemizedLayer!!.removeAllItems()

            itemizedLayer!!.addItem(createMarkerItem(start, R.drawable.marker_icon_green))
            mapView!!.map().updateMap(true)
        }
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        mapView = MapView(this)

        val input = EditText(this)
        input.setText(currentArea)
        val greaterOrEqKitkat = Build.VERSION.SDK_INT >= 19
        if (greaterOrEqKitkat) {
            if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
                logUser("GraphHopper is not usable without an external storage!")
                return
            }
            mapsFolder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "/graphhopper/maps/")
        } else
            mapsFolder = File(Environment.getExternalStorageDirectory(), "/graphhopper/maps/")

        if (!mapsFolder!!.exists())
            mapsFolder!!.mkdirs()

        val welcome = findViewById<View>(R.id.welcome) as TextView
        welcome.text = "Welcome to GraphHopper " + Constants.VERSION + "!"
        welcome.setPadding(6, 3, 3, 3)
        localSpinner = findViewById<View>(R.id.locale_area_spinner) as Spinner
        localButton = findViewById<View>(R.id.locale_button) as Button
        remoteSpinner = findViewById<View>(R.id.remote_area_spinner) as Spinner
        remoteButton = findViewById<View>(R.id.remote_button) as Button
        // TODO get user confirmation to download
        // if (AndroidHelper.isFastDownload(this))
        chooseAreaFromRemote()
        chooseAreaFromLocal()
    }

    override fun onResume() {
        super.onResume()
        mapView!!.onResume()
        loadOverpassExample()

    }

    override fun onPause() {
        super.onPause()
        mapView!!.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (hopper != null)
            hopper!!.close()

        hopper = null
        // necessary?
        System.gc()

        // Cleanup VTM
        mapView!!.map().destroy()
    }

    private fun initFiles(area: String?) {
        prepareInProgress = true
        currentArea = area
        downloadingFiles()
    }

    private fun chooseAreaFromLocal() {
        val nameList = ArrayList<String>()
        val files = mapsFolder!!.list { dir, filename ->
            filename != null && (filename.endsWith(".ghz") || filename
                    .endsWith("-gh"))
        }
        Collections.addAll(nameList, *files!!)

        if (nameList.isEmpty())
            return

        chooseArea(localButton, localSpinner, nameList,
                object : MySpinnerListener {
                    override fun onSelect(selectedArea: String?, selectedFile: String?) {
                        initFiles(selectedArea)
                    }
                })
    }

    private fun chooseAreaFromRemote() {
        object : GHAsyncTask<Void, Void, MutableList<String>>() {
            @Throws(Exception::class)
            override fun saveDoInBackground(vararg params: Void): MutableList<String> {
                val lines = AndroidDownloader().downloadAsString(fileListURL, false).split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val res = ArrayList<String>()
                for (str in lines) {
                    var index = str.indexOf("href=\"")
                    if (index >= 0) {
                        index += 6
                        val lastIndex = str.indexOf(".ghz", index)
                        if (lastIndex >= 0)
                            res.add(prefixURL + str.substring(index, lastIndex)
                                    + ".ghz")
                    }
                }

                return res
            }

            override fun onPostExecute(nameList: MutableList<String>?) {
                if (hasError()) {
                    error!!.printStackTrace()
                    logUser("Are you connected to the internet? Problem while fetching remote area list: " + errorMessage!!)
                    return
                } else if (nameList == null || nameList.isEmpty()) {
                    logUser("No maps created for your version!? $fileListURL")
                    return
                }

                val spinnerListener = object : MySpinnerListener {
                    override fun onSelect(selectedArea: String?, selectedFile: String?) {
                        if (selectedFile == null
                                || File(mapsFolder, selectedArea!! + ".ghz").exists()
                                || File(mapsFolder, "$selectedArea-gh").exists()) {
                            downloadURL = null
                        } else {
                            downloadURL = selectedFile
                        }
                        initFiles(selectedArea)
                    }
                }
                chooseArea(remoteButton, remoteSpinner, nameList, spinnerListener)
            }
        }.execute()
    }

    private fun chooseArea(button: Button?, spinner: Spinner?,
                           nameList: MutableList<String>, myListener: MySpinnerListener) {
        val nameToFullName = TreeMap<String, String>()
        for (fullName in nameList) {
            var tmp = Helper.pruneFileEnd(fullName)
            if (tmp.endsWith("-gh"))
                tmp = tmp.substring(0, tmp.length - 3)

            tmp = AndroidHelper.getFileName(tmp)
            nameToFullName[tmp] = fullName
        }
        nameList.clear()
        nameList.addAll(nameToFullName.keys)
        val spinnerArrayAdapter = ArrayAdapter(
                this, android.R.layout.simple_spinner_dropdown_item, nameList)
        spinner!!.adapter = spinnerArrayAdapter
        button!!.setOnClickListener {
            val o = spinner.selectedItem
            if (o != null && o.toString().length > 0 && !nameToFullName.isEmpty()) {
                val area = o.toString()
                myListener.onSelect(area, nameToFullName[area])
            } else {
                myListener.onSelect(null, null)
            }
        }
    }

    internal fun downloadingFiles() {
        val areaFolder = File(mapsFolder, currentArea!! + "-gh")
        if (downloadURL == null || areaFolder.exists()) {
            loadMap(areaFolder)
            return
        }

        val dialog = ProgressDialog(this)
        dialog.setMessage("Downloading and uncompressing " + downloadURL!!)
        dialog.isIndeterminate = false
        dialog.max = 100
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        dialog.show()

        object : GHAsyncTask<Void, Int, Any?>() {
            @Throws(Exception::class)
            override fun saveDoInBackground(vararg _ignore: Void): Any? {
                var localFolder = Helper.pruneFileEnd(AndroidHelper.getFileName(downloadURL!!))
                localFolder = File(mapsFolder, "$localFolder-gh").absolutePath
                log("downloading & unzipping $downloadURL to $localFolder")
                val downloader = AndroidDownloader()
                downloader.setTimeout(30000)
                downloader.downloadAndUnzip(downloadURL, localFolder
                ) { `val` -> publishProgress(`val`.toInt()) }
                return null
            }

            override fun onProgressUpdate(vararg values: Int?) {
                super.onProgressUpdate(*values)
                dialog.progress = values[0]?:0
            }

            override fun onPostExecute(result: Any?) {
                dialog.dismiss()
                if (hasError()) {
                    val str = "An error happened while retrieving maps:" + errorMessage!!
                    log(str, error)
                    logUser(str)
                } else {
                    loadMap(areaFolder)
                }
            }
        }.execute()
    }

    internal fun loadMap(areaFolder: File) {
        logUser("loading map")

        // Map events receiver
        mapView!!.map().layers().add(MapEventsReceiver(mapView!!.map()))

        // Map file source
        val tileSource = MapFileTileSource()
        tileSource.setMapFile(File(areaFolder, currentArea!! + ".map").absolutePath)
        val l = mapView!!.map().setBaseMap(tileSource)
        mapView!!.map().setTheme(VtmThemes.DEFAULT)
        mapView!!.map().layers().add(BuildingLayer(mapView!!.map(), l))
        mapView!!.map().layers().add(LabelLayer(mapView!!.map(), l))


        // Markers layer
        itemizedLayer = ItemizedLayer(mapView!!.map(), null as MarkerSymbol?)
        mapView!!.map().layers().add(itemizedLayer)

        // Map position
        val mapCenter = GeoPoint(50.2836424, 11.6190666)//tileSource.getMapInfo().boundingBox.getCenterPoint();
        mapView!!.map().setMapPosition(mapCenter.latitude, mapCenter.longitude, (1 shl 15).toDouble())

        setContentView(mapView)
        loadGraphStorage()
        drawOverpassExample()
    }

    private fun drawOverpassExample() {
        if (opLayer != null) {
            mapView!!.map().layers().remove(opLayer)
            Log.d(TAG, "Removed op layer")
        }

        val style = Style.builder()
                .fixed(true)
                .generalization(Style.GENERALIZATION_SMALL)
                .strokeColor(Color.RED)
                .fillColor(Color.CYAN)
                .fillAlpha(0.75f)
                .strokeWidth(4 * resources.displayMetrics.density)
                .build()
        opLayer = PathLayer(mapView!!.map(), style)
        val points = mutableListOf<GeoPoint>()
        Collections.addAll(points, *arrayOf(GeoPoint(50.2833069, 11.6405699), GeoPoint(50.2832737, 11.6405915), GeoPoint(50.2832483, 11.6406080), GeoPoint(50.2832163, 11.6404800), GeoPoint(50.2832749, 11.6404419), GeoPoint(50.2833029, 11.6405543), GeoPoint(50.2833069, 11.6405699)))
        opLayer!!.setPoints(points)
        mapView!!.map().layers().add(opLayer)
        mapView!!.map().updateMap(true)
        Log.d(TAG, "Added op layer")
        /*
    {
      "type": "way",
      "id": 402396741,
      "nodes": [
        404625872,
        4048066664,
        4048066656,
        4048066639,
        404625870,
        4048066667,
        404625872
      ],
      "tags": {
        "access": "customers",
        "addr:city": "Paderborn",
        "addr:housenumber": "29",
        "addr:postcode": "33102",
        "addr:street": "Bahnhofstraße",
        "indoor": "room",
        "level": "0",
        "name": "Relay",
        "opening_hours": "Mo-Fr 06:00-20:00; Sa 07:00-19:00; Su 08:00-19:00",
        "room": "shop",
        "shop": "books"
      }
    },

    {
      "type": "node",
      "id": 404625872,
      "lat": 51.7133069,
      "lon": 8.7405699
    },
    {
      "type": "node",
      "id": 4048066664,
      "lat": 51.7132737,
      "lon": 8.7405915
    },
    {
      "type": "node",
      "id": 4048066656,
      "lat": 51.7132483,
      "lon": 8.7406080
    },
    {
      "type": "node",
      "id": 4048066639,
      "lat": 51.7132163,
      "lon": 8.7404800
    },
    {
      "type": "node",
      "id": 404625870,
      "lat": 51.7132749,
      "lon": 8.7404419
    },
    {
      "type": "node",
      "id": 4048066667,
      "lat": 51.7133029,
      "lon": 8.7405543
    },
 */
    }

    internal fun loadGraphStorage() {
        logUser("loading graph (" + Constants.VERSION + ") ... ")
        object : GHAsyncTask<Void, Void, Path?>() {
            @Throws(Exception::class)
            override fun saveDoInBackground(vararg v: Void): Path? {
                val tmpHopp = GraphHopper().forMobile()
                tmpHopp.setElevation(true)
                tmpHopp.load(File(mapsFolder, currentArea!!).absolutePath + "-gh")
                log("found graph " + tmpHopp.graphHopperStorage.toString() + ", nodes:" + tmpHopp.graphHopperStorage.nodes)
                hopper = tmpHopp
                return null
            }

            override fun onPostExecute(o: Path?) {
                if (hasError()) {
                    logUser("An error happened while creating graph:" + errorMessage!!)
                } else {
                    logUser("Finished loading graph. Long press to define where to start and end the route.")
                }

                finishPrepare()
            }
        }.execute()
    }

    private fun loadOverpassExample() {
        try {
            val inputStream = resources.openRawResource(R.raw.response)
            val sc = Scanner(inputStream)
            val sb = StringBuilder()
            while (sc.hasNextLine()) {
                sb.append(sc.nextLine())
            }
            val response = sb.toString()
            Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
            Log.d(TAG, response)
            parseOverpassResponse(response)
            inputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun parseOverpassResponse(response: String) {
        val gson = Gson()

        val overpassResponse = gson.fromJson(response, OverpassResponse::class.java)

        val elements = overpassResponse.elements
        val ways = ArrayList<OverpassElement>()
        val nodes = ArrayList<OverpassElement>()
        for (e in elements) {
            if (e.isWay) {
                ways.add(e)
            } else {
                nodes.add(e)
            }
        }

        Log.d(TAG, "Got " + elements.size + " elements: " + nodes.size + " nodes and " + ways.size + " ways.")
    }

    private fun finishPrepare() {
        prepareInProgress = false
    }

    private fun createPathLayer(response: PathWrapper): PathLayer {
        val style = Style.builder()
                .fixed(true)
                .generalization(Style.GENERALIZATION_SMALL)
                .strokeColor(-0x66ff33cd)
                .strokeWidth(4 * resources.displayMetrics.density)
                .build()
        val pathLayer = PathLayer(mapView!!.map(), style)
        val geoPoints = ArrayList<GeoPoint>()
        val pointList = response.points
        for (i in 0 until pointList.size)
            geoPoints.add(GeoPoint(pointList.getLatitude(i), pointList.getLongitude(i)))
        pathLayer.setPoints(geoPoints)
        return pathLayer
    }

    private fun createMarkerItem(p: GeoPoint?, resource: Int): MarkerItem {
        val drawable = resources.getDrawable(resource)
        val bitmap = AndroidGraphics.drawableToBitmap(drawable)
        val markerSymbol = MarkerSymbol(bitmap, 0.5f, 1f)
        val markerItem = MarkerItem("", "", p)
        markerItem.marker = markerSymbol
        return markerItem
    }

    fun calcPath(fromLat: Double, fromLon: Double,
                 toLat: Double, toLon: Double) {

        log("calculating path ...")
        object : AsyncTask<Void, Void, PathWrapper>() {
            internal var time: Float = 0.toFloat()

            override fun doInBackground(vararg v: Void): PathWrapper {
                val sw = StopWatch().start()
                val lowLevelRouting = LowLevelRouting(hopper)
                val route = lowLevelRouting.getRoute(fromLat, fromLon, toLat, toLon, 0.0, 0.0)
                time = sw.stop().seconds
                return route
            }

            override fun onPostExecute(resp: PathWrapper) {
                if (!resp.hasErrors()) {
                    log("from:" + fromLat + "," + fromLon + " to:" + toLat + ","
                            + toLon + " found path with distance:" + resp.distance / 1000f + ", nodes:" + resp.points.size + ", time:"
                            + time + " " + resp.debugInfo)
                    logUser("the route is " + (resp.distance / 100).toInt() / 10f
                            + "km long, time:" + resp.time / 60000f + "min, debug:" + time)

                    pathLayer = createPathLayer(resp)
                    mapView!!.map().layers().add(pathLayer)
                    mapView!!.map().updateMap(true)
                    drawOverpassExample()
                } else {
                    logUser("Error:" + resp.errors)
                }
                shortestPathRunning = false
            }
        }.execute()
    }

    private fun log(str: String) {
        Log.i("GH", str)
    }

    private fun log(str: String, t: Throwable?) {
        Log.i("GH", str, t)
    }

    private fun logUser(str: String) {
        log(str)
        Toast.makeText(this, str, Toast.LENGTH_LONG).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menu.add(0, NEW_MENU_ID, 0, "Google")
        // menu.add(0, NEW_MENU_ID + 1, 0, "Other");
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            NEW_MENU_ID -> {
                if (start == null || end == null) {
                    logUser("tap screen to set start and end of route")
                    return true
                }
                val intent = Intent(Intent.ACTION_VIEW)
                // get rid of the dialog
                intent.setClassName("com.google.android.apps.maps",
                        "com.google.android.maps.MapsActivity")
                intent.data = Uri.parse("http://maps.google.com/maps?saddr="
                        + start!!.latitude + "," + start!!.longitude + "&daddr="
                        + end!!.latitude + "," + end!!.longitude)
                startActivity(intent)
            }
        }
        return true
    }

    interface MySpinnerListener {
        fun onSelect(selectedArea: String?, selectedFile: String?)
    }

    internal inner class MapEventsReceiver(map: org.oscim.map.Map) : Layer(map), GestureListener {

        override fun onGesture(g: Gesture, e: MotionEvent): Boolean {
            if (g is Gesture.LongPress) {
                val p = mMap.viewport().fromScreenPoint(e.x, e.y)
                return onLongPress(p)
            }
            return false
        }
    }

    companion object {
        private val NEW_MENU_ID = Menu.FIRST + 1
    }
}
