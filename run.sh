dir=`dirname $(readlink $0)`
cd "$dir"
java -jar target/rolli-jar-with-dependencies.jar
