import com.graphhopper.PathWrapper;
import com.graphhopper.util.*;
import com.graphhopper.util.shapes.GHPoint3D;
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
                logger.debug("{} {}, extra: {}", instruction.getTurnDescription(tr), instruction, instruction.getExtraInfoJSON());
            }
            collectWayOsm(path);
        }catch (Exception ignored){}

    }

    private static void collectWayOsm(PathWrapper path) {
        StringBuilder sb = new StringBuilder("<?xml version='1.0' encoding='UTF-8'?><osm version='0.6' generator='JOSM'>");
        PointList points = path.getPoints();
        int counter = 100;
        for (GHPoint3D p :points) {
            sb.append("<node id='-");
            sb.append(counter++);
            sb.append("' lat='");
            sb.append(p.lat);
            sb.append("' lon='");
            sb.append(p.lon);
            sb.append("' />");
        }
        sb.append("</osm>");
        logger.debug(sb.toString());
    }
}
