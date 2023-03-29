dir=`dirname $(readlink $0)`
cd "$dir"
java -jar target/drz_signs_of_life-1.1-SNAPSHOT.jar
