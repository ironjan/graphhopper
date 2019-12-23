package com.graphhopper.routing.weighting;

import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.util.EdgeIteratorState;

public class WrappedShortestWeighting extends ShortestWeighting {
    public WrappedShortestWeighting(FlagEncoder flagEncoder) {
        super(flagEncoder);
    }

    @Override
    public double calcWeight(EdgeIteratorState edgeState, boolean reverse, int prevOrNextEdgeId) {
        try{
            return super.calcWeight(edgeState, reverse, prevOrNextEdgeId);
        }catch (IllegalStateException e){
            return Double.MAX_VALUE;
        }
    }
}
