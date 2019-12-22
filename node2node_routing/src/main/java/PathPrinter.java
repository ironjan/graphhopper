import com.graphhopper.PathWrapper;
import com.graphhopper.routing.Path;
import com.graphhopper.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

public class PathPrinter {

    private static Logger logger = LoggerFactory.getLogger(PathPrinter.class);

    static void print(PathWrapper path) {

// points, distance in meters and time in millis of the full path
        PointList pointList = path.getPoints();
        double distance = path.getDistance();
        long timeInMs = path.getTime();

        InstructionList il = path.getInstructions();
// iterate over every turn instruction
        for (Instruction instruction : il) {
            instruction.getDistance();
            instruction.getTime();
            instruction.getName();
            instruction.getLength();
            instruction.getExtraInfoJSON();

            TranslationMap translationMap = new TranslationMap().doImport();
            Translation tr = translationMap.getWithFallBack(Locale.GERMAN);
            logger.debug("{} {}", instruction.getTurnDescription(tr), instruction);
        }
    }
}
