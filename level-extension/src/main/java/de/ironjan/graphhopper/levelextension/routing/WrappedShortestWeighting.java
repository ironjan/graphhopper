package de.ironjan.graphhopper.levelextension.routing;

import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.routing.weighting.ShortestWeighting;
import com.graphhopper.routing.weighting.Weighting;
import com.graphhopper.util.EdgeIteratorState;

public class WrappedShortestWeighting extends ShortestWeighting {
    public WrappedShortestWeighting(FlagEncoder foot) {
        super(foot);
    }

    @Override
    public double calcWeight(EdgeIteratorState edgeState, boolean reverse, int prevOrNextEdgeId) {
        try{
            return super.calcWeight(edgeState, reverse, prevOrNextEdgeId);
        }catch (Exception ignored) {
            return Double.MAX_VALUE;
        }
    }
}
