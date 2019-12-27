import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;

import java.util.ArrayList;
import java.util.List;

public class PoiRoutingWrapper {
    private final EntranceRoutingWrapper entranceRouting;

    public PoiRoutingWrapper(GraphHopper hopper, boolean edgeBased) {
        entranceRouting = new EntranceRoutingWrapper(hopper, edgeBased);
    }

    public PathWrapper route(Poi a, Poi b) {
        List<Coordinate> entrancesOfA = a.coordinates;
        List<Coordinate> entrancesOfB = b.coordinates;

        ArrayList<PathWrapper> routes = new ArrayList<>(entrancesOfA.size() * entrancesOfB.size());

        for (Coordinate fromCoordinate : entrancesOfA) {
            for (Coordinate toCoordinate : entrancesOfB) {
                PathWrapper route = entranceRouting.route(fromCoordinate, toCoordinate);
                if(route != null) {
                    routes.add(route);
                }
            }
        }
        if(routes.size()>0){
            // fixme select best
            return routes.get(0);
        }
        return null;
    }
}
