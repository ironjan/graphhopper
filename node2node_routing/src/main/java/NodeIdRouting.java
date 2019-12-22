import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import com.graphhopper.routing.*;
import com.graphhopper.routing.ch.CHRoutingAlgorithmFactory;
import com.graphhopper.routing.querygraph.QueryGraph;
import com.graphhopper.routing.template.ViaRoutingTemplate;
import com.graphhopper.routing.util.*;
import com.graphhopper.routing.weighting.AbstractWeighting;
import com.graphhopper.routing.weighting.FastestWeighting;
import com.graphhopper.routing.weighting.ShortestWeighting;
import com.graphhopper.routing.weighting.Weighting;
import com.graphhopper.storage.CHProfile;
import com.graphhopper.storage.index.QueryResult;
import com.graphhopper.util.DouglasPeucker;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.PathMerger;

import java.util.List;
import java.util.Locale;

public class NodeIdRouting extends RoutingExample {
    public NodeIdRouting(GraphHopper hopper) {
        super(hopper);
    }

    public PathWrapper getRoute(int from, int to) {
        FlagEncoder foot = EncodingManager.create(FlagEncoderFactory.FOOT).getEncoder(FlagEncoderFactory.FOOT);

        Weighting w = new WrappedShortestWeighting(foot);

        AbstractRoutingAlgorithm alg = new DijkstraBidirectionCH(hopper.getGraphHopperStorage().getCHGraph(), new FastestWeighting(foot));
        alg = new Dijkstra(hopper.getGraphHopperStorage().getBaseGraph(), w, TraversalMode.NODE_BASED);




        Path path = alg.calcPath(from, to);
        PathWrapper pathWrapper = new PathWrapper();

        pathWrapper.setWaypoints(path.calcPoints());

        return pathWrapper;
//        return alg.calcPath(from, to);
//        GHRequest req = new GHRequest(fromLat, fromLon, toLat, toLon).
//                setWeighting("fastest").
//                setVehicle("foot").
//                setLocale(Locale.GERMAN);
//        GHResponse rsp = hopper.route(req);

//        List<QueryResult> qResults = routingTemplate.lookup(points, encoder);
//
//
//        HintsMap hints = new HintsMap();
//        hints.put("weighting", "fastest");
//        hints.put("vehicle","foot");
//        RoutingAlgorithmFactory tmpAlgoFactory = hopper.getAlgorithmFactory(hints);
//        RoutingAlgorithmFactory chAlgoFactory = tmpAlgoFactory;
//        CHProfile chProfile = ((CHRoutingAlgorithmFactory) chAlgoFactory).getCHProfile();
//        queryGraph = QueryGraph.lookup(hopper.getGraphHopperStorage().getCHGraph(chProfile), qResults);

        /*



        tmode node
         AlgorithmOptions algoOpts = AlgorithmOptions.start().
                        algorithm("dijkstrabi").traversalMode(tMode).weighting(weighting).
                        maxVisitedNodes(2147483647).
                        hints(hints). //{weighting=fastest, vehicle=foot}
                        build();

                // do the actual route calculation !
                altPaths = routingTemplate.calcPaths(queryGraph, tmpAlgoFactory, algoOpts, encoder);

         */
//        return null;
    }
}
