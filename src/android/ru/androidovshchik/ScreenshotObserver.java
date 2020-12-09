package ru.androidovshchik;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

public class ScreenshotObserver extends ContentObserver {

    private final WeakReference<Context> reference;

    private final Set<CallbackContext> callbacks = new HashSet<>();

    private boolean hasBeenRegistered = false;

    public ScreenshotObserver(Context context) {
        super(null);
        reference = new WeakReference<>(context);
    }

    public void addCallback(CallbackContext callback) {
        callbacks.add(callback);
    }

    public void registerIfNeeded() {
        if (!hasBeenRegistered) {
            Context context = reference.get();
            if (context != null) {
                context.getContentResolver().registerContentObserver(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    true,
                    this
                );
                hasBeenRegistered = true;
            }
        }
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        String path = getImagePath(uri);
        for (CallbackContext callback : callbacks) {
            PluginResult result = new PluginResult(PluginResult.Status.OK, path);
            result.setKeepCallback(true);
            callback.sendPluginResult(result);
        }
    }

    private String getImagePath(Uri uri) {
        Context context = reference.get();
        if (context == null || uri == null) {
            return null;
        }
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, new String[]{
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATA
            }, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public void release() {
        Context context = reference.get();
        if (context != null) {
            context.getContentResolver().unregisterContentObserver(this);
        }
        callbacks.clear();
    }
}