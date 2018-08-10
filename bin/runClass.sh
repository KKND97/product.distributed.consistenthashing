#!/bin/bash
cd ..
jars="./target/classes"
dependencies=./target/dependencies

for jar in $(ls ${dependencies});
  do
	jars=$jars":${dependencies}/"$jar;
  done;
export jars=$jars 
#echo java -cp $jars $@   
time java -cp $jars $@

wait;

