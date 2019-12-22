package com.graphhopper.reader;

import com.graphhopper.storage.DataAccess;
import com.graphhopper.storage.Directory;
import com.graphhopper.util.Helper;
import com.graphhopper.util.IndoorPointAccess;

public class IndoorPillarInfo
        extends PillarInfo
        implements IndoorPointAccess {
    private final DataAccess da;
    private final int rowSizeInBytes = 4;

    public IndoorPillarInfo(boolean enabled3D, Directory dir) {
        super(enabled3D, dir);
        this.da = dir.find("tmp_indoor_pillar_info").create(100);

    }

    @Override
    public void ensureNode(int nodeId) {
        super.ensureNode(nodeId);
        long tmp = (long) nodeId * rowSizeInBytes;
        da.ensureCapacity(tmp + rowSizeInBytes);
    }

    @Override
    public void setNode(int nodeId, double lat, double lon) {
        setNode(nodeId, lat, lon, Double.NaN, 0);
    }

    @Override
    public void setNode(int nodeId, double lat, double lon, double ele) {
        setNode(nodeId, lat, lon, ele, 0.0);
    }

    @Override
    public void setNode(int nodeId, double lat, double lon, double ele, double level) {
        super.setNode(nodeId, lat, lon, ele);
        _setNode(nodeId, level);
    }


    private void _setNode(int nodeId, double level) {
        ensureNode(nodeId);
        long tmp = (long) nodeId * rowSizeInBytes;
        da.setInt(tmp, (int) level*10);
    }

    @Override
    public double getLevel(int nodeId) {
        return da.getInt((long)nodeId * rowSizeInBytes);
    }
}
