@echo off
@title TagWall
set CLASSPATH=.;dist\*;
java com.kazvoeten.tagwall.client.Client
pause