module.exports = {
    makeReflection: function (params, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "DeviceInfoPlugin", "callReflection", params);
    }
};
