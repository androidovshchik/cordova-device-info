# cordova-device-info plugin

Simple plugin that provides info about Android device

## Getting started

Create a new Cordova Project

    $ cordova create example com.example Example
    
Install the plugin

    $ cd example
    $ cordova plugin add https://github.com/androidovshchik/cordova-device-info.git
    

Edit `www/js/index.js` and add the following code inside `onDeviceReady`

```js
DeviceInfo.(function(result) {
    if (result) {
    }
});
```

Install Android platform

    cordova platform add android
    
Run the app

    cordova run android
