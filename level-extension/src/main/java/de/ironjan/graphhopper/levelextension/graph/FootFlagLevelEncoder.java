package de.ironjan.graphhopper.levelextension.graph;

import com.graphhopper.reader.ReaderWay;
import com.graphhopper.routing.profiles.DecimalEncodedValue;
import com.graphhopper.routing.profiles.EncodedValue;
import com.graphhopper.routing.profiles.SimpleBooleanEncodedValue;
import com.graphhopper.routing.profiles.UnsignedDecimalEncodedValue;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.routing.util.FootFlagEncoder;
import com.graphhopper.storage.IntsRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.graphhopper.routing.util.EncodingManager.getKey;

public class FootFlagLevelEncoder extends FootFlagEncoder {
    private String prefix = "foot_level";
    private DecimalEncodedValue levelEncoder = new UnsignedDecimalEncodedValue(getKey(prefix, "level_no"), 3, 1, false);
    private SimpleBooleanEncodedValue levelDirEncoder = new SimpleBooleanEncodedValue(EncodingManager.getKey(prefix, "level_dir"), true);

    private Logger logger = LoggerFactory.getLogger(FootFlagLevelEncoder.class);

    public FootFlagLevelEncoder() {
    }

    @Override
    public void createEncodedValues(List<EncodedValue> registerNewEncodedValue, String prefix, int index) {
        super.createEncodedValues(registerNewEncodedValue, prefix, index);
        registerNewEncodedValue.add(levelEncoder);
        registerNewEncodedValue.add(levelDirEncoder);
        logger.debug("Created encoded values and added levelEncoder and levelDirEncoder.");
    }

    @Override
    public IntsRef handleWayTags(IntsRef edgeFlags, ReaderWay way, EncodingManager.Access access) {
        IntsRef intsRef = super.handleWayTags(edgeFlags, way, access);

        long id = way.getId();
        logger.debug("Handling way tags for way {}.", id);

        String level;
        if (way.hasTag("level")) {
            level = way.getTag("level");
            logger.debug("Way had a level tag with value {}.", level);
        } else {
            level = "0.0";
        }

        double lvl;
        try {
            lvl = Double.parseDouble(level);
        } catch (NumberFormatException ignored) {
            logger.debug("Way did not have a valid level tag value. Defaulting to 0.0.");
            lvl = 0d;
        }
        boolean flipped = lvl < 0d;
        double absLvl = Math.abs(lvl);
        levelDirEncoder.setBool(false, edgeFlags, flipped);
        levelEncoder.setDecimal(false, edgeFlags, absLvl);

        logger.debug("Encoded level {} for way {}: flipped={}, absLvl={}.", lvl, id, flipped, absLvl);

        return intsRef;
    }

    @Override
    public String toString() {
        return prefix;
    }

    public double getLevelFrom(IntsRef flags) {
        boolean flipped = levelDirEncoder.getBool(false, flags);
        double rawLevel = levelEncoder.getDecimal(false, flags);

        double lvl = flipped ? -rawLevel : rawLevel;

        logger.debug("Parsed lvl {} from flags: flipped={}, absLvl={}.", lvl, flipped, rawLevel);

        return lvl;
    }

    public DecimalEncodedValue getLevelEncoder() {
        return levelEncoder;
    }

    public SimpleBooleanEncodedValue getLevelDirEncoder() {
        return levelDirEncoder;
    }
}
