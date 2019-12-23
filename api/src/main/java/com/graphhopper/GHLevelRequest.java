package com.graphhopper;

public class GHLevelRequest extends GHRequest {

    public GHLevelRequest(double fromLat, double fromLon, double toLat, double toLon, double startHeading, double endHeading, double fromLvl, double toLvl) {
        super(fromLat, fromLon, toLat, toLon, startHeading, endHeading);
    }

    public GHLevelRequest(double fromLat, double fromLon, double toLat, double toLon, double startHeading, double endHeading) {
        this(fromLat, fromLon, toLat, toLon, startHeading, endHeading,0d,0d);
    }

    public GHLevelRequest(double fromLat, double fromLon, double toLat, double toLon) {
        this(fromLat, fromLon, toLat, toLon, Double.NaN, Double.NaN);
    }
}
