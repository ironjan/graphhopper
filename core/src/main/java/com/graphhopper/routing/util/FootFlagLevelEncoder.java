package com.graphhopper.routing.util;

import com.graphhopper.reader.ReaderWay;
import com.graphhopper.routing.profiles.DecimalEncodedValue;
import com.graphhopper.routing.profiles.EncodedValue;
import com.graphhopper.routing.profiles.SimpleBooleanEncodedValue;
import com.graphhopper.routing.profiles.UnsignedDecimalEncodedValue;
import com.graphhopper.storage.IntsRef;

import java.util.List;

import static com.graphhopper.routing.util.EncodingManager.getKey;

public class FootFlagLevelEncoder extends FootFlagEncoder {
    private String prefix = "foot_level";
    private DecimalEncodedValue levelEncoder= new UnsignedDecimalEncodedValue(getKey(prefix, "level_no"), 3, 1, false);
    private SimpleBooleanEncodedValue levelDirEncoder = new SimpleBooleanEncodedValue(EncodingManager.getKey(prefix, "level_dir"), true);

    public FootFlagLevelEncoder() {
    }

    @Override
    public void createEncodedValues(List<EncodedValue> registerNewEncodedValue, String prefix, int index) {
        super.createEncodedValues(registerNewEncodedValue, prefix, index);
        registerNewEncodedValue.add(levelEncoder);
        registerNewEncodedValue.add(levelDirEncoder);
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
        boolean flipped = lvl < 0d;
        levelDirEncoder.setBool(false, edgeFlags, flipped);
        levelEncoder.setDecimal(false, edgeFlags, Math.abs(lvl));

        return intsRef;
    }

    @Override
    public String toString() {
        return prefix;
    }

    public double getLevelFrom(IntsRef flags){
        boolean flipped = levelDirEncoder.getBool(false, flags);
        double rawLevel = levelEncoder.getDecimal(false, flags);

        return flipped ? -rawLevel : rawLevel;
    }

    public DecimalEncodedValue getLevelEncoder() {
        return levelEncoder;
    }

    public SimpleBooleanEncodedValue getLevelDirEncoder() {
        return levelDirEncoder;
    }
}
