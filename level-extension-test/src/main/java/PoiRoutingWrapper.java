import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PoiRoutingWrapper {
    private final EntranceRoutingWrapper entranceRouting;

    public PoiRoutingWrapper(GraphHopper hopper) {
        entranceRouting = new EntranceRoutingWrapper(hopper);
    }

    public PathWrapper route(Poi a, Poi b) {
        List<Coordinate> entrancesOfA = a.entrances;
        List<Coordinate> entrancesOfB = b.entrances;

        ArrayList<PathWrapper> routes = new ArrayList<>(entrancesOfA.size() * entrancesOfB.size());

        for (Coordinate fromCoordinate : entrancesOfA) {
            for (Coordinate toCoordinate : entrancesOfB) {
                PathWrapper route = entranceRouting.route(fromCoordinate, toCoordinate);
                if(route != null && !route.hasErrors()) {
                    routes.add(route);
                }
            }
        }


        Logger logger = LoggerFactory.getLogger(PoiRoutingWrapper.class.getName());
        logger.debug("Found {} routes from {} to {}",routes.size(), a.name, b.name);
        for (PathWrapper p : routes) {
            logger.debug("{}m -- {}",p.getDistance(), p);
        }

        Collections.sort(routes, new Comparator<PathWrapper>() {
            @Override
            public int compare(PathWrapper o1, PathWrapper o2) {
                return Double.compare(o1.getDistance(), o2.getDistance());
            }
        });
        if(routes.size()>0){
            // fixme select best
            return routes.get(0);
        }
        return null;
    }
}
