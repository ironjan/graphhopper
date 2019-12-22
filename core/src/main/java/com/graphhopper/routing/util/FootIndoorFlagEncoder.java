package com.graphhopper.routing.util;

import com.graphhopper.routing.profiles.EncodedValue;
import com.graphhopper.util.PMap;

import java.util.List;

public class FootIndoorFlagEncoder extends FootFlagEncoder {
    public FootIndoorFlagEncoder() {
        super();
    }

    public FootIndoorFlagEncoder(PMap properties) {
        super(properties);
    }

    public FootIndoorFlagEncoder(String propertiesStr) {
        super(propertiesStr);
    }

    public FootIndoorFlagEncoder(int speedBits, double speedFactor) {
        super(speedBits, speedFactor);
    }


    @Override
    public void createEncodedValues(List<EncodedValue> registerNewEncodedValue, String prefix, int index) {
        super.createEncodedValues(registerNewEncodedValue, prefix, index);
    }
}
