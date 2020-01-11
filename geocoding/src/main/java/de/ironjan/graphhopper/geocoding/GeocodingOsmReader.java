package de.ironjan.graphhopper.geocoding;

import com.carrotsearch.hppc.LongArrayList;
import com.graphhopper.reader.ReaderElement;
import com.graphhopper.reader.ReaderNode;
import com.graphhopper.reader.ReaderWay;
import com.graphhopper.reader.osm.OSMInput;
import com.graphhopper.reader.osm.OSMInputFile;
import com.graphhopper.util.StopWatch;
import de.ironjan.graphhopper.extensions_core.Coordinate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// TODO implement DataReader ?
public class GeocodingOsmReader {

    private File osmFile;
    private int workerThreads = 2;
    private HashMap<String, LongArrayList> roomNodes = new HashMap<>();
    private HashMap<Long, Coordinate> doors = new HashMap<>();
    private HashMap<String, Double> roomLevels = new HashMap<>();
    private List<Poi> discoveredPois = new ArrayList<>();
    // Missing or invalid level
    private Logger logger = LoggerFactory.getLogger(GeocodingOsmReader.class.getName());

    GeocodingOsmReader setOsmFile(File osmFile) {
        this.osmFile = osmFile;
        return this;
    }

    public void setWorkerThreads(int workerThreads) {
        this.workerThreads = workerThreads;
    }

    /**
     * This method triggers reading the underlying data to create a graph
     * @return
     */
    public List<Poi> readGraph() throws IOException {
        if (osmFile == null)
            throw new IllegalStateException("No OSM file specified");

        if (!osmFile.exists())
            throw new IllegalStateException("Your specified OSM file does not exist:" + osmFile.getAbsolutePath());

        StopWatch sw1 = new StopWatch().start();
        preProcess(osmFile);
        sw1.stop();

        StopWatch sw2 = new StopWatch().start();
        convertDataToPois();
        sw2.stop();

        return discoveredPois;
    }

    /**
     *  Collects room-ways and door-nodes
     *  todo handle relations
     *
     * @param osmFile
     * @throws IOException
     */
    private void preProcess(File osmFile) throws IOException {
        try (OSMInput in = openOsmInputFile(osmFile)) {
            ReaderElement item;
            while ((item = in.getNext()) != null) {

                // we're only interested in named rooms and areas
                boolean isRoomLikeElement = item.hasTag("indoor", "room", "area");
                boolean hasName = item.hasTag("name");
                boolean isIndoorElementWithName = isRoomLikeElement && hasName;


                if (isIndoorElementWithName) {
                    String name = item.getTag("name");
                    logger.debug("Reading nodes of room like element. osmid = {}, name = {}.", item.getId(), name);

                    if (item.isType(ReaderElement.WAY)) {
                        ReaderWay way = (ReaderWay) item;
                        LongArrayList nodes = way.getNodes();
                        roomNodes.put(name, nodes);


                        try{
                            // FIXME invalid
                            Double level = Double.parseDouble(way.getTag("level", ""));
                            roomLevels.put(name, level);
                        }catch (NumberFormatException e){
                            logger.error("{}", e);
                        }
                    }
                }

                if(item.isType(ReaderElement.NODE)){
                    boolean isDoor = item.hasTag("door", "yes");
                    if(isDoor){
                        ReaderNode node = (ReaderNode) item;
                        doors.put(node.getId(),
                                new Coordinate(node.getLat(), node.getLon(), Double.NaN));
                    }
                }
            }

            logger.debug("Found {} rooms with {} doors.", roomNodes.size(), doors.size());
        } catch (Exception e) {
            throw new RuntimeException("Problem while parsing file", e);
        }
    }

    /**
     * Converts the read room-ways and doors into my pois
     */
    private void convertDataToPois() {
        for (String roomName : roomNodes.keySet()) {
            double level = Double.NaN;
            if(roomLevels.containsKey(roomName)){
                level = roomLevels.get(roomName);
            }

            List<Coordinate> entrances = new ArrayList<>();

            LongArrayList roomNodes = this.roomNodes.get(roomName);
            for (int i = 0; i < roomNodes.size(); i++) {
                long nodeId = roomNodes.get(i);
                if(doors.containsKey(nodeId)){
                    Coordinate coordinate = doors.get(nodeId);
                    Coordinate withLevel = new Coordinate(coordinate.lat, coordinate.lon, level);
                    entrances.add(withLevel);
                }
            }

            Poi poi = new Poi(roomName, entrances);
            discoveredPois.add(poi);
        }
    }

    private OSMInput openOsmInputFile(File osmFile) throws IOException, XMLStreamException {
        return new OSMInputFile(osmFile).setWorkerThreads(workerThreads).open();

    }
}
