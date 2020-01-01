package de.ironjan.overpass;

import java.util.List;

public class OverpassResponse {
    private double version;
    private String generator;
    private Osm3s osm3s;
    private List<OverpassElement> elements;

    public OverpassResponse() {
    }

    public double getVersion() {
        return version;
    }

    public void setVersion(double version) {
        this.version = version;
    }

    public String getGenerator() {
        return generator;
    }

    public void setGenerator(String generator) {
        this.generator = generator;
    }

    public Osm3s getOsm3s() {
        return osm3s;
    }

    public void setOsm3s(Osm3s osm3s) {
        this.osm3s = osm3s;
    }

    public List<OverpassElement> getElements() {
        return elements;
    }

    public void setElements(List<OverpassElement> elements) {
        this.elements = elements;
    }
}
