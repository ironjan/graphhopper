import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
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
        new Main(hopper).runLatLonTest();

    }

    private void runLatLonTest() {
//        singleTest(0,48.1403750, 11.5592910, 48.139029, 11.568700, 0d, 0d);
//        singleTest(1,48.1387140, 11.5648430, 48.138832, 11.567654, 0d, 0d);
//        singleTest(2,48.1384420, 11.5649660, 48.139029, 11.568700, 0d, 0d);

        // Start is exactly on top of a "Stachuspassage, 1. Untergeschoss" node
        singleTest(3,48.1394991, 11.5659233, 48.139029, 11.568700, -0d, 0d);
        singleTest(4,48.1394991, 11.5659233, 48.139029, 11.568700, -1d, 0d);
    }

    private void singleTest(int run, double fromLat, double fromLon, double toLat, double toLon, double fromLvl, double toLvl) {
        LOGGER.debug("Route #{} Route from {},{},{} to {},{},{}.", run, fromLat, fromLon, fromLvl, toLon, fromLvl, toLvl);

        LatLonRouting latLonRouting = new LatLonRouting(hopper);
        LowLevelRouting lowLevelRouting = new LowLevelRouting(hopper, false);
        LowLevelRouting allEdgesLowLevelRouting = new LowLevelRouting(hopper, true);

        PathWrapper route = latLonRouting.getRoute(fromLat, fromLon, toLat, toLon, fromLvl, toLvl);
//        PathPrinter.printSummary("GHRequest Routing", route);

        route = allEdgesLowLevelRouting.getRoute(fromLat, fromLon, toLat, toLon, fromLvl, toLvl);
//        PathPrinter.print("FootLevel All  EF", route);

        route = lowLevelRouting.getRoute(fromLat, fromLon, toLat, toLon, fromLvl, toLvl);
        PathPrinter.print("FootLevel LevelEF", route);
    }

}
