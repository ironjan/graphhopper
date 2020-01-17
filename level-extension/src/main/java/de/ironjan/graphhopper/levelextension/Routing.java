package de.ironjan.graphhopper.levelextension;

import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import de.ironjan.graphhopper.levelextension.routing.LowLevelRouting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Routing {
    private final LowLevelRouting lowLevelRouting;

    public Routing(GraphHopper hopper) {
        this.lowLevelRouting = new LowLevelRouting(hopper);
    }

    private Logger logger = LoggerFactory.getLogger(Routing.class);

    public PathWrapper route(Coordinate from, Coordinate to) {
        logger.debug("Requested route from {} to {}. Delegating to lat,lon,lvl method.", from, to);
        return route(from.lat, from.lon, from.lvl, to.lat, to.lon, to.lvl);
    }

    public PathWrapper route(double fromLat, double fromLon, double fromLvl, double toLat, double toLon, double toLvl) {
        logger.debug("Requested route from {},{},{} to {},{},{}. Delegating to LowLevelRouting.", fromLat, fromLon, fromLvl, toLat, toLon, toLvl);
        return lowLevelRouting.getRoute(fromLat, fromLon, fromLvl, toLat, toLon, toLvl);
    }



}
