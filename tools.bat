setlocal
rem set DEVICE=-s 192.168.3.121:5555
rem set device=
rem set device=emulator-5554
set device=-s ?
set PACKAGE=com.shirokuma.musicplayer
set PROJECT_PATH=e:\work\kumalrc
set project=kumalrc
set module=player
if "%1"=="reinstall" goto reinstall
if [%1]==[sign] goto sign
if [%1]==[release] goto release
if [%1]==[debug] goto debug
if [%1]==[run] goto run
goto :eof
:sign
set ALIAS=shiro
rem set TARGET=bin\%PROJECT%-release-unsigned.apk
set TARGET=%module%\build\outputs\apk\%module%-release-unsigned.apk
jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore %PROJECT%.jks %TARGET% %ALIAS%
jarsigner -verify -verbose -certs %TARGET%
del %module%\build\outputs\apk\%module%-release.apk
rename %TARGET% %module%-release.apk
rem ls bin
goto :eof
:reinstall
adb uninstall %PACKAGE% 
rem if exist app-release.apk adb install app-release.apk && goto :eof
cd %PROJECT_PATH%\app\build\outputs\apk
if exist app-release.apk adb install app-release.apk
goto :eof
:release
set apk=%module%/build/outputs/apk/%module%-release.apk
call :uninstall
call :install
call :run
call :log
exit /b 0
:debug
set apk=%module%/build/outputs/apk/%module%-debug.apk
call :uninstall
call :install
call :run
call :log
goto :eof
:install
adb %DEVICE% install %apk%
exit /b 0
:uninstall
adb %DEVICE% uninstall %PACKAGE%
exit /b 0
:run
set COMPONENT=%package%.musiclib.MusicListActivity
adb %DEVICE% shell am start -n %PACKAGE%/%COMPONENT%
exit /b 0
:log
adb %device% logcat *:e
exit /b 0
