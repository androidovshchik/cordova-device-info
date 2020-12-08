package ru.androidovshchik;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;

import java.util.Date;
import java.util.List;

public class DeviceInfoPlugin extends CordovaPlugin {

    @Override
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) {
        Context context = cordova.getContext();
        switch (action) {
            case "callReflection":
                PluginResult result;
                try {
                    String[] names = data.getString(0).split("\\.");
                    Class<?> cls = Class.forName(names[0]);
                    Object instance;
                    if (names[0].equals("EasyCpuMod")) {
                        instance = cls.newInstance();
                    } else {
                        instance = cls.getConstructor(Context.class).newInstance(context);
                    }
                    Object output = cls.getMethod(names[1]).invoke(instance);
                    if (output instanceof Integer) {
                        result = new PluginResult(PluginResult.Status.OK, (int) output);
                    } else if (output instanceof Long) {
                        result = new PluginResult(PluginResult.Status.OK, (long) output);
                    } else if (output instanceof Float) {
                        result = new PluginResult(PluginResult.Status.OK, (float) output);
                    } else if (output instanceof Boolean) {
                        result = new PluginResult(PluginResult.Status.OK, (boolean) output);
                    } else if (output instanceof String) {
                        result = new PluginResult(PluginResult.Status.OK, (String) output);
                    } else if (output instanceof Date) {
                        result = new PluginResult(PluginResult.Status.OK, output.toString());
                    } else if (output instanceof Integer[]) {
                        result = new PluginResult(PluginResult.Status.OK, new JSONArray(output));
                    } else if (output instanceof List<?>) {
                        result = new PluginResult(PluginResult.Status.OK, new JSONArray(output));
                    } else {
                        result = new PluginResult(PluginResult.Status.NO_RESULT);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    result = new PluginResult(PluginResult.Status.ERROR, e.getMessage());
                }
                callbackContext.sendPluginResult(result);
                break;
            default:
                return false;
        }
        return true;
    }
}
