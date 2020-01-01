package de.ironjan.graphhopper.levelexttest;

import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import de.ironjan.graphhopper.geocoding.Coordinate;
import de.ironjan.graphhopper.levelextension.LowLevelRouting;

public class EntranceRoutingWrapper {
    private final LowLevelRouting routing;

    public EntranceRoutingWrapper(GraphHopper hopper) {
        routing = new LowLevelRouting(hopper);
    }

    public PathWrapper route(Coordinate a, Coordinate b) {
        return routing.getRoute(a.lat, a.lon, b.lat, b.lon, a.lvl, b.lvl);
    }
}

