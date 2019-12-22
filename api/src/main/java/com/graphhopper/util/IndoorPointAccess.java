package com.graphhopper.util;

public interface IndoorPointAccess extends PointAccess {
    /**
     * This method ensures that the node with the specified index exists and prepares access to it.
     * The index goes from 0 (inclusive) to graph.getNodes() (exclusive)
     * <p>
     * This methods sets the latitude, longitude and elevation to the specified value.
     */
    void setNode(int nodeId, double lat, double lon, double level);

    /**
     * This method ensures that the node with the specified index exists and prepares access to it.
     * The index goes from 0 (inclusive) to graph.getNodes() (exclusive)
     * <p>
     * This methods sets the latitude, longitude and elevation to the specified value.
     */
    void setNode(int nodeId, double lat, double lon, double ele, double level);

    double getLevel(int nodeId);
}
