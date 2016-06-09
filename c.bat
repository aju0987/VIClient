@echo on

del classes\com\vmware\viclient\connectionmgr\*.class
del classes\com\vmware\viclient\helper\*.class
del classes\com\vmware\viclient\managedentities\*.class
del classes\com\vmware\viclient\ui\*.class
del classes\com\vmware\viclient\ui\graphics\*.class
del classes\com\vmware\viclient\ui\fx\*.class
del VIClient.jar

javac -cp JARs\vim25.jar;JARs\dom4j-1.6.1.jar;JARs\jfxrt.jar;. -d classes src\com\vmware\viclient\helper\*.java src\com\vmware\viclient\connectionmgr\*.java  src\com\vmware\viclient\managedentities\*.java src\com\vmware\viclient\ui\*.java src\com\vmware\viclient\ui\graphics\*.java src\com\vmware\viclient\ui\fx\*.java


rem javac -cp JARs\vim25.jar;JARs\dom4j-1.6.1.jar;JARs\jfxrt.jar;. -d classes src\com\vmware\viclient\connectionmgr\*.java src\com\vmware\viclient\helper\*.java src\com\vmware\viclient\managedentities\*.java src\com\vmware\viclient\ui\*.java src\com\vmware\viclient\ui\graphics\*.java src\com\vmware\viclient\ui\fx\*.java

rem jar -cvfm VIClient.jar manifest.mf classes\com\vmware\viclient\*.class names.properties JARs\vijava50120120518.jar JARs\dom4j-1.6.1.jar  

