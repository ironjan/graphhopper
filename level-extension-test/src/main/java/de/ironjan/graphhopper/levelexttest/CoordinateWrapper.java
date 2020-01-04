package de.ironjan.graphhopper.levelexttest;

import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import de.ironjan.graphhopper.extensions_core.Coordinate;
import de.ironjan.graphhopper.levelextension.routing.LowLevelRouting;

public class CoordinateWrapper {
    private final LowLevelRouting routing;

    public CoordinateWrapper(GraphHopper hopper) {
        routing = new LowLevelRouting(hopper);
    }

    public PathWrapper route(Coordinate a, Coordinate b) {
        return routing.getRoute(a.lat, a.lon, b.lat, b.lon, a.lvl, b.lvl);
    }
}

