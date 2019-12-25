import java.util.HashMap;

public class GeocodingSample {
    private final HashMap<String, Poi> knownPois = new HashMap<>();


    public Poi getPoiByName(String name){
        if(knownPois.containsKey(name)){
            return knownPois.get(name);
        }
        return null;
    }

    public void add(Poi poi){
        knownPois.put(poi.name, poi);
    }

    public void addAll(Poi... pois){
        for (Poi poi : pois) {
            add(poi);
        }
    }
}
