setlocal
set project=kumalrc
set alias=shiro
set package=com.shiro.linguistics
set DEVICE=-s 192.168.3.121:5555
rem set DEVICE=-s emulator-5554
rem set DEVICE=-s ?
set module=linguistics
rem --- options ---
if [%1]==[monkey] goto monkey
if [%1]==[buildDebug] goto buildDebug
if [%1]==[buildRelease] goto buildRelease
if [%1]==[py] goto py
if [%1]==[lock] goto lock
if [%1]==[emu] goto emu
if [%1]==[install] goto install
if [%1]==[run] goto run
if [%1]==[activity] goto activity
if [%1]==[capture] goto capture
if [%1]==[list] goto list
if [%1]==[create] goto create
if [%1]==[uninstall] goto uninstall
if [%1]==[release] goto release
if [%1]==[debug] goto debug
rem check keystore
if [%1]==[sha1] goto sha1
rem generate keystore
if [%1]==[genkey] goto genkey
if [%1]==[sign] goto sign
if [%1]==[log] goto log
goto :eof
:buildRelease
gradlew :%module%:assembleRelease
goto :eof
:buildDebug
gradlew :%module%:assembleDebug
goto :eof
:log
adb %device% logcat *:e
exit /b 0
:sign
set TARGET=%module%\build\outputs\apk\%module%-release-unsigned.apk
jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore %PROJECT%.jks %TARGET% %ALIAS%
jarsigner -verify -verbose -certs %TARGET%
del %module%\build\outputs\apk\%module%-release.apk
rename %TARGET% %module%-release.apk
goto :eof
:genkey
keytool -genkey -v -keystore %PROJECT%.jks -alias %PROJECT% -keyalg RSA -keysize 2048 -validity 10000
dir
goto :eof
:sha1
keytool -list -v -alias %PROJECT% -keystore %PROJECT%.jks -storepass %KEYPSW% -keypass %KEYPSW%
goto :eof
:create
android -v create project -n %PROJECT% -k %PACKAGE% -t android-19 -p .
goto :eof
:list
for %%i in (*) do echo %%i
goto :eof
:capture
set FILENAME=screen-%RANDOM%
adb shell screencap -p /sdcard/%FILENAME%.png
mkdir captures
adb pull /sdcard/%FILENAME%.png captures/
adb shell rm /sdcard/%FILENAME%.png
start "" "%FILENAME%.png"
goto :eof
rem
:emu
rem emulator @Nexus_One_API_15
rem start "" "emulator" @Nexus_One_API_16 -no-boot-anim -scale 0.65 -show-kernel
start "" "%ANDROID_HOME%\AVD Manager.exe"
goto :eof
:activity
set /p COMPONENT=which activity: 
adb shell am start -n %PACKAGE%/%PACKAGE%.%COMPONENT%
goto :eof
rem
:install
adb %DEVICE% install %apk%
exit /b 0
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
exit /b 0
:uninstall
adb %DEVICE% uninstall %PACKAGE%
exit /b 0
rem
:py
cd tools
for %%i in (*.py) do (
	monkeyrunner %%~fi
)
cd ..
goto :eof
:monkey
rem send 512 pseudo-random events
adb shell monkey -p %package% 512
goto :eof
:run
set COMPONENT=%package%.MainActivity
adb %DEVICE% shell am start -n %PACKAGE%/%COMPONENT%
exit /b 0
:lock
rem unlock
adb %device% shell input keyevent 82
goto :eof
:setupNdk
xcopy /e /y %ANDROID_NDK%\samples\hello-jni .\
android update project -p . -t android-8
ndk-build
ant debug install
adb shell am start -n $PACKAGE/${PACKAGE}.$MAIN_ACTIVITY
exit /b 0
