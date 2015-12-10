setlocal
set DEVICE=-s 192.168.3.103:5555
set PACKAGE=com.shirokuma.musicplayer
set PROJECT_PATH=e:\work\kumalrc
set project=kumalrc
set module=app
if "%1"=="reinstall" goto reinstall
if [%1]==[sign] goto sign
if [%1]==[release] goto release
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
call :install
exit /b 0
:install
call :uninstall
adb %DEVICE% install %apk%
call :run
call :log
exit /b 0
:uninstall
adb %DEVICE% uninstall %PACKAGE%
exit /b 0
:run
rem set COMPONENT=android.app.NativeActivity
set COMPONENT=%package%.WelcomeActivity
adb %DEVICE% shell am start -n %PACKAGE%/%COMPONENT%
exit /b 0
:log
adb %device% logcat *:e
exit /b 0
