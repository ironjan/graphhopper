public class Coordinate {
    final double lat, lon, lvl;

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
        return "Coordinate(" +
                lat + ", " +
                lon + ", " +
                lvl + ')';
    }
}
