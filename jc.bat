@echo on

del VIClient.jar
cd classes
jar -cvfm ..\VIClient.jar ..\manifest.mf com 
cd ..
jar -uvf VIClient.jar JARs\names.properties JARs\VMwareClientLogo.png
rem jar -uvf vijava50120120518.jar JARs\dom4j-1.6.1.jar  


