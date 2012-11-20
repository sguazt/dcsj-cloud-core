#!/bin/sh

cp="build/classes:test/build/classes"
for j in lib/*.jar; do
    cp+=":$j"
done

#java -cp "$cp" test.unit.TestMiniCloudRawSocketInformationProvider
java -cp "$cp" test.unit.TestMiniCloudRawSocketServiceScheduler
