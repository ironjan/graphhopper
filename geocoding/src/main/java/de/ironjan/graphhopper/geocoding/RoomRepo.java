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
    private List<RoomRepo> otherSources = new ArrayList<>();
    private Logger logger = LoggerFactory.getLogger(RoomRepo.class);
    ;

    public Poi getByName(String name) {
        logger.debug("Retrieving information about '{}'.", name);

        if (knownPois.containsKey(name)) {
            Poi poi = knownPois.get(name);
            logger.debug("Found POI: {}.", poi);
            return poi;
        }

        logger.debug("Delegating to other sources, if any.");
        for (RoomRepo src: otherSources) {
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

        for (RoomRepo source : otherSources) {
            allPoiNames.addAll(source.getAllNames());
        }

        return allPoiNames;
    }

    private int size() {
        int size = knownPois.size();
        for (RoomRepo g :
                otherSources) {
            size += g.size();
        }
        return size;
    }

    public void addSource(RoomRepo repo) {
        otherSources.add(repo);
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

        public static RoomRepo fromFile(String osmFile) throws IOException {
            logger.info("Initializing RoomRepo class from file {}.", osmFile);

            RoomOsmReader osmReader = new RoomOsmReader();
            osmReader.setOsmFile(new File(osmFile));
            List<Poi> pois = osmReader.readGraph();

            logger.debug("Read file.");

            RoomRepo repo = new RoomRepo();
            for (Poi poi : pois) {
                repo.add(poi);
            }

            logger.info("Loaded room data with {} pois into repo.", repo.size());

            return repo;
        }

        public static RoomRepo fromPois(Poi... pois) {
            RoomRepo repo = new RoomRepo();
            repo.addAll(pois);
            return repo;
        }


    }
}
