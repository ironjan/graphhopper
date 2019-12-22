package com.graphhopper.reader.osm;

import com.graphhopper.json.geo.JsonFeatureCollection;
import com.graphhopper.reader.DataReader;
import com.graphhopper.storage.GraphHopperStorage;

public class IndoorGraphHopperOSM extends GraphHopperOSM {
    public IndoorGraphHopperOSM() {
    }

    public IndoorGraphHopperOSM(JsonFeatureCollection landmarkSplittingFeatureCollection) {
        super(landmarkSplittingFeatureCollection);
    }

    @Override
    protected DataReader createReader(GraphHopperStorage ghStorage) {
        return initDataReader(new IndoorOSMReader(ghStorage));
    }
}
