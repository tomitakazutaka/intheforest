#! /bin/sh

# Author: Kazutaka Tomita <tomita@intheforest.co.jp>

THREADNUM=30
UPLOADDATASIZE="200K.data.dump"
CONNECTHOST="localhost"


JVM_SEARCH_DIRS="/usr/lib/jvm/java-6-openjdk /usr/lib/jvm/java-6-sun"

# If JAVA_HOME has not been set, try to determine it.
if [ -z "$JAVA_HOME" ]; then
    if [ -n `which java` ]; then
        java=`which java`
        while true; do
            if [ -h "$java" ]; then
                java=`readlink "$java"`
                continue
            fi
            break
        done
        JAVA_HOME="`dirname $java`/../"
    else
        for jdir in $JVM_SEARCH_DIRS; do
            if [ -x "$jdir/bin/java" ]; then
                JAVA_HOME="$jdir"
                break
            fi
        done
    fi
fi

while [ 1 ]
do

    java \
        -jar ./cassandra_benchmark.jar \
        -t ${THREADNUM} \
        -H ${CONNECTHOST} \
        --upload_datasize ${UPLOADDATASIZE} \

done

# vi:ai sw=4 ts=4 tw=0 et
