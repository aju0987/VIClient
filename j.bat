del VIClient.jar
del VIClient.tar
cd classes
jar -cvfm VIClient.jar ..\manifest.mf .  
move VIClient.jar ..
cd ..
jar -uvf VIClient.jar JARs\names.properties JARs\VMwareClientLogo.png 
tar -cvf VIClient.tar VIClient.jar dom4j-1.6.1.jar vijava50120120518.jar jr.bat

