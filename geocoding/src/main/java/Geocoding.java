import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class Geocoding {
    private final HashMap<String, Poi> knownPois = new HashMap<>();

    public Poi getByName(String name) {
        if (knownPois.containsKey(name)) {
            return knownPois.get(name);
        }
        return null;
    }

    private void add(Poi poi) {
        knownPois.put(poi.name, poi);
    }

    private void addAll(Poi... pois) {
        for (Poi poi : pois) {
            add(poi);
        }
    }

    public static class Loader {
        public Geocoding load(String osmFile) throws IOException {
            GeocodingOsmReader osmReader = new GeocodingOsmReader();
            osmReader.setOsmFile(new File(osmFile));
            List<Poi> pois = osmReader.readGraph();
            Geocoding geocoding = new Geocoding();
            for (Poi poi : pois) {
                geocoding.add(poi);
            }
            return geocoding;
        }
    }
}
