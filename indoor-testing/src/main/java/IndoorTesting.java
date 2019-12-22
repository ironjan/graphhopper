
import com.carrotsearch.hppc.IntLongHashMap;
import com.carrotsearch.hppc.LongIntHashMap;
import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import com.graphhopper.coll.GHLongIntBTree;
import com.graphhopper.coll.LongIntMap;
import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.reader.osm.OSMReader;
import com.graphhopper.routing.AStar;
import com.graphhopper.routing.Dijkstra;
import com.graphhopper.routing.Path;
import com.graphhopper.routing.querygraph.QueryGraph;
import com.graphhopper.routing.util.*;
import com.graphhopper.routing.weighting.ShortestWeighting;
import com.graphhopper.routing.weighting.Weighting;
import com.graphhopper.storage.Graph;
import com.graphhopper.storage.NodeAccess;
import com.graphhopper.storage.index.LocationIndex;
import com.graphhopper.storage.index.QueryResult;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.Instruction;
import com.graphhopper.util.InstructionList;
import com.graphhopper.util.PointList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class IndoorTesting {

    private static Logger LOGGER = LoggerFactory.getLogger(IndoorTesting.class);

    public static void main(String[] args) throws IOException {
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
        EncodingManager foot = EncodingManager.create(FlagEncoderFactory.FOOT_INDOOR);
        hopper.setEncodingManager(foot);

        // now this can take minutes if it imports or a few seconds for loading
        // of course this is dependent on the area you import
        hopper.importOrLoad();

//        GraphBuilder gb = new GraphBuilder(em).setLocation("graphhopper_folder").setStore(true);


        Map<Double, List<Long>> levelToNodeIdMap1 = foo(hopper, hopper.getGraphHopperStorage().getBaseGraph(), foot);


        // fixme location index does not have floors
        LocationIndex index = hopper.getLocationIndex();

        // There are multiple pois close  on different floors
        double lat1 = 48.139136, lon1 = 11.5650924, level1 = -1.0;
        final double lat2 = 48.1391105, lon2 = 11.5647606, level2 = 0.0;
        double lat12 = 48.139, lon12 = 11.565, level12 = -0.5;
        double lat3 = 48.1390981, lon3 = 11.5653542, level3 = 0.0;

        // todo use foot filter instead
        EdgeFilter mEdgeFilter = new EdgeFilter() {

            @Override
            public boolean accept(EdgeIteratorState edgeState) {
                return false;
            }
        };
        QueryResult qr1 = index.findClosest(lat1, lon1, EdgeFilter.ALL_EDGES);
        int closestNode = qr1.getClosestNode();
        final NodeAccess nodeAccess = QueryGraph.lookup(hopper.getGraphHopperStorage(), qr1).getNodeAccess();
        LOGGER.debug("Node access 1: " + nodeAccess.getLatitude(closestNode) + ", " + nodeAccess.getLongitude(closestNode));


//        System.exit(0);

        final Graph baseGraph = hopper.getGraphHopperStorage().getBaseGraph();


        LOGGER.debug("Nodes by level:");

        Map<Double, List<Long>> levelToNodeIdMap = levelToNodeIdMap1;
        for (Double lvl : levelToNodeIdMap.keySet()) {
            List<Long> ids = levelToNodeIdMap.get(lvl);


            String s = ids.stream().map(new Function<Long, String>() {
                @Override
                public String apply(Long aLong) {
                    return aLong.toString();
                }
            }).collect(Collectors.joining(", "));

            LOGGER.debug("Got {} nodes on level {}: {}.", ids.size(), lvl, s);
        }

//        Graph baseGraph1 = hopper.getGraphHopperStorage().getBaseGraph();
//        List<Long> foooo = new ArrayList<>();
//        for (long i = 0; i < 999999999L; i++) {
//            int ghId = hopper.getGhIdOfOsmId(i);
//            if (ghId > 0) {
//                foooo.add(i);
//            }
//        }
//
        Weighting encoder = new ShortestWeighting(hopper.getEncodingManager().getEncoder("foot"));
        TraversalMode traversalMode = TraversalMode.NODE_BASED;
//        testFoooo(foooo, baseGraph, encoder, traversalMode, hopper);


        long[] snodes = {3274849523L, 3274849529L, 3274849537L, 3274849538L};
        long[] enodes = {3446999570L, 3446999568L, 3580872899L};

        for (long sn : snodes) {
            for (long en : enodes) {
                int startNode = hopper.getGhIdOfOsmId(sn);
                int endEdge = hopper.getGhIdOfOsmId(en);

                if (startNode < 0 || endEdge < 0) {
                    LOGGER.debug("osm {}->{}, gh {}->{} failed.", sn, en, startNode, endEdge);
                } else {
                    LOGGER.debug("Path from {} to {}: ...", startNode, endEdge);
                    Path path = new Dijkstra(baseGraph, encoder, traversalMode).calcPath(startNode, endEdge);
                    LOGGER.debug("Path from {} to {}: {}", startNode, endEdge, path);
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


                            LOGGER.debug("(" + latitude + "/" + longitude + ", " + ele1 + ") -> (" + latitude2 + "/" + longitude2 + ", " + ele2 + ") via " + edge.getName());
                        }

                        @Override
                        public void finish() {

                        }
                    });
                }
            }
        }


        LOGGER.debug("Stachus-Test:");

        GHRequest req = new GHRequest(48.138034, 11.564822, 48.138961, 11.566485)
//                .setWeighting("fastest")
//                .setVehicle("foot")
//                .setLocale("de-DE")
                ;
        GHResponse rsp = hopper.route(req);

        // use the best path, see the GHResponse class for more possibilities.
        PathWrapper bestPath = rsp.getBest();

        

// points, distance in meters and time in millis of the full path
        PointList pointList = bestPath.getPoints();
        double distance = bestPath.getDistance();
        long timeInMs = bestPath.getTime();

        InstructionList il = bestPath.getInstructions();
// iterate over every turn instruction
        for (Instruction instruction : il) {
            LOGGER.debug("{}", instruction);
        }
        System.exit(0);

        LOGGER.debug("Some nodes with ele!=0");
        NodeAccess allNodes = baseGraph.getNodeAccess();

        for (int i = 0; i < baseGraph.getNodes(); i++) {
            double latitude = allNodes.getLatitude(i);
            double longitude = allNodes.getLongitude(i);
            double ele = allNodes.getElevation(i);

            if (ele != 0.0) {
                LOGGER.debug("Node {}: {}, {}, {} (lat, lon, ele)", i, latitude, longitude, ele);
            }
        }


        for (int i = -10; i < 2; i++) {
            int count = countNodesBetweenElevation(baseGraph, i - 0.4, i + 0.50);
            LOGGER.debug("There are {} on level ~{}.", count, i);
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

    private static void testFoooo(List<Long> foooo, Graph baseGraph, Weighting encoder, TraversalMode traversalMode, GraphHopper hopper) {
        for (int i = 0; i < foooo.size(); i++) {
            for (int j = 0; j < foooo.size(); j++) {
                int startNode = hopper.getGhIdOfOsmId(i);
                int endEdge = hopper.getGhIdOfOsmId(j);
                Path path = new Dijkstra(baseGraph, encoder, traversalMode).calcPath(startNode, endEdge);

                Path path2 = new Dijkstra(baseGraph, encoder, traversalMode).calcPath(i, j);

                if(path.getDistance() > 0 || path2.getDistance() > 0){
                    LOGGER.debug("Path from {} to {} (GH: {} to {}): {}", i, j, startNode, endEdge, path);
                    LOGGER.debug("Path2 from {} to {} (GH: {} to {}): {}", i, j, startNode, endEdge, path);
                }
            }
        }
    }

    private static Map<Double, List<Long>> foo(GraphHopper hopper, Graph baseGraph, EncodingManager foot) {
        NodeAccess nodeAccess1 = hopper.getGraphHopperStorage().getNodeAccess();
        int maxn = hopper.getGraphHopperStorage().getNodes();
        Map<Double, List<Long>> levelToNodeIdMap1 = hopper.getLevelToNodeIdMap();
        Map<Long, Double> idToLevel = new HashMap<>(maxn);

        for (Double lvl : levelToNodeIdMap1.keySet()) {
            List<Long> lvlNodes = levelToNodeIdMap1.get(lvl);
            for (Long id : lvlNodes) {
                idToLevel.put(id, lvl);
            }
        }

        int l2Index = 0;
        int lM1Index = 0;
        List<Long> l2Nodes = levelToNodeIdMap1.get(2.0);
        List<Long> lm1Nodes = levelToNodeIdMap1.get(-1.0);

        for (Long i : l2Nodes) {
            for (Long j : lm1Nodes) {
                bar(baseGraph, foot, i.intValue(), j.intValue(), l2Nodes, lm1Nodes);
            }
        }
        return levelToNodeIdMap1;
    }

    private static void bar(Graph baseGraph, EncodingManager foot, int l2Index, int lM1Index, List<Long> l2Nodes, List<Long> lm1Nodes) {
        try {
            Long firstLEvel2Node = l2Nodes.get(l2Index);
            Long firstLevelMinus1Node = lm1Nodes.get(lM1Index);

            AllEdgesIterator allEdges = baseGraph.getAllEdges();
            int startEdge = firstLEvel2Node.intValue();
            int endEdge = firstLevelMinus1Node.intValue();

            ShortestWeighting weighting = new ShortestWeighting(foot.getEncoder("foot"));


            LOGGER.debug("Computing path from {} to {} in A* and Dijkstra.", startEdge, endEdge);
            Path aSTarPAth = new AStar(baseGraph, weighting, TraversalMode.NODE_BASED).calcPath(startEdge, endEdge);
            Path dijkstraPath = new Dijkstra(baseGraph, weighting, TraversalMode.NODE_BASED).calcPath(startEdge, endEdge);

            LOGGER.debug("Final path lengths:");
            LOGGER.debug("{}", aSTarPAth);
            LOGGER.debug("{}", dijkstraPath);
        } catch (Exception e) {
            /* ignore */
        }
    }

    private static int countNodesBetweenElevation(Graph graph, double eleMin, double eleMax) {
        return findNodesBetweenElevations(graph, eleMin, eleMax).size();
    }

    private static List<Integer> findNodesBetweenElevations(Graph graph, double eleMin, double eleMax) {
        NodeAccess nodeAccess = graph.getNodeAccess();
        List<Integer> foundNodeIds = new ArrayList<>();

        for (int i = 0; i < graph.getNodes(); i++) {
            double ele = nodeAccess.getElevation(i);
            if (eleMin <= ele && ele <= eleMax) {
                foundNodeIds.add(i);
            }
        }

        return foundNodeIds;
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
