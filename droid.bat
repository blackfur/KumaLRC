@setlocal
set project=kumalrc
set alias=shiro
rem set package=com.shiro.linguistics
set package=com.shiro.player.KumaPlayerApp
@rem set DEVICE=-s 192.168.3.115:5555
rem set DEVICE=-s emulator-5554
rem set DEVICE=-s ?
rem set device=-s 00664B1A8EA6
set module=linguistics
@rem @set component=com.shiro.memo.RedactActivity
rem @set component=com.shiro.linguistics.player/com.shirokuma.musicplayer.MainActivity
rem @set component=com.shiro.linguistics.player/com.shirokuma.musicplayer.musiclib.ScanActivity
@set component=com.shiro.player.KumaPlayerApp/com.shirokuma.musicplayer.musiclib.ScanActivity
@rem set apk=%module%/build/outputs/apk/%module%-release.apk
@set keycode=4
@set logtag=kumaplayer
@call e:\.tool\droid.bat %1
