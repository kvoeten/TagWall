#!/bin/sh
export CLASSPATH=".:dist/*" 
java com.kazvoeten.tagwall.client.Client -Dtinylog.level=info