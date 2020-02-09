package de.ironjan.graphhopper.levelextension;

import de.ironjan.graphhopper.util.DirectoryDeleter;

public class Main {
    public static void main(String[] args){
        if(args.length < 2) {
            System.out.println("Invalid Arguments. Allowed format: <osm file> <gh folder>.");
        }

        String osmFile = args[0];
        String graphFolder = args[1];
        DirectoryDeleter.deleteDirectory(graphFolder);

            GraphLoader.importAndExit(osmFile, graphFolder);
    }

}
