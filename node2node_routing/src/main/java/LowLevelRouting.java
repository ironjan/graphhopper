import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;

public class LowLevelRouting extends RoutingExample {
    public LowLevelRouting(GraphHopper hopper) {
        super(hopper);
    }

    public PathWrapper getRoute(double fromLat, double fromLon, double toLat, double toLon, double fromLvl, double toLvl) {
        return null;
    }
}
