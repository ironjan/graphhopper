import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Node2NodeRouting {
    private static Logger LOGGER = LoggerFactory.getLogger(Node2NodeRouting.class);

    public static void main(String[] args) {
        String osmFile = args[0];
        String graphFolder = args[1];
        DirectoryDeleter.deleteDirectory(graphFolder);


        GraphHopper hopper = GraphLoader.get(osmFile, graphFolder);
        new Node2NodeRouting();

        PathWrapper route = new LatLonRouting(hopper)
                .getRoute(48.140375, 11.559291, 48.139029, 11.5687);

        PathPrinter.print(route);

    }


}
