#!/bin/bash

FILE="saw"
MAPS_FOLDER="$HOME/projects/maps"
MAP_START_POS="50.2836424,11.6190666"
MAP_START_ZOOM="17"
BBOX="50.26696,11.5864563,50.3011828,11.6777802"

INPUT_FILE="$MAPS_FOLDER/${FILE}.osm"
GH_FOLDER="$MAPS_FOLDER/${FILE}-gh/"
MAP_FILE="$GH_FOLDER/${FILE}.map"


if [ "$1" == "-h" ]; then
  echo "Pre-Requirements:"
  echo " * osmosis with map writer plugin"
  echo "How to prepare GHZ files:"
  echo " 1. Get an .osm-file"
  echo " 2. Edit the variables in this script"
  echo " 3. Execute"
  echo ""
  echo "Related resources:"
  echo " * https://github.com/graphhopper/graphhopper/blob/master/docs/android/index.md#maps"
  echo " * https://github.com/mapsforge/mapsforge/blob/master/docs/Getting-Started-Map-Writer.md"
  echo ""
  echo "Options:"
  echo " -i  : Installs osmosis and the required plugin"
  echo " -p  : adb pushes the created files"
  exit 0
fi

if [ "$1" == "-i" ]; then
  yay -S osmosis
  mkdir -p $HOME/.openstreetmap/osmosis/plugins
  wget "https://oss.sonatype.org/content/repositories/snapshots/org/mapsforge/mapsforge-map-writer/master-SNAPSHOT/mapsforge-map-writer-master-20191223.192846-331-jar-with-dependencies.jar" \
    -O $HOME/.openstreetmap/osmosis/plugins/mapsforge-map-writer.jar
  exit 0
fi

if [ "$1" == "-p" ]; then
  for i in ${GH_FOLDER}*; do 
    adb push $i /sdcard/Download/graphhopper/maps/${FILE}-gh/$(basename $i)
  done
  exit 0
fi

rm -rv $GH_FOLDER
./graphhopper.sh -a import -i $INPUT_FILE

osmosis --rx file=$INPUT_FILE --mapfile-writer file=$MAP_FILE map-start-position=$MAP_START_POS map-start-zoom=$MAP_START_ZOOM bbox=$BBOX
pushd $GH_FOLDER
zip -r ${FILE}.ghz *
popd

cp -v $GH_FOLDER/${FILE}.ghz ./android/app/src/main/res/raw/