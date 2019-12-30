package de.ironjan.graphhopper.geocoding;

public class Coordinate {
    public final double lat, lon, lvl;

    public Coordinate(String name, double lat, double lon, double lvl) {
        this(lat,lon,lvl);
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
}
