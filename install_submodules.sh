cd hfc-database
mvn clean install
cd..

cd osm2owl
mvn clean install
cd ..

cd mlt-rosbridge/RosBridge
mvn clean install
cd ../..

yes | rm -r nuancewebsocket
git clone git@mlt-gitlab.sb.dfki.de:willms/nuancewebsocket.git
cd nuancewebsocket
mvn clean install
cd ..

cd rudibugger
mvn clean install
cd ..
