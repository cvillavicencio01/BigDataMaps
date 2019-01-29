#!/bin/bash

echo __Shell built-in__

source /espace/Auber_PLE-005/user-env.sh 

echo __Maven installation__
mvn install

echo __Generating the JavaDoc__
##cd  ./src/main/java

echo Please go here to check the documentation :


echo __Spark__
spark-submit --master yarn --deploy-mode cluster --class bigdata.ProjetMaps --executor-memory 5GB --num-executors 14 --executor-cores 3 ./target/ProjetMaps-0.0.1.jar 