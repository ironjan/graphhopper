import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import com.graphhopper.storage.GraphHopperStorage;
import com.graphhopper.storage.NodeAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private final GraphHopper hopper;
    private GeocodingSample geocoding;

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
        setupPois();

        if (osmFile.contains("stachus")) {
            runStachusTest();
        }

        if (osmFile.contains("fuerstena")) {
            runFuTest();
        }

        if (osmFile.contains("issue")) {
            runIssueTest();
        }

        if (osmFile.contains("room")) {
            singleTest("Just a test", 51.7260712, 8.7387656, 51.6995871, 8.7423705, 0, 0);
        }

        if (osmFile.contains("place_test")) {
            singleTest(new Poi("WP", 0, 0, 0), new Poi("NP", 2, 2, 0), true);
            singleTest(new Poi("WP", 0, 0, 0), new Poi("EP", 0, 4, 0), true);
            GraphHopperStorage graph = hopper.getGraphHopperStorage();
            NodeAccess nodeAccess = graph.getNodeAccess();
            for (int i = 0; i < graph.getNodes(); i++) {
                LoggerFactory.getLogger(Main.class.getName()).debug("Has tower node: {},{}", nodeAccess.getLongitude(i), nodeAccess.getLatitude(i));
            }
        }
    }

    private void setupPois() {
        this.geocoding = new GeocodingSample();
        geocoding.addAll(
                new Poi("Karlsplatz", 48.1394991, 11.5659233, -0d),
                new Poi("Stachuspassage -1", 48.1394991, 11.5659233, -1d),
                new Poi("München Hbf Lvl 0", 48.140203, 11.55972, 0d),
                new Poi("München Hbf Lvl 1", 48.140203, 11.55972, 1d),
                new Poi("Fürstenallee Eingang", 51.73210, 8.73502, 0d),
                new Poi("Fürstenallee F2", 51.73191690536, 8.73486839258, 2d),
                new Poi("southern mid point", 0, 0, Double.NaN),
                new Poi("northern mid point", 10, 0, Double.NaN),
                new Poi("northern east point", 10, 5, Double.NaN),
                new Poi("southern east point", 5, 5, Double.NaN),
                new Poi("west point", 5, -5, Double.NaN)
        );
    }

    private void runStachusTest() {
        Poi tmp = new Poi("München, tmp", 48.139029, 11.568700, -0d);
        singleTest(geocoding.getPoiByName("Karlsplatz"), tmp);
        singleTest(geocoding.getPoiByName("Stachuspassage -1"), tmp);
    }

    private void runFuTest() {
        singleTest(geocoding.getPoiByName("Fürstenallee Eingang"), geocoding.getPoiByName("Fürstenallee F2"));
        singleTest(geocoding.getPoiByName("Fürstenallee Eingang"), new Poi("Fürstenallee FU", 51.73168,8.73467, -1d));
    }

    private void runIssueTest() {
        Poi southern_mid_point = geocoding.getPoiByName("southern mid point");
        Poi northern_mid_point = geocoding.getPoiByName("northern mid point");
        Poi northern_east_point =  geocoding.getPoiByName("northern east point");
        Poi southern_east_point =  geocoding.getPoiByName("southern east point");
        Poi west_point =  geocoding.getPoiByName("west point");

        singleTest(west_point, southern_east_point, true);
        singleTest(west_point, northern_mid_point, true);
        singleTest(west_point, southern_mid_point, true);
    }

    private void singleTest(Poi a, Poi b) {
        singleTest(a, b, false);
    }

    private void singleTest(Poi a, Poi b, boolean edgeBased) {
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
