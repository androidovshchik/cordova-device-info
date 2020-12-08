module.exports = {
    makeReflection: function (params, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "DeviceInfoPlugin", "callReflection", params);
    },
    getTimeZone: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "DeviceInfoPlugin", "getTimeZone");
    },
    getLanguages: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "DeviceInfoPlugin", "getLanguages");
    }
};
