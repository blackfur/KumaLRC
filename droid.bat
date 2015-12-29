rem ---- define variables ----
setlocal
set project=kumalrc
set alias=shiro
set package=com.shiro.linguistics
set DEVICE=-s 192.168.3.115:5555
rem set DEVICE=-s emulator-5554
rem set DEVICE=-s ?
set module=linguistics
set component=com.shiro.memo.RedactActivity
set apk=%module%/build/outputs/apk/%module%-release.apk
set keycode=4
call e:\.tool\droid.bat %1
