package de.ironjan.overpass;

import java.util.HashMap;

public class OverpassElement {
    private String type;
    private long id;
    private double lat;
    private double lon;
    private HashMap<String, String> tags;
    private long[] nodes;

    public boolean isWay(){
        return "way".equals(type);
    }

    public OverpassElement() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public HashMap<String, String> getTags() {
        return tags;
    }

    public void setTags(HashMap<String, String> tags) {
        this.tags = tags;
    }

    public long[] getNodes() {
        return nodes;
    }

    public void setNodes(long[] nodes) {
        this.nodes = nodes;
    }
}
