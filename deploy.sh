TOMCAT="/Users/kamilhus/Downloads/apache-tomcat-9.0.115"
PROJECT="/Users/kamilhus/IdeaProjects/Currency exchange"
MAVEN="/Applications/IntelliJ IDEA CE.app/Contents/plugins/maven/lib/maven3/bin/mvn"

"$MAVEN" clean package || exit 1

rm -rf "$TOMCAT/webapps/ROOT" \
       "$TOMCAT/webapps/ROOT.war"

cp "$PROJECT/target/currency-exchange-1.0-SNAPSHOT.war" \
   "$TOMCAT/webapps/ROOT.war"
