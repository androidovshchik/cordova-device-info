module.exports = {
    // see https://github.com/nisrulz/easydeviceinfo/wiki/Usage
    makeReflection: function (params, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "DeviceInfoPlugin", "callReflection", params);
    },
    retrieveIMEI: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "DeviceInfoPlugin", "retrieveIMEI");
    },
    getZoneOffset: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "DeviceInfoPlugin", "getZoneOffset");
    },
    getLanguages: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "DeviceInfoPlugin", "getLanguages");
    },
    observeScreenshots: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "DeviceInfoPlugin", "observeScreenshots");
    }
};
