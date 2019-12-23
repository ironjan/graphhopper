import com.graphhopper.routing.util.EdgeFilter;
import com.graphhopper.util.EdgeIteratorState;

public class FootLevelEdgeFilter implements EdgeFilter {
    @Override
    public boolean accept(EdgeIteratorState edgeState) {
        // Accept all for now
        return true;
    }
}
