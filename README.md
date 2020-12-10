## cordova-device-info plugin

Simple plugin that provides info about Android device

### Getting started

Create a new Cordova Project

    $ cordova create example com.example Example
    
Install the plugin

    $ cd example
    $ cordova plugin add https://github.com/androidovshchik/cordova-device-info.git
    

Edit `www/js/index.js` and add the following code inside `onDeviceReady`

```js
// it wrappers this library https://github.com/nisrulz/easydeviceinfo/wiki/Usage#easydevicemod
DeviceInfo.makeReflection(['EasyMemoryMod.getTotalRAM'], function(result) {
    DeviceInfo.makeReflection(['EasyMemoryMod.convertToMb', result], function(result) {
        console.log(`Total RAM in mb: ${result}`);
    });
});
DeviceInfo.retrieveIMEI(function(result) {
    console.log(`IMEI: ${result}`);
}, function(error) {
    console.log(`IMEI error: ${error}`);
});
DeviceInfo.getLanguages(function(result) {
    console.log(`Languages: ${result}`);
});
DeviceInfo.getZoneOffset(function(result) {
    console.log(`Timezone offset in millis: ${result}`);
});
DeviceInfo.observeScreenshots(function(result) {
    if (result != null && result.toLowerCase().includes('screenshot')) {
        console.log(`Screenshot path: ${result}`);
    }
}, function(error) {
    console.log(`Observe error: ${error}`);
});
```

Install Android platform

    cordova platform add android
    
Run the app

    cordova run android

### Proguard

```
-keep github.nisrulz.easydeviceinfo.base.** { *; }
```
