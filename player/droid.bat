@setlocal
@set debugjks=C:\Users\Administrator.PC-20150720BPSO\.android\debug.keystore
@set debugjksAlias=AndroidDebugKey
@set alias=%debugjksAlias%
@set package=com.shiro.cocos2dtest
@rem set DEVICE=-s 192.168.3.121:5555
@rem set DEVICE=-s emulator-5554
@set DEVICE=-s ?
@set module=player
@rem @set keystore=%project%.jks
@set keystore=%debugjks%
@set storepass=android
@set keypass=%storepass%
@set component=org.cocos2dx.cpp.AppActivity
@set apk=bin\%project%-debug.apk
@set keycode=4
@set TARGET=%module%\build\outputs\apk\%module%-release-unsigned.apk
@set tests=com.shiro.driverstore.common.HttpUtilsTest
@set directory=E:/work/driverstore
@set testsLog=file:///%directory%/app/build/reports/tests/release/classes/
@set androidTestLog=file:///%directory%/app/build/outputs/reports/androidTests/connected/index.html
@set logTag=kumaplayer
@call e:\.tool\droid.bat %1
