package com.graphhopper.routing.util;

import com.graphhopper.reader.ReaderWay;
import com.graphhopper.routing.profiles.DecimalEncodedValue;
import com.graphhopper.routing.profiles.EncodedValue;
import com.graphhopper.routing.profiles.SimpleBooleanEncodedValue;
import com.graphhopper.routing.profiles.UnsignedDecimalEncodedValue;
import com.graphhopper.storage.IntsRef;
import com.graphhopper.util.PMap;

import java.util.List;

import static com.graphhopper.routing.util.EncodingManager.getKey;

public class FootFlagLevelEncoder extends FootFlagEncoder {
    private DecimalEncodedValue levelEncoder;
    private SimpleBooleanEncodedValue levelDirEncoder;

    public FootFlagLevelEncoder() {
    }

    public FootFlagLevelEncoder(PMap properties) {
        super(properties);
    }

    public FootFlagLevelEncoder(String propertiesStr) {
        super(propertiesStr);
    }

    public FootFlagLevelEncoder(int speedBits, double speedFactor) {
        super(speedBits, speedFactor);
    }

    @Override
    public void createEncodedValues(List<EncodedValue> registerNewEncodedValue, String prefix, int index) {
        super.createEncodedValues(registerNewEncodedValue, prefix, index);
        registerNewEncodedValue.add(levelEncoder = new UnsignedDecimalEncodedValue(getKey(prefix, "level_no"), 3, 2, false));
        registerNewEncodedValue.add(levelDirEncoder = new SimpleBooleanEncodedValue(EncodingManager.getKey(prefix, "level_dir"), true));
        List<EncodedValue> x = registerNewEncodedValue;
    }

    @Override
    public IntsRef handleWayTags(IntsRef edgeFlags, ReaderWay way, EncodingManager.Access access) {
        IntsRef intsRef = super.handleWayTags(edgeFlags, way, access);

        String level = way.getTag("level", "0.0");
        double lvl;
        try {
            lvl = Double.parseDouble(level);
        } catch (NumberFormatException ignored) {
            lvl = 0d;
        }
        boolean belowGround = lvl < 0d;
        levelEncoder.setDecimal(false, edgeFlags, belowGround ? -lvl : lvl);
        levelDirEncoder.setBool(false, edgeFlags, belowGround);

        IntsRef x = intsRef;
        return intsRef;
    }

    @Override
    public String toString() {
        return "foot_level";
    }
}
