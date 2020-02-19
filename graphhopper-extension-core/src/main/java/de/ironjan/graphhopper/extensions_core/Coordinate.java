package de.ironjan.graphhopper.extensions_core;

import java.util.Locale;
import java.util.NoSuchElementException;
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


    /**
     * Creates a string representation of this {@see Coordinate} that can be parsed via {@see fromString}
     * @return a parsable string representation
     */
    public String asString() {
        return String.format(Locale.US, "%f,%f,%f", lat, lon, lvl);
    }

    /**
     * Parses the given String into a coordinate.
     * @param s the string to be parsed
     * @return the {@see Coordinate} the string was representing
     * @throws IllegalArgumentException if {@see s} is in the wrong format
     */
    public static Coordinate fromString(String s) {
        try {
            Scanner sc = new Scanner(s);
            sc.useLocale(Locale.US);
            double lat = sc.nextDouble();
            double lon = sc.nextDouble();
            double lvl = sc.nextDouble();
            sc.close();
            return new Coordinate(lat, lon, lvl);
        } catch (NoSuchElementException ignored){
         throw new IllegalArgumentException("The given string is not in the correct format (lat,lon,lvl).");
        }
    }
}
