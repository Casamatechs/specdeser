#!/bin/sh -e

JAR=benchmark/build/libs/specdeser-benchmark.jar
HEAP_SIZE=2g

[ -z ${JVM_OPTIONS} ] && JVM_OPTIONS="-XX:AutoBoxCacheMax=20000 -XX:+UseBiasedLocking -XX:BiasedLockingStartupDelay=500 -Xms${HEAP_SIZE} -Xmx${HEAP_SIZE}"
[ -z ${SEED} ] && export SEED=${RANDOM}
[ -z ${SHADOW} ] && echo ./gradlew clean build shadowJar && ./gradlew clean jmhJar

#echo "Running benchmark with Oracle JDK"
#/home/carlos/Oracle-JDK17/bin/java ${JVM_OPTIONS} \
# -XX:-TieredCompilation \
# -jar ${JAR} $*
echo "Running benchmark with GraalVM"
exec mx -p $GRAAL_COMPILER -v vm ${JVM_OPTIONS} \
 -XX:+UnlockDiagnosticVMOptions \
 -XX:-TieredCompilation \
 -jar ${JAR} -Djmh.ignoreLock=true $*
# -Dgraal.Dump=:2 \
# -Dgraal.MethodFilter=kr.sanchez.specdeser.core.jakarta.*.*,org.glassfish.json.*.* \
