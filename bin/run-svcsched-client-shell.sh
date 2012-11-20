#!/bin/sh

cp="build/classes:test/build/classes"
for j in lib/*.jar; do
    cp+=":$j"
done

java -cp "$cp" test.mock.it.unipmn.di.dcs.cloud.middleware.minicloud.model.sched.RawSocketServiceSchedulerClientShell 1 0.0.0.0 7778
#java -cp "$cp" test.mock.it.unipmn.di.dcs.cloud.middleware.minicloud.model.sched.RawSocketServiceSchedulerClientShell 2 0.0.0.0 7777 0.0.0.0 7778
#java -cp "$cp" test.mock.it.unipmn.di.dcs.cloud.middleware.minicloud.model.sched.RawSocketServiceSchedulerClientShell 2 0.0.0.0 7777 172.22.16.122 12000
