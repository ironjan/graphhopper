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
        new Main(hopper).runLatLonTest(osmFile);

    }

    private void runLatLonTest(String osmFile) {
        if(osmFile.contains("stachus")){
            runStachusTest();
        }

        if(osmFile.contains("fuerstena")) {
            runFuTest();
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
        singleTest("entry-f2", 51.73210, 8.73502,51.73191690536, 8.73486839258, 0d, 2d);
        singleTest("entry-f0", 51.73210, 8.73502,51.73168,8.73467, 0d, -1d);
        singleTest("entry-f2", 51.73210, 8.73502,51.73191690536, 8.73486839258, 0d, 2d, true);
        singleTest("entry-f0", 51.73210, 8.73502,51.73168,8.73467, 0d, -1d, true);
    }

    private void runIssueTest(){
        singleTest("issue", 0,0,5,5,0,0);
    }
    private void singleTest(String run, double fromLat, double fromLon, double toLat, double toLon, double fromLvl, double toLvl, boolean edgeBased) {
        LOGGER.debug("Route {} from {},{},{} to {},{},{}. Edge based? {}", run, fromLat, fromLon, fromLvl, toLat, toLon, toLvl, edgeBased);

        LatLonRouting latLonRouting = new LatLonRouting(hopper);
        LowLevelRouting lowLevelRouting = new LowLevelRouting(hopper, edgeBased);

        PathWrapper route = latLonRouting.getRoute(fromLat, fromLon, toLat, toLon, fromLvl, toLvl);
//        PathPrinter.printSummary("GHRequest Routing", route);

//        PathPrinter.print("FootLevel All  EF", route);

        route = lowLevelRouting.getRoute(fromLat, fromLon, toLat, toLon, fromLvl, toLvl);
        PathPrinter.print("FootLevel LevelEF", route);
    }

    private void singleTest(String run, double fromLat, double fromLon, double toLat, double toLon, double fromLvl, double toLvl) {
        singleTest(run, fromLat, fromLon, toLat, toLon, fromLvl, toLvl, false);
    }

}
