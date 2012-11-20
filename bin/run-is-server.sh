#!/bin/sh

cp="build/classes:test/build/classes"
for j in lib/*.jar; do
    cp+=":$j"
done

#java -cp "$cp" test.mock.it.unipmn.di.dcs.cloud.middleware.minicloud.model.is.RawSocketInformationProviderServer 7777 10 20
java -cp "$cp" test.mock.it.unipmn.di.dcs.cloud.middleware.minicloud.model.is.RawSocketInformationProviderServer 7777 10 20 7778
