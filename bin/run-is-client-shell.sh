#!/bin/sh

cp="build/classes:test/build/classes"
for j in lib/*.jar; do
    cp+=":$j"
done

java -cp "$cp" test.mock.it.unipmn.di.dcs.cloud.middleware.minicloud.model.is.RawSocketInformationProviderClientShell 0.0.0.0 7777
