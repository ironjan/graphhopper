import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import com.graphhopper.storage.GraphHopperStorage;
import com.graphhopper.storage.NodeAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private final GraphHopper hopper;

    public Main(GraphHopper hopper) {
        this.hopper = hopper;
    }

    public static void main(String[] args) {
        String osmFile = args[0];
        String graphFolder = args[1];
        DirectoryDeleter.deleteDirectory(graphFolder);

        GraphHopper hopper = GraphLoader.get(osmFile, graphFolder);
        new Main(hopper).runLatLonTest(osmFile);

    }

    private void runLatLonTest(String osmFile) {
        if(osmFile.contains("stachus")){
            runStachusTest();
        }

        if(osmFile.contains("fuerstena")) {
            runFuTest();
        }

        if(osmFile.contains("issue")) {
            runIssueTest();
        }

        if(osmFile.contains("room")) {
            singleTest("Just a test", 51.7260712,8.7387656, 51.6995871, 8.7423705, 0,0);
        }

        if(osmFile.contains("place_test")) {
            singleTest(new Poi("WP", 0, 0,0), new Poi("NP", 2,2,0), true);
            singleTest(new Poi("WP", 0, 0,0), new Poi("EP", 0,4,0), true);
            GraphHopperStorage graph = hopper.getGraphHopperStorage();
            NodeAccess nodeAccess = graph.getNodeAccess();
            for(int i=0; i< graph.getNodes();i++){
                LoggerFactory.getLogger(Main.class.getName()).debug("Has tower node: {},{}", nodeAccess.getLongitude(i), nodeAccess.getLatitude(i));
            }
        }
    }

    private void runStachusTest() {

//        singleTest(0,48.1403750, 11.5592910, 48.139029, 11.568700, 0d, 0d);
//        singleTest(1,48.1387140, 11.5648430, 48.138832, 11.567654, 0d, 0d);
//        singleTest(2,48.1384420, 11.5649660, 48.139029, 11.568700, 0d, 0d);

        // Start is exactly on top of a "Stachuspassage, 1. Untergeschoss" node
        singleTest("Karlsplatz",48.1394991, 11.5659233, 48.139029, 11.568700, -0d, 0d);
        singleTest("Stachusp-1",48.1394991, 11.5659233, 48.139029, 11.568700, -1d, 0d);
//        singleTest("Hbf      0",48.140203,11.55972, 48.138514,11.568131, 0d, 0d);
//        singleTest("hbf      1",48.140203,11.55972, 48.138514,11.568131, 1d, 0d);
    }

    private void runFuTest() {

        singleTest(new Poi("entry", 51.73210,8.73502,0d), new Poi("f2", 51.73191690536,8.73486839258,2d),false);
        singleTest("entry-f2", 51.73210, 8.73502,51.73191690536, 8.73486839258, 0d, 2d);
        singleTest(new Poi("entry", 51.73210,8.73502,0d), new Poi("f2", 51.73191690536,8.73486839258,2d),true);
//        singleTest("entry-f0", 51.73210, 8.73502,51.73168,8.73467, 0d, -1d);
//        singleTest("entry-f2", 51.73210, 8.73502,51.73191690536, 8.73486839258, 0d, 2d, true);
//        singleTest("entry-f0", 51.73210, 8.73502,51.73168,8.73467, 0d, -1d, true);
    }

    private void runIssueTest(){
        Poi southern_mid_point = new Poi("southern mid point", 0, 0, Double.NaN);
        Poi northern_mid_point = new Poi("northern mid point", 10, 0, Double.NaN);
        Poi northern_east_point = new Poi("northern east point", 10, 5, Double.NaN);
        Poi southern_east_point = new Poi("southern east point", 5, 5, Double.NaN);
        Poi west_point = new Poi("west point", 5, -5, Double.NaN);

        singleTest(west_point, southern_east_point, true);
        singleTest(west_point, northern_mid_point,true);
        singleTest(west_point, southern_mid_point, true);
    }

    public class Poi{
        final double lat, lon, lvl;
        final String name;

        private Poi(String name, double lat, double lon, double lvl) {
            this.lat = lat;
            this.lon = lon;
            this.lvl = lvl;
            this.name = name;
        }
    }
    private void singleTest(Poi a, Poi b, boolean edgeBased){
        String msg = String.format("%s to %s", a.name, b.name);
        singleTest(msg, a.lat, a.lon, b.lat, b.lon, a.lvl, b.lvl, edgeBased);
    }

    private void singleTest(String run, double fromLat, double fromLon, double toLat, double toLon, double fromLvl, double toLvl, boolean edgeBased) {
        LOGGER.debug("Route {} from {},{},{} to {},{},{}. Edge based? {}", run, fromLat, fromLon, fromLvl, toLat, toLon, toLvl, edgeBased);

        LowLevelRouting lowLevelRouting = new LowLevelRouting(hopper, edgeBased);

        PathWrapper route = lowLevelRouting.getRoute(fromLat, fromLon, toLat, toLon, fromLvl, toLvl);
        PathPrinter.print("FootLevel LevelEF", route);
    }

    private void singleTest(String run, double fromLat, double fromLon, double toLat, double toLon, double fromLvl, double toLvl) {
        singleTest(run, fromLat, fromLon, toLat, toLon, fromLvl, toLvl, false);
    }

}
