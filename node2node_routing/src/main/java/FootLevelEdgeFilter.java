import com.graphhopper.routing.profiles.SimpleBooleanEncodedValue;
import com.graphhopper.routing.profiles.UnsignedDecimalEncodedValue;
import com.graphhopper.routing.util.EdgeFilter;
import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.routing.util.FootFlagLevelEncoder;
import com.graphhopper.storage.IntsRef;
import com.graphhopper.util.EdgeIteratorState;
import org.slf4j.LoggerFactory;

import static com.graphhopper.routing.util.EncodingManager.getKey;

public class FootLevelEdgeFilter implements EdgeFilter {

    FootFlagLevelEncoder encoder;

    public FootLevelEdgeFilter(FlagEncoder encoder) {
        if(!(encoder instanceof FootFlagLevelEncoder)){
            throw new IllegalArgumentException();
        }
        this.encoder = (FootFlagLevelEncoder) encoder;
    }

    @Override
    public boolean accept(EdgeIteratorState edgeState) {
        // Accept all for now

        IntsRef flags = edgeState.getFlags();
        String prefix = "foot_level";
        UnsignedDecimalEncodedValue levelEncoder = new UnsignedDecimalEncodedValue(getKey(prefix, "level_no"), 3, 2, false);
        SimpleBooleanEncodedValue levelDirEncoder = new SimpleBooleanEncodedValue(getKey(prefix, "level_dir"), false);

        double level = encoder.getLevelFrom(edgeState.getFlags());

        if(level!=0){
            LoggerFactory.getLogger(FootLevelEdgeFilter.class).debug("Got level {}", level);
        }

        return true;
    }
}
