jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore oizom.keystore release-unsigned.apk OIZOM
zipalign -v 4 release-unsigned.apk OIZOM.apk
hetvi_1234
