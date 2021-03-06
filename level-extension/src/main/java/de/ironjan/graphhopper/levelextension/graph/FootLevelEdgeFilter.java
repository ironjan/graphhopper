package de.ironjan.graphhopper.levelextension.graph;

import com.graphhopper.routing.util.EdgeFilter;
import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.util.EdgeIteratorState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FootLevelEdgeFilter implements EdgeFilter {

    FootFlagLevelEncoder encoder;
    private double expectedLevel;
    private Logger logger = LoggerFactory.getLogger(FootLevelEdgeFilter.class);

    public FootLevelEdgeFilter(FlagEncoder encoder, double expectedLevel) {
        this.expectedLevel = expectedLevel;
        if(!(encoder instanceof FootFlagLevelEncoder)){
            throw new IllegalArgumentException();
        }
        this.encoder = (FootFlagLevelEncoder) encoder;
    }


    @Override
    public boolean accept(EdgeIteratorState edgeState) {
        logger.debug("Checking if edge {} is on level {}", edgeState, expectedLevel);
        
        double level = encoder.getLevelFrom(edgeState.getFlags());

        if(Double.isNaN(expectedLevel)){
            return true;
        }

        boolean isMatch = expectedLevel == level;
        logger.debug("Edge {} accepted when looking for level {}? {}", edgeState, expectedLevel, isMatch);
        return isMatch;
    }
}
