
import com.graphhopper.GHRequest;
import com.graphhopper.GraphHopper;
import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.routing.Dijkstra;
import com.graphhopper.routing.Path;
import com.graphhopper.routing.querygraph.QueryGraph;
import com.graphhopper.routing.util.*;
import com.graphhopper.routing.weighting.ShortestWeighting;
import com.graphhopper.routing.weighting.TurnWeighting;
import com.graphhopper.routing.weighting.Weighting;
import com.graphhopper.storage.Graph;
import com.graphhopper.storage.GraphBuilder;
import com.graphhopper.storage.GraphStorage;
import com.graphhopper.storage.NodeAccess;
import com.graphhopper.storage.index.LocationIndex;
import com.graphhopper.storage.index.QueryResult;
import com.graphhopper.util.EdgeIteratorState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.function.Consumer;

public class IndoorTesting {

    private static Logger LOGGER;

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: IndoorTesting <input.osm> <gh-folder>");
            System.exit(1);
        }


        deleteDirectory(new File(args[1]));

        // create one GraphHopper instance
        GraphHopper hopper = new GraphHopperOSM().forServer();
        hopper.setDataReaderFile(args[0]);

        // where to store graphhopper files?
        hopper.setGraphHopperLocation(args[1]);
        hopper.setElevation(true);
        EncodingManager foot = EncodingManager.create("foot");
        hopper.setEncodingManager(foot);

        // now this can take minutes if it imports or a few seconds for loading
        // of course this is dependent on the area you import
        hopper.importOrLoad();

//        GraphBuilder gb = new GraphBuilder(em).setLocation("graphhopper_folder").setStore(true);



        // fixme location index does not have floors
        LocationIndex index = hopper.getLocationIndex();

        // There are multiple pois close  on different floors
        double lat1 = 48.139136, lon1= 11.5650924, level1= -1.0;
        final double lat2 = 48.1391105, lon2= 11.5647606, level2 = 0.0;
        double lat12 = 48.139, lon12=11.565, level12 = -0.5;
        double lat3 = 48.1390981, lon3 = 11.5653542, level3= 0.0;

        // todo use foot filter instead
        QueryResult qr1 = index.findClosest(lat1, lon1, EdgeFilter.ALL_EDGES);
        int closestNode = qr1.getClosestNode();
        final NodeAccess nodeAccess = QueryGraph.lookup(hopper.getGraphHopperStorage(), qr1).getNodeAccess();
        LOGGER = LoggerFactory.getLogger(IndoorTesting.class);
        LOGGER.debug("Node access 1: " + nodeAccess.getLatitude(closestNode) + ", " + nodeAccess.getLongitude(closestNode));


        final Graph baseGraph = hopper.getGraphHopperStorage().getBaseGraph();
        Weighting encoder = new ShortestWeighting(hopper.getEncodingManager().getEncoder("foot"));
        TraversalMode traversalMode = TraversalMode.NODE_BASED;


        Path path = new Dijkstra(baseGraph, encoder, traversalMode).calcPath(0,145);
        LOGGER.debug("PAth: "+ path);
        path.forEveryEdge(new Path.EdgeVisitor() {
            @Override
            public void next(EdgeIteratorState edge, int index, int prevEdgeId) {
                LOGGER.debug("Iterating over edge #" + index);
                double latitude = baseGraph.getNodeAccess().getLatitude(edge.getBaseNode());
                double longitude = baseGraph.getNodeAccess().getLongitude(edge.getBaseNode());

                double latitude2 = baseGraph.getNodeAccess().getLatitude(edge.getAdjNode());
                double longitude2 = baseGraph.getNodeAccess().getLongitude(edge.getAdjNode());

                double ele1 = baseGraph.getNodeAccess().getElevation(edge.getBaseNode());
                double ele2 = baseGraph.getNodeAccess().getElevation(edge.getAdjNode());

                LOGGER.debug("("+latitude+", "+longitude+", " + ele1 +") -> ("+latitude2+", "+longitude2+", " + ele2+")");
            }

            @Override
            public void finish() {

            }
        });


        LOGGER.debug("");
        NodeAccess allNodes = baseGraph.getNodeAccess();
        for(int i=0; i<baseGraph.getNodes(); i++) {
            double latitude = allNodes.getLatitude(i);
            double longitude = allNodes.getLongitude(i);
            double ele = allNodes.getElevation(i);

            if(ele != 0.0) {
                LOGGER.debug("Node {}: {}, {}, {} (lat, lon, ele)", i, latitude, longitude, ele);
            }
        }
//path.calcEdges().forEach(new Consumer<EdgeIteratorState>() {
//    @Override
//    public void accept(EdgeIteratorState edgeIteratorState) {
//        NodeAccess nodeAccess1 = baseGraph.getNodeAccess();
//
//        double elat1 = nodeAccess1.getLatitude(edge.getBaseNode());
//        double elon1 = nodeAccess1.getLongitude(edge.getBaseNode());
//        double elat2 = nodeAccess1.getLatitude(edge.getAdjNode());
//        double elon2 = nodeAccess1.getLongitude(edge.getAdjNode());
//
//        LoggerFactory.getLogger("EdgeVisitor:  " + elat1 + ", " + elon1 + " -> " + elat2 + ", " + elon2);
//
//    }
//});

// simple configuration of the request object, see the GraphHopperServlet classs for more possibilities.
//        GHRequest req = new GHRequest(latFrom, lonFrom, latTo, lonTo).
//                setWeighting("fastest").
//                setVehicle("car").
//                setLocale(Locale.US);
//        GHResponse rsp = hopper.route(req);
//
//// first check for errors
//        if(rsp.hasErrors()) {
//            // handle them!
//            // rsp.getErrors()
//            return;
//        }
//
//// use the best path, see the GHResponse class for more possibilities.
//        PathWrapper path = rsp.getBest();
//
//// points, distance in meters and time in millis of the full path
//        PointList pointList = path.getPoints();
//        double distance = path.getDistance();
//        long timeInMs = path.getTime();
//
//        InstructionList il = path.getInstructions();
//// iterate over every turn instruction
//        for(Instruction instruction : il) {
//            instruction.getDistance();
//   ...
//        }
//
//// or get the json
//        List<Map<String, Object>> iList = il.createJson();
//
//// or get the result as gpx entries:
//        List<GPXEntry> list = il.createGPXList();
    }


    // https://www.baeldung.com/java-delete-directory
    private static boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }
}
