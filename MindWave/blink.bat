@setlocal
@echo off
@echo Running with prms: %*
::
set CP=.\classes
set CP=%CP%;D:\_mywork\dev-corner\olivsoft\all-3rd-party\rxtx.distrib\RXTXcomm.jar
set CP=%CP%;D:\OlivSoft.git\raspberry-pi4j-samples\Serial.IO\classes 
::
set JAVA_OPT=-Djava.library.path=D:\_mywork\dev-corner\olivsoft\all-3rd-party\rxtx.distrib\win64 
:: prms: -br:115200 -port:COM25 
:: or -list-ports 
java -cp %CP% %JAVA_OPT%  mindwave.samples.io.BlinkDetector %*
@endlocal