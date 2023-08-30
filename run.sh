dir=`dirname $(realpath "$0")`
cd "$dir"
java -jar -Dlogback.configurationFile=logback-debug.xml target/gcs_dialog.jar
