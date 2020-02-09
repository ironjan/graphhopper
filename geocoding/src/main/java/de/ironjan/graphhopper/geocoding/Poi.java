package de.ironjan.graphhopper.geocoding;

import de.ironjan.graphhopper.extensions_core.Coordinate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Poi{
    private final String name;
    private final List<Coordinate> entrances = new ArrayList<>();

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


    public Poi(String name, List<Coordinate> cList) {
        this.name = name;
        this.entrances.addAll(cList);
    }

    public String getName() {
        return name;
    }

    public List<Coordinate> getEntrances() {
        return entrances;
    }
}
