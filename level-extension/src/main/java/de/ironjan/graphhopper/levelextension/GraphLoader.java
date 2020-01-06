package de.ironjan.graphhopper.levelextension;

import com.graphhopper.GraphHopper;
import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.storage.Graph;
import de.ironjan.graphhopper.levelextension.graph.FootFlagLevelEncoder;
import de.ironjan.graphhopper.util.DirectoryDeleter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraphLoader {
    private static EncodingManager em;

    private static Logger logger = LoggerFactory.getLogger(GraphLoader.class);

    public static void importAndExit(String osmFile, String graphFolder) {
        logger.debug("Importing {} into {}.", osmFile, graphFolder);

        DirectoryDeleter.deleteDirectory(graphFolder);

        GraphHopper hopper = buildGraphHopper(graphFolder);
        hopper.setDataReaderFile(osmFile);
        hopper.importAndClose();
    }

    public static GraphHopper loadExisting(String graphFolder){
        GraphHopper hopper = buildGraphHopper(graphFolder);

        logger.debug("Loading existing gh folder {}.", graphFolder);

        return hopper.importOrLoad();
    }
    public static GraphHopper importOrLoad(String osmFile, String graphFolder) {
        GraphHopper hopper = buildGraphHopper(graphFolder);

        hopper.setDataReaderFile(osmFile);

        logger.debug("Either loading existing gh folder {} or importing from {} and then loading.", graphFolder, osmFile);

// now this can take minutes if it imports or a few seconds for loading
// of course this is dependent on the area you import
        hopper.importOrLoad();

        return hopper;
    }

    private static GraphHopper buildGraphHopper(String graphFolder) {
        // create one GraphHopper instance
        GraphHopper hopper = new GraphHopperOSM().forMobile();
        hopper.setElevation(true);
        hopper.setMinNetworkSize(1, 1);
// where to store graphhopper files?
        hopper.setGraphHopperLocation(graphFolder);


        EncodingManager em = getEncodingManager();

        hopper.setEncodingManager(em);
        return hopper;
    }

    public static synchronized EncodingManager getEncodingManager() {
        if (em == null) {
            em = EncodingManager.create(new FootFlagLevelEncoder());
        }
        return em;
    }
}
