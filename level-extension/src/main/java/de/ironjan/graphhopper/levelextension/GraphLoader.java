package de.ironjan.graphhopper.levelextension;

import com.graphhopper.GraphHopper;
import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.routing.util.EncodingManager;

public class GraphLoader {
    private static EncodingManager em;

    public static GraphHopper get(String osmFile, String graphFolder) {
        // create one GraphHopper instance
        GraphHopper hopper = new GraphHopperOSM().forServer();
        hopper.setDataReaderFile(osmFile);
        hopper.setElevation(true);
        hopper.setMinNetworkSize(1,1);
// where to store graphhopper files?
        hopper.setGraphHopperLocation(graphFolder);


        EncodingManager em = getEncodingManager();
//        EncodingManager em = EncodingManager.create("foot");

        hopper.setEncodingManager(em);

// now this can take minutes if it imports or a few seconds for loading
// of course this is dependent on the area you import
        hopper.importOrLoad();

        return hopper;
    }

    public static synchronized EncodingManager getEncodingManager() {
        if(em == null){
            em = EncodingManager.create(new FootFlagLevelEncoder());;
        }
        return em;
    }
}
