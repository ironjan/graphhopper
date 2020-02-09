package de.ironjan.graphhopper.levelextension.routing;

import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import com.graphhopper.routing.Dijkstra;
import com.graphhopper.routing.Path;
import com.graphhopper.routing.querygraph.QueryGraph;
import com.graphhopper.routing.util.EdgeFilter;
import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.routing.util.TraversalMode;
import com.graphhopper.storage.Graph;
import com.graphhopper.storage.index.QueryResult;
import com.graphhopper.util.Translation;
import com.graphhopper.util.TranslationMap;
import de.ironjan.graphhopper.levelextension.graph.FootLevelEdgeFilter;
import de.ironjan.graphhopper.levelextension.GraphLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Locale;

public class LowLevelRouting {

    private FlagEncoder encoder;
    private WrappedShortestWeighting weighting;

    private final GraphHopper hopper;
    private Logger logger = LoggerFactory.getLogger(LowLevelRouting.class.getName());
    private TranslationMap trMap;

    public LowLevelRouting(GraphHopper hopper) {
        super();
         this.hopper = hopper;
        encoder = GraphLoader.getEncodingManager().getEncoder("foot_level");
        weighting = new WrappedShortestWeighting(encoder);
        trMap = new TranslationMap().doImport();
    }

    public PathWrapper getRoute(double fromLat, double fromLon, double fromLvl, double toLat, double toLon, double toLvl) {
        logger.debug("Requested route from {},{},{} to {},{},{}.", fromLat, fromLon, fromLvl, toLat, toLon, toLvl);

        EdgeFilter fromFilter = new FootLevelEdgeFilter(encoder, fromLvl);
        EdgeFilter toFilter = new FootLevelEdgeFilter(encoder, toLvl);

        logger.debug("Constructed fromFilter and toFilter for finding closest edges/nodes.");

        QueryResult fromQr = hopper.getLocationIndex().findClosest(fromLat, fromLon, fromFilter);
        QueryResult toQr = hopper.getLocationIndex().findClosest(toLat, toLon, toFilter);

        logger.debug("QueryResults for finding closest: fromQr = {}, toQr = {}.", fromQr, toQr);

        boolean hasInvalidQueryResult = !(fromQr.isValid() && toQr.isValid());
        if(hasInvalidQueryResult) {
            logger.warn("At least one of the query results is invalid. Returning null.");
            // TODO improve this and drop level restriction for a second attempt?
            return null;
        }

        ArrayList<QueryResult> qrs = new ArrayList<>();
        qrs.add(fromQr);
        qrs.add(toQr);

        Graph graph = hopper.getGraphHopperStorage().getBaseGraph();
        QueryGraph queryGraph = QueryGraph.lookup(graph, qrs);


        TraversalMode traversalMode = TraversalMode.NODE_BASED;

        logger.debug("Constructed query graph for traversal.");

        Path path = new Dijkstra(queryGraph, weighting, traversalMode)
                .calcPath(fromQr.getClosestNode(), toQr.getClosestNode());

        logger.debug("Instantiated Dijkstra and computed the following path: {}.", path);

        ArrayList<Path> paths = new ArrayList<>();
        paths.add(path);

        PathWrapper pathWrapper = new PathWrapper();
        PathMerger merger = new PathMerger(graph, weighting);
        merger.setSimplifyResponse(false);
        Translation tr = trMap.getWithFallBack(Locale.GERMAN);
        merger.doWork(pathWrapper, paths, GraphLoader.getEncodingManager(), tr);

        logger.debug("Wrapped the raw path via merger and added instructions: {}.", pathWrapper);

        return pathWrapper;
    }
}
