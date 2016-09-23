#!/bin/bash

ndk-build NDK_DEBUG=1
adb root
for f in $(ls libs/x86); 
	do adb push libs/x86/$f /data/aj;
done;
adb shell "chmod 777 /data/aj/aj && LD_LIBRARY_PATH=/data/aj /data/aj/aj"
adb kill-server
