
import com.graphhopper.GraphHopper;
import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.routing.util.EncodingManager;

import java.io.File;

public class IndoorTesting {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: IndoorTesting <input.osm> <gh-folder>");
            System.exit(1);
        }


        deleteDirectory(new File(args[1]));

        // create one GraphHopper instance
        GraphHopper hopper = new GraphHopperOSM().forServer();
        hopper.setDataReaderFile(args[0]);

        // where to store graphhopper files?
        hopper.setGraphHopperLocation(args[1]);
        hopper.setEncodingManager(EncodingManager.create("foot"));

        // now this can take minutes if it imports or a few seconds for loading
        // of course this is dependent on the area you import
        hopper.importOrLoad();


// simple configuration of the request object, see the GraphHopperServlet classs for more possibilities.
//        GHRequest req = new GHRequest(latFrom, lonFrom, latTo, lonTo).
//                setWeighting("fastest").
//                setVehicle("car").
//                setLocale(Locale.US);
//        GHResponse rsp = hopper.route(req);
//
//// first check for errors
//        if(rsp.hasErrors()) {
//            // handle them!
//            // rsp.getErrors()
//            return;
//        }
//
//// use the best path, see the GHResponse class for more possibilities.
//        PathWrapper path = rsp.getBest();
//
//// points, distance in meters and time in millis of the full path
//        PointList pointList = path.getPoints();
//        double distance = path.getDistance();
//        long timeInMs = path.getTime();
//
//        InstructionList il = path.getInstructions();
//// iterate over every turn instruction
//        for(Instruction instruction : il) {
//            instruction.getDistance();
//   ...
//        }
//
//// or get the json
//        List<Map<String, Object>> iList = il.createJson();
//
//// or get the result as gpx entries:
//        List<GPXEntry> list = il.createGPXList();
    }


    // https://www.baeldung.com/java-delete-directory
    private static boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }
}
