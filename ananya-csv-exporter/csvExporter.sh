#
# Build the report for the given entity and exports into a .csv file
#
# Usage: csvExporter.sh <Entity to be reported> [-f <arg> filters file path] [-o <arg> output file path]
#

jar_names=`ls lib/ | grep .jar`

java_command="java -cp "
for jar_name in $jar_names
 do
  java_command=$java_command":lib/"$jar_name
 done
java_command="$java_command org.motechproject.ananya.console.CsvExporterApp $@"
echo `$java_command`
