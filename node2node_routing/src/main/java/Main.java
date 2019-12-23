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
        singleTest(48.1403750, 11.5592910, 48.139029, 11.568700);
        singleTest(48.1387140, 11.5648430, 48.138832, 11.567654);
        singleTest(48.1384420, 11.5649660, 48.139029, 11.568700);

        // Start is exactly on top of a "Stachuspassage, 1. Untergeschoss" node
        singleTest(48.1394991, 11.5659233, 48.139029, 11.568700);
    }

    private void singleTest(double fromLat, double fromLon, double toLat, double toLon) {
        LOGGER.debug("Route from {},{} to {},{}.", fromLat, fromLon, toLat, toLon);

        LatLonRouting latLonRouting = new LatLonRouting(hopper);

        PathWrapper latLonRoute = latLonRouting.getRoute(fromLat, fromLon, toLat, toLon);
        PathPrinter.print(latLonRoute);
    }

}
