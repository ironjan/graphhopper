package de.ironjan.graphhopper.levelextension.routing;

/*
 *  Licensed to GraphHopper GmbH under one or more contributor
 *  license agreements. See the NOTICE file distributed with this work for
 *  additional information regarding copyright ownership.
 *
 *  GraphHopper GmbH licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except in
 *  compliance with the License. You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import com.graphhopper.routing.Path;
import com.graphhopper.routing.profiles.*;
import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.routing.weighting.Weighting;
import com.graphhopper.storage.Graph;
import com.graphhopper.storage.IntsRef;
import com.graphhopper.util.*;
import de.ironjan.graphhopper.levelextension.graph.FootFlagLevelEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.graphhopper.routing.util.EncodingManager.getKey;

/**
 * This class calculates instructions from the edges in a Path.
 *
 * @author Peter Karich
 * @author Robin Boldt
 * @author jan soe
 */
public class InstructionsFromEdges
        extends com.graphhopper.routing.InstructionsFromEdges
        implements Path.EdgeVisitor {
    private final FootFlagLevelEncoder encoder;
    private String prefix = "foot_level";
    private EdgeIteratorState prevEdge;
    private double prevLevel = Double.NaN;
    private Logger logger = LoggerFactory
            .getLogger("MyInstructions");

    public InstructionsFromEdges(Graph graph, Weighting weighting, EncodedValueLookup evLookup,
                                 Translation tr, InstructionList ways) {
        super(graph, weighting, evLookup, tr, ways);
        FlagEncoder encoder = weighting.getFlagEncoder();
        if(!(encoder instanceof FootFlagLevelEncoder)){
            throw new IllegalArgumentException();
        }
        this.encoder = (FootFlagLevelEncoder) encoder;
    }

    @Override
    public void next(EdgeIteratorState edge, int index, int prevEdgeId) {
        super.next(edge, index, prevEdgeId);


        IntsRef flags = edge.getFlags();
        double level = encoder.getLevelFrom(flags);
        logger.debug("Processing edge {} on level {}", edge, level);

        double tmpPrevLevel = prevLevel;
        EdgeIteratorState tmpPrevEdge = prevEdge;
        prevLevel = level;
        prevEdge = edge;

        if(Double.isNaN(tmpPrevLevel)){
            return;
        }

        double levelDifference = level - tmpPrevLevel;

        logger.debug("Moving from edge {} to edge  {} ({} to {}, {})", prevEdge, edge, tmpPrevLevel, level, levelDifference);
        // TODO improve this
        Map<String, Object> extraInfoJSON = getLastAddedInstruction().getExtraInfoJSON();
        extraInfoJSON.put("prevEdgeLevel", tmpPrevLevel);
        extraInfoJSON.put("thisEdgeLevel", level);
    }


    /**
     * @return the list of instructions for this path.
     */
    public static InstructionList calcInstructions(Path path, Graph graph, Weighting weighting, EncodedValueLookup evLookup, final Translation tr) {
        final InstructionList ways = new InstructionList(tr);
        if (path.isFound()) {
            if (path.getSize() == 0) {
                ways.add(new FinishInstruction(graph.getNodeAccess(), path.getEndNode()));
            } else {
                path.forEveryEdge(new InstructionsFromEdges(graph, weighting, evLookup, tr, ways));
            }
        }
        return ways;
    }

}