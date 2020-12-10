package ru.androidovshchik;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.telephony.TelephonyManager;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@SuppressLint("MissingPermission")
public class DeviceInfoPlugin extends CordovaPlugin {

    private ScreenshotObserver observer;

    @Override
    @SuppressLint("HardwareIds")
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public boolean execute(String action, JSONArray data, final CallbackContext callbackContext) {
        Context context = cordova.getContext();
        PluginResult result;
        switch (action) {
            case "callReflection":
                try {
                    String[] names = data.getString(0).split("\\.");
                    @SuppressWarnings("SpellCheckingInspection")
                    Class<?> cls = Class.forName("github.nisrulz.easydeviceinfo.base." + names[0]);
                    Object instance;
                    if (names[0].equals("EasyCpuMod")) {
                        instance = cls.newInstance();
                    } else {
                        instance = cls.getConstructor(Context.class).newInstance(context);
                    }
                    Object[] params = null;
                    Class<?>[] classes = null;
                    if (data.length() > 1) {
                        params = new Object[data.length() - 1];
                        classes = new Class<?>[data.length() - 1];
                        for (int i = 0; i < data.length() - 1; i++) {
                            Object value = data.get(i + 1);
                            params[i] = value;
                            if (value != null) {
                                Class<?> type = value.getClass();
                                if (type.equals(Integer.class)) {
                                    classes[i] = int.class;
                                } else if (type.equals(Long.class)) {
                                    classes[i] = long.class;
                                } else if (type.equals(Float.class)) {
                                    classes[i] = float.class;
                                } else if (type.equals(Boolean.class)) {
                                    classes[i] = boolean.class;
                                } else {
                                    classes[i] = type;
                                }
                            } else {
                                throw new IllegalArgumentException("Null arguments are not supported");
                            }
                        }
                    }
                    Object output = cls.getMethod(names[1], classes).invoke(instance, params);
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    result = new PluginResult(PluginResult.Status.ERROR, "Android Q has restricted to access for both IMEI and serial no");
                    callbackContext.sendPluginResult(result);
                    return true;
                }
                if (cordova.hasPermission(Manifest.permission.READ_PHONE_STATE)) {
                    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    String imei;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        imei = tm.getImei();
                    } else {
                        imei = tm.getDeviceId();
                    }
                    result = new PluginResult(PluginResult.Status.OK, imei);
                } else {
                    cordova.requestPermission(this, 100, Manifest.permission.READ_PHONE_STATE);
                    result = new PluginResult(PluginResult.Status.NO_RESULT);
                }
                callbackContext.sendPluginResult(result);
                break;
            case "getZoneOffset":
                int offset = TimeZone.getDefault().getOffset(System.currentTimeMillis());
                result = new PluginResult(PluginResult.Status.OK, offset);
                callbackContext.sendPluginResult(result);
                break;
            case "getLanguages":
                String output;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    LocaleList locales = Resources.getSystem().getConfiguration().getLocales();
                    output = locales.toString();
                } else {
                    output = Resources.getSystem().getConfiguration().locale.toString();
                }
                result = new PluginResult(PluginResult.Status.OK, output);
                callbackContext.sendPluginResult(result);
            case "observeScreenshots":
                if (cordova.hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    cordova.getThreadPool().execute(new Runnable() {

                        @Override
                        public void run() {
                            Context context = cordova.getContext();
                            if (context != null) {
                                if (observer == null) {
                                    observer = new ScreenshotObserver(context);
                                }
                                observer.addCallback(callbackContext);
                                observer.registerIfNeeded();
                            }
                        }
                    });
                } else {
                    cordova.requestPermission(this, 200, Manifest.permission.READ_EXTERNAL_STORAGE);
                    result = new PluginResult(PluginResult.Status.NO_RESULT);
                    callbackContext.sendPluginResult(result);
                }
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    public void onDestroy() {
        observer.release();
    }
}
