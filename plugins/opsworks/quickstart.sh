#! /bin/bash

rm -rf /Users/brovolc/.m2/repository/com/jostens/opsworks

rm -rf ./target ./work

mvn package -DskipTests=true
mvn install -DskipTests=true

mvn hpi:run
