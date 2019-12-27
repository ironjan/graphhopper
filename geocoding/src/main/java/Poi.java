import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Poi{
    final String name;
    final List<Coordinate> entrances = new ArrayList<>();

    public Poi(String name, Coordinate... entrances) {
        this.name = name;

        if(entrances.length<1) {
            throw new IllegalArgumentException("Must have at least one entrance.");
        }

        Collections.addAll(this.entrances, entrances);
    }

    public Poi(String name, double lat, double lon, double lvl){
        this(name, new Coordinate(lat,lon, lvl));
    }


}
