package de.ironjan.graphhopper.levelextension.routing;

import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.routing.weighting.ShortestWeighting;
import com.graphhopper.routing.weighting.Weighting;
import com.graphhopper.util.EdgeIteratorState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WrappedShortestWeighting extends ShortestWeighting {
    private Logger logger = LoggerFactory.getLogger(WrappedShortestWeighting.class);

    public WrappedShortestWeighting(FlagEncoder foot) {
        super(foot);
    }

    @Override
    public double calcWeight(EdgeIteratorState edgeState, boolean reverse, int prevOrNextEdgeId) {
        try{
            return super.calcWeight(edgeState, reverse, prevOrNextEdgeId);
        }catch (Exception e) {
            logger.debug("Caught and ignored the following exception when calculating weight.", e);
            return Double.MAX_VALUE;
        }
    }
}
