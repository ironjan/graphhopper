package de.ironjan.graphhopper.extensions_core;

import java.util.Locale;
import java.util.Scanner;

public class Coordinate {
    public final double lat, lon, lvl;

    public Coordinate(String name, double lat, double lon, double lvl) {
        this(lat, lon, lvl);
    }

    public Coordinate(double lat, double lon, double lvl) {
        this.lat = lat;
        this.lon = lon;
        this.lvl = lvl;
    }

    @Override
    public String toString() {
        return "de.ironjan.graphhopper.geocoding.Coordinate(" +
                lat + ", " +
                lon + ", " +
                lvl + ')';
    }


    public String asString() {
        return String.format(Locale.US, "%f,%f,%f", lat, lon, lvl);
    }

    public static Coordinate fromString(String s) {
        Scanner sc = new Scanner(s);
        double lat = sc.nextDouble();
        double lon = sc.nextDouble();
        double lvl = sc.nextDouble();
        return new Coordinate(lat, lon, lvl);
    }
}
