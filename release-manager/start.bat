@echo off
title Release Manager Control Panel
echo Starting Release Manager...
start http://localhost:3005
node server.js
pause
