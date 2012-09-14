@echo off
SET CLASSTEMP=%CLASSPATH%;
SET CLASSPATH=%CLASSPATH%;./lib/jga-20060602.jar;./lib/nsga-20060602.jar;./lib/ioutils-20051103.jar;./bin
SET CLASSPATH=%CLASSPATH%;.
java BOFLPMain JGAConfigFNCUncapBin.ini
SET CLASSPATH=CLASSTEMP;
echo PRESS CTRL-Z+Return   TO EXIT
more
