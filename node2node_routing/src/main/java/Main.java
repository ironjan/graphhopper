import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        String osmFile = args[0];
        String graphFolder = args[1];
        DirectoryDeleter.deleteDirectory(graphFolder);


        GraphHopper hopper = GraphLoader.get(osmFile, graphFolder);
        new Main();

        PathWrapper latLonRoute = new LatLonRouting(hopper)
                .getRoute(48.140375, 11.559291, 48.139029, 11.5687);
        PathPrinter.print(latLonRoute);

        PathWrapper nodeIdRoute = new NodeIdRouting(hopper).getRoute(121, 77);
        PathPrinter.print(nodeIdRoute);
    }


}
