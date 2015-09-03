#!/bin/sh

APP_NAME="JungleCars"
PROJECT_DIR=$HOME/Developer/Library/EclipseWS
WEBAPPS=$CATALINA_HOME/webapps
TARGET_WAR=$WEBAPPS/$APP_NAME.war
TARGET_DIR=$WEBAPPS/$APP_NAME
CARS=$PROJECT_DIR/JCCars/cars

# Build the app
mvn package
MVN_RETURN=$?
if [ ! $MVN_RETURN -eq 0  ];
then
    echo "Return value form mvn package is: " $MVN_RETURN
    exit $MVN_RETURN
fi

# Save the car images folder before starting the browser
#if [ -f $TARGET_DIR ];
#then
    echo "Moving car listings to: " $WEBAPPS
    mv $TARGET_DIR/cars $WEBAPPS/cars
#fi


# Clean the install path
if [ -f $TARGET_WAR ];
then
    echo "Deleting file: " $TARGET_WAR
    rm $TARGET_WAR
fi
if [ -d $TARGET_DIR ];
then
    echo "Deleting folder: " $TARGET_DIR
    rm -r $TARGET_DIR
fi

# Restart Tomcat
echo "Shutting Tomcat down"
$CATALINA_HOME/bin/shutdown.sh
sleep 5

# Kill all MySQL pending oonnections
echo "Killing pending MySQL connections"
ruby ../JCCars/mysql_kill_processes.rb 2> /dev/null

# Install new app version
echo "Deploying Web Aplication " $APP_NAME
cp target/$APP_NAME.war $WEBAPPS

echo "Truncating Tomcat log file..."
echo > $CATALINA_HOME/logs/catalina.out

echo "Starting Tomcat..."
$CATALINA_HOME/bin/startup.sh
sleep 20

# Restore the car images folder before starting the browser
if [ -d $TARGET_DIR ];
then
    echo "Moving car listings in: " $TARGET_DIR
    mv $WEBAPPS/cars $TARGET_DIR/cars
fi

# Open the browser
open "http://localhost:8080/$APP_NAME"
