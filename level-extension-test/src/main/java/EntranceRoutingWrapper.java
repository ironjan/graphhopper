import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;

public class EntranceRoutingWrapper {
    private final LowLevelRouting routing;

    public EntranceRoutingWrapper(GraphHopper hopper, boolean edgeBased) {
        routing = new LowLevelRouting(hopper, edgeBased);
    }

    public PathWrapper route(Coordinate a, Coordinate b) {
        return routing.getRoute(a.lat, a.lon, b.lat, b.lon, a.lvl, b.lvl);
    }
}

