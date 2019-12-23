import com.graphhopper.PathWrapper;
import com.graphhopper.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

public class PathPrinter {

    private static Logger logger = LoggerFactory.getLogger(PathPrinter.class);

    static void printSummary(String router, PathWrapper path) {
        if(path == null){
            logger.debug("Could not compute route with {}.", router);
            return;
        }
        logger.debug("Path from {}: {}", router, path);
    }
    static void print(String router, PathWrapper path) {

// points, distance in meters and time in millis of the full path
        printSummary(router,path);
        PointList pointList = path.getPoints();
        logger.debug("Pointlist: {}", pointList);

        try{

            InstructionList il = path.getInstructions();
// iterate over every turn instruction
            for (Instruction instruction : il) {
                TranslationMap translationMap = new TranslationMap().doImport();
                Translation tr = translationMap.getWithFallBack(Locale.GERMAN);
                logger.debug("{} {}", instruction.getTurnDescription(tr), instruction);
                logger.debug("{}", instruction.getExtraInfoJSON());

            }
        }catch (Exception ignored){}

    }
}
