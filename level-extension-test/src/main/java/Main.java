import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import com.graphhopper.storage.GraphHopperStorage;
import com.graphhopper.storage.NodeAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main {
    private static Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private final GraphHopper hopper;
    private Geocoding geocoding;

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
        Geocoding fromPois = Geocoding.Loader.fromPois(
                new Poi("Karlsplatz", 48.1394991, 11.5659233, -0d),
                new Poi("Stachuspassage -1", 48.1394991, 11.5659233, -1d),
                new Poi("München Hbf Lvl 0", 48.140203, 11.55972, 0d),
                new Poi("München Hbf Lvl 1", 48.140203, 11.55972, 1d),
                new Poi("southern mid point", 0, 0, Double.NaN),
                new Poi("northern mid point", 10, 0, Double.NaN),
                new Poi("northern east point", 10, 5, Double.NaN),
                new Poi("southern east point", 5, 5, Double.NaN),
                new Poi("west point", 5, -5, Double.NaN),
                new Poi("Southwest Building", 51.701, 8.7287, 0),
                new Poi("East Block", 51.730, 8.7855, 0),
                new Poi("Center Church", 51.717, 8.755, 0),
                new Poi("Northwest Building", 51.735, 8.724, 0),
                new Poi("South Point", 51.6965, 8.7759, 0),

                new Poi("Fürstenallee Eingang",
                        51.73210, 8.73502, 0d),
                new Poi("Fürstenallee F2",
                        51.73191690536, 8.73486839258, 2d),
                new Poi("FU.343",
                        51.7317734, 8.7341487, -1),
                new Poi("FU.Treppenhaus",
                        51.718908, 8.7350631, -1),
                new Poi("FU.511",
                        new Coordinate(51.7318062, 8.7345019, -1),
                        new Coordinate(51.7318080, 8.7343946, -1)),
                new Poi("FU.237", 51.7314104, 8.7350023, -1)
        );
        Geocoding fromFile = null;
        try {
            fromFile = Geocoding.Loader.fromFile(osmFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.geocoding = new Geocoding();
        geocoding.addSource(fromFile);
        geocoding.addSource(fromPois);

        if (osmFile.contains("stachus")) {
            runStachusTest();
        }

        if (osmFile.contains("upb-fu")) {
            runFuTest();
        }

        if (osmFile.contains("issue")) {
            runIssueTest();
        }

        if (osmFile.contains("room")) {
            singleTest("Just a test", 51.7260712, 8.7387656, 51.6995871, 8.7423705, 0, 0);
        }

        if (osmFile.contains("place_test")) {
            place_test();
        }

        if (osmFile.contains("floor")) {
            runFloorTest();
        }

        if (osmFile.contains("area_test")) {
            String[] poiNames = {"Southwest Building", "East Block", "Center Church", "Northwest Building", "South Point"};

            for (String a : poiNames) {
                for (String b : poiNames) {
                    singleTest(this.geocoding.getByName(a), this.geocoding.getByName(b));
                }
            }
        }
    }

    private void place_test() {
        singleTest(new Coordinate("WP", 0, 0, 0), new Coordinate("NP", 2, 2, 0), true);
        singleTest(new Coordinate("WP", 0, 0, 0), new Coordinate("EP", 0, 4, 0), true);
        printTowerNodes();
    }

    private void printTowerNodes() {
        GraphHopperStorage graph = hopper.getGraphHopperStorage();
        NodeAccess nodeAccess = graph.getNodeAccess();
        for (int i = 0; i < graph.getNodes(); i++) {
            LoggerFactory.getLogger(Main.class.getName()).debug("Has tower node: {},{}", nodeAccess.getLongitude(i), nodeAccess.getLatitude(i));
        }
    }


    private void runStachusTest() {
        Poi tmp = new Poi("München, tmp", 48.139029, 11.568700, -0d);
        singleTest(geocoding.getByName("Karlsplatz"), tmp);
        singleTest(geocoding.getByName("Stachuspassage -1"), tmp);
    }

    private void runFuTest() {
//        singleTest(geocoding.getPoiByName("Fürstenallee Eingang"), geocoding.getPoiByName("Fürstenallee F2"));
//        singleTest(geocoding.getPoiByName("Fürstenallee Eingang"), new Poi("Fürstenallee FU", 51.73168, 8.73467, -1d));
        singleTest(geocoding.getByName("FU.Treppenhaus"), geocoding.getByName("FU.343"));
        singleTest(geocoding.getByName("FU.Treppenhaus"), geocoding.getByName("FU.511"));
        singleTest(geocoding.getByName("FU.Treppenhaus"), geocoding.getByName("FU.237"));
        singleTest(geocoding.getByName("FU.237"), geocoding.getByName("FU.511"));
    }

    private void runFloorTest() {
        printTowerNodes();

        Coordinate ground = new Coordinate("Mid Ground floor", 51.733, 8.7473, 0d);
        Coordinate minus1 = new Coordinate("Mid Lower floor", 51.733, 8.7473, -1d);

        singleTest(ground, minus1);
    }

    private void runIssueTest() {
        Poi southern_mid_point = geocoding.getByName("southern mid point");
        Poi northern_mid_point = geocoding.getByName("northern mid point");
        Poi northern_east_point = geocoding.getByName("northern east point");
        Poi southern_east_point = geocoding.getByName("southern east point");
        Poi west_point = geocoding.getByName("west point");

        singleTest(west_point, southern_east_point, true);
        singleTest(west_point, northern_mid_point, true);
        singleTest(west_point, southern_mid_point, true);
    }

    private void singleTest(Poi A, Poi B) {
        singleTest(A, B, true);
    }

    private void singleTest(Poi A, Poi B, boolean edgeBased) {
        PoiRoutingWrapper routingWrapper = new PoiRoutingWrapper(hopper, edgeBased);
        LOGGER.debug("POI Routing from {} to {}", A.name, B.name);
        PathWrapper route = routingWrapper.route(A, B);
        PathPrinter.print("FootLevel LevelEF", route);

    }

    private void singleTest(Coordinate a, Coordinate b) {
        singleTest(a, b, false);
    }

    private void singleTest(Coordinate a, Coordinate b, boolean edgeBased) {
        String msg = String.format("%s to %s", a.toString(), b.toString());
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
