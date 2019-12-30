package de.ironjan.graphhopper.geocoding;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Geocoding {
    private final HashMap<String, Poi> knownPois = new HashMap<>();
    private List<Geocoding> otherSources = new ArrayList<>();
    ;

    public Poi getByName(String name) {
        if (knownPois.containsKey(name)) {
            return knownPois.get(name);
        }

        for (Geocoding src: otherSources) {
            Poi potentialResult = src.getByName(name);
            if(potentialResult != null) {
                return potentialResult;
            }
        }

        return null;
    }
    
    public List<String> getAllNames(){
        List<String> allPoiNames = new ArrayList<>(size());

        allPoiNames.addAll(knownPois.keySet());

        for (Geocoding source : otherSources) {
            allPoiNames.addAll(source.getAllNames());
        }

        return allPoiNames;
    }

    private int size() {
        int size = knownPois.size();
        for (Geocoding g :
                otherSources) {
            size += g.size();
        }
        return size;
    }

    public void addSource(Geocoding geocoding) {
        otherSources.add(geocoding);
    }
    private void add(Poi poi) {
        knownPois.put(poi.getName(), poi);
    }

    private void addAll(Poi... pois) {
        for (Poi poi : pois) {
            add(poi);
        }
    }

    public static class Loader {
        public static Geocoding fromFile(String osmFile) throws IOException {
            GeocodingOsmReader osmReader = new GeocodingOsmReader();
            osmReader.setOsmFile(new File(osmFile));
            List<Poi> pois = osmReader.readGraph();
            Geocoding geocoding = new Geocoding();
            for (Poi poi : pois) {
                geocoding.add(poi);
            }
            return geocoding;
        }

        public static Geocoding fromPois(Poi... pois) {
            Geocoding geocoding = new Geocoding();
            geocoding.addAll(pois);
            return geocoding;
        }


    }
}
