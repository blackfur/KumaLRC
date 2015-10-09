set package_name=com.shirokuma.musicplayer
set root=e:\work\kumalrc
if "%1"=="/r" goto reinstall
goto end
:reinstall
adb uninstall %package_name% 
if exist app-release.apk adb install app-release.apk && goto end
cd %root%\app\build\outputs\apk
if exist app-release.apk adb install app-release.apk
goto end
rem setlocal enabledelayedexpansion 
rem start check signature
set /p storepass= Enter store passphrase: 
if "%storepass%"=="" set storepass=Aa123456
set /p keypass= Enter key passphrase: 
if "%keypass%"=="" set keypass=Aa123456
cd E:\work\kumalrc-gradle 
keytool -list -v -alias shiro -keystore kumalrc.jks -storepass %storepass% -keypass %keypass%
rem end check signature
:end
cd %root%
echo.
date /t
