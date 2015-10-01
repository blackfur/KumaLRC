rem setlocal enabledelayedexpansion 
rem start check signature
set /p storepass= Enter store passphrase: 
if "%storepass%"=="" set storepass=Aa123456
set /p keypass= Enter key passphrase: 
if "%keypass%"=="" set keypass=Aa123456
cd E:\work\kumalrc-gradle 
keytool -list -v -alias shiro -keystore kumalrc.jks -storepass %storepass% -keypass %keypass%
rem end check signature
