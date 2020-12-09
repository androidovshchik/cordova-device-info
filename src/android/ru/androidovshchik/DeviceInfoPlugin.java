package ru.androidovshchik;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class DeviceInfoPlugin extends CordovaPlugin {

    @Override
    @SuppressLint({"MissingPermission", "HardwareIds"})
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) {
        Context context = cordova.getContext();
        PluginResult result;
        switch (action) {
            case "callReflection":
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
                        List<String> values = new ArrayList<>();
                        for (Object item : ((List<?>) output)) {
                            values.add(item.toString());
                        }
                        result = new PluginResult(PluginResult.Status.OK, new JSONArray(values));
                    } else {
                        result = new PluginResult(PluginResult.Status.NO_RESULT);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    result = new PluginResult(PluginResult.Status.ERROR, e.getMessage());
                }
                callbackContext.sendPluginResult(result);
                break;
            case "retrieveIMEI":
                String packageName = context.getPackageName();
                PackageManager pm = context.getPackageManager();
                if (pm.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, packageName) == PackageManager.PERMISSION_GRANTED) {
                    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    String imei;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        imei = tm.getImei();
                    } else {
                        imei = tm.getDeviceId();
                    }
                    result = new PluginResult(PluginResult.Status.OK, imei);
                } else {
                    cordova.getThreadPool().execute(new Runnable() {

                        @Override
                        public void run() {

                        }
                    });
                }
                result = new PluginResult(PluginResult.Status.OK, offset);
                break;
            case "getZoneOffset":
                int offset = TimeZone.getDefault().getOffset(System.currentTimeMillis());
                result = new PluginResult(PluginResult.Status.OK, offset);
                callbackContext.sendPluginResult(result);
                break;
            case "getLanguages":
                String[] locales = Resources.getSystem().getAssets().getLocales();
                try {
                    result = new PluginResult(PluginResult.Status.OK, new JSONArray(locales));
                } catch (JSONException e) {
                    e.printStackTrace();
                    result = new PluginResult(PluginResult.Status.JSON_EXCEPTION);
                }
                callbackContext.sendPluginResult(result);
            case "observeScreenshots":
                context.getContentResolver().registerContentObserver(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    true,

                    );
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
        super.onRequestPermissionResult(requestCode, permissions, grantResults);
    }
}
