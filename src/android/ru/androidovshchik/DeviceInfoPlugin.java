package ru.androidovshchik;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
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

@SuppressLint("MissingPermission")
public class DeviceInfoPlugin extends CordovaPlugin {

    private static final int REQUEST_PHONE = 1;
    private static final int REQUEST_STORAGE = 2;

    private ScreenshotObserver observer;

    private CallbackContext callbackPhone;
    private CallbackContext callbackStorage;

    @Override
    @SuppressLint("HardwareIds")
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
                if (checkPermission(Manifest.permission.READ_PHONE_STATE)) {
                    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    String imei;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        imei = tm.getImei();
                    } else {
                        imei = tm.getDeviceId();
                    }
                    result = new PluginResult(PluginResult.Status.OK, imei);
                    callbackContext.sendPluginResult(result);
                } else {
                    callbackPhone = callbackContext;
                    if (!requestPermission(Manifest.permission.READ_PHONE_STATE, REQUEST_PHONE)) {
                        result = new PluginResult(PluginResult.Status.ERROR, "You should show UI with rationale");
                        callbackContext.sendPluginResult(result);
                        callbackPhone = null;
                    }
                }
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
                if (checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    if (observer == null) {

                    }
                    context.getContentResolver().registerContentObserver(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        true,
                        observer
                    );
                } else {
                    callbackStorage = callbackContext;
                    if (!requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_STORAGE)) {
                        result = new PluginResult(PluginResult.Status.ERROR, "You should show UI with rationale");
                        callbackContext.sendPluginResult(result);
                        callbackStorage = null;
                    }
                }
                break;
            default:
                return false;
        }
        return true;
    }

    private boolean checkPermission(String permission) {
        Context context = cordova.getContext();
        String packageName = context.getPackageName();
        PackageManager pm = context.getPackageManager();
        return pm.checkPermission(permission, packageName) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean requestPermission(String permission, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Activity activity = cordova.getActivity();
            if (activity == null || activity.shouldShowRequestPermissionRationale(permission)) {
                return false;
            }
            activity.requestPermissions(new String[]{permission}, requestCode);
        }
        return true;
    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PHONE:
                if (checkPermission(Manifest.permission.READ_PHONE_STATE)) {
                    execute("retrieveIMEI", (JSONArray) null, callbackPhone);
                }
                callbackPhone = null;
                break;
            case REQUEST_STORAGE:
                if (checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    execute("observeScreenshots", (JSONArray) null, callbackStorage);
                }
                callbackStorage = null;
        }
    }
}
