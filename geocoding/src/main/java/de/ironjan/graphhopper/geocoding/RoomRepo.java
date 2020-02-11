package de.ironjan.graphhopper.geocoding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RoomRepo {
    private final HashMap<String, Poi> knownPois = new HashMap<>();
    private List<Geocoding> otherSources = new ArrayList<>();
    private Logger logger = LoggerFactory.getLogger(Geocoding.class);
    ;

    public Poi getByName(String name) {
        logger.debug("Retrieving information about '{}'.", name);

        if (knownPois.containsKey(name)) {
            Poi poi = knownPois.get(name);
            logger.debug("Found POI: {}.", poi);
            return poi;
        }

        logger.debug("Delegating to other sources, if any.");
        for (Geocoding src: otherSources) {
            Poi potentialResult = src.getByName(name);
            if(potentialResult != null) {
                return potentialResult;
            }
        }

        logger.debug("No POI with name {} found.", name);

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

        private static Logger logger = LoggerFactory.getLogger(Loader.class);

        public static Geocoding fromFile(String osmFile) throws IOException {
            logger.info("Initializing Geocoding class from file {}.", osmFile);

            GeocodingOsmReader osmReader = new GeocodingOsmReader();
            osmReader.setOsmFile(new File(osmFile));
            List<Poi> pois = osmReader.readGraph();

            logger.debug("Read file.");

            Geocoding geocoding = new Geocoding();
            for (Poi poi : pois) {
                geocoding.add(poi);
            }

            logger.info("Loaded geocoding data with {} pois.", geocoding.size());

            return geocoding;
        }

        public static Geocoding fromPois(Poi... pois) {
            Geocoding geocoding = new Geocoding();
            geocoding.addAll(pois);
            return geocoding;
        }


    }
}
