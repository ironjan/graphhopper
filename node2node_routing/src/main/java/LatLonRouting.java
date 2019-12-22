import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;

import java.util.Locale;

public class LatLonRouting {
    private GraphHopper hopper;

    public LatLonRouting(GraphHopper hopper) {
        this.hopper = hopper;
    }

    public PathWrapper getRoute(double fromLat, double fromLon, double toLat, double toLon) {

// simple configuration of the request object, see the GraphHopperServlet classs for more possibilities.
        GHRequest req = new GHRequest(fromLat, fromLon, toLat, toLon).
                setWeighting("fastest").
                setVehicle("foot").
                setLocale(Locale.GERMAN);
        GHResponse rsp = hopper.route(req);


// first check for errors
        if(rsp.hasErrors()) {
            // handle them!
            // rsp.getErrors()

            return null;
        }

// use the best path, see the GHResponse class for more possibilities.
        PathWrapper path = rsp.getBest();

        return path;

    }
}
