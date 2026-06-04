@echo off
set "JAVA_HOME=C:\Program Files\Java\jdk1.8.0_221"
set "PATH=%JAVA_HOME%\bin;%PATH%"
"%~dp0.maven\apache-maven-3.8.8\bin\mvn.cmd" %*
