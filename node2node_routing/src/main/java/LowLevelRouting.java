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
import com.graphhopper.util.PathMerger;
import com.graphhopper.util.Translation;
import com.graphhopper.util.TranslationMap;

import java.util.ArrayList;
import java.util.Locale;

public class LowLevelRouting extends RoutingExample {

    private FlagEncoder encoder;
    private WrappedShortestWeighting weighting;
    private boolean useFilter;

    public LowLevelRouting(GraphHopper hopper, boolean useFilter) {
        super(hopper);
        this.useFilter = useFilter;
        encoder = GraphLoader.getEncodingManager().getEncoder("foot_level");
        weighting = new WrappedShortestWeighting(encoder);
    }

    public PathWrapper getRoute(double fromLat, double fromLon, double toLat, double toLon, double fromLvl, double toLvl) {
        EdgeFilter fromFilter;
        EdgeFilter toFilter;
        if(useFilter){
            fromFilter = EdgeFilter.ALL_EDGES;
            toFilter = EdgeFilter.ALL_EDGES;
        }else {
            fromFilter = new FootLevelEdgeFilter(encoder, fromLvl);
            toFilter= new FootLevelEdgeFilter(encoder, toLvl);
        }

        QueryResult fromQr = hopper.getLocationIndex().findClosest(fromLat, fromLon, fromFilter);
        QueryResult toQr = hopper.getLocationIndex().findClosest(toLat, toLon, toFilter);

        ArrayList<QueryResult> qrs = new ArrayList<>();
        qrs.add(fromQr);
        qrs.add(toQr);

        Graph graph = hopper.getGraphHopperStorage().getBaseGraph();
        QueryGraph queryGraph = QueryGraph.lookup(graph, qrs);


        Path path = new Dijkstra(queryGraph, weighting, TraversalMode.NODE_BASED)
                .calcPath(fromQr.getClosestNode(), toQr.getClosestNode());

        ArrayList<Path> paths = new ArrayList<>();
        paths.add(path);

        PathWrapper pathWrapper = new PathWrapper();
        PathMerger merger = new PathMerger(graph, weighting);
        merger.setSimplifyResponse(false);
        final TranslationMap trMap = new TranslationMap().doImport();
        Translation tr = trMap.getWithFallBack(Locale.GERMAN);
        merger.doWork(pathWrapper, paths, GraphLoader.getEncodingManager(), tr);

        return pathWrapper;
    }
}
