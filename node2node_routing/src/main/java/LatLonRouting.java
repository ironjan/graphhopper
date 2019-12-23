import com.graphhopper.*;

import java.util.Locale;

public class LatLonRouting extends RoutingExample {
    public LatLonRouting(GraphHopper hopper) {
        super(hopper);
    }

    public PathWrapper getRoute(double fromLat, double fromLon, double toLat, double toLon) {
        return getRoute(fromLat, fromLon, toLat, toLon, 0d, 0d);
    }

    public PathWrapper getRoute(double fromLat, double fromLon, double toLat, double toLon, double fromLvl, double toLvl) {
// simple configuration of the request object, see the GraphHopperServlet classs for more possibilities.
        GHRequest req = new GHLevelRequest(fromLat, fromLon, toLat, toLon, Double.NaN, Double.NaN, fromLvl, toLvl)
                .setLocale(Locale.GERMAN);
        String weighting = req.getWeighting();
        GHResponse rsp = hopper.route(req);


// first check for errors
        if (rsp.hasErrors()) {
            // handle them!
            // rsp.getErrors()

            return null;
        }

// use the best path, see the GHResponse class for more possibilities.
        PathWrapper path = rsp.getBest();



        return path;

    }
}
