import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * From https://www.baeldung.com/java-delete-directory
 */
public class DirectoryDeleter {

    public static boolean deleteDirectory(String path){
        LoggerFactory.getLogger(DirectoryDeleter.class).debug("Deleting {}...", path);
        return deleteDirectory(new File(path));
    }
    public static boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }
}
