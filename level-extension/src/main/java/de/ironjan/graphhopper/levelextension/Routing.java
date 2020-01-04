package de.ironjan.graphhopper.levelextension;

import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import de.ironjan.graphhopper.extensions_core.Coordinate;
import de.ironjan.graphhopper.levelextension.routing.LowLevelRouting;

public abstract class Routing {
    private final LowLevelRouting lowLevelRouting;

    public Routing(GraphHopper hopper) {
        this.lowLevelRouting = new LowLevelRouting(hopper);
    }

    public PathWrapper route(Coordinate from, Coordinate to) {
        return route(from.lat, from.lon, from.lvl, to.lat, to.lon, to.lvl);
    }

    public PathWrapper route(double fromLat, double fromLon, double fromLvl, double toLat, double toLon, double toLvl) {
        return lowLevelRouting.getRoute(fromLat, fromLon, fromLvl, toLat, toLon, toLvl);
    }



}
