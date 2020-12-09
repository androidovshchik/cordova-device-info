package ru.androidovshchik;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.lang.ref.WeakReference;

public class ScreenshotObserver extends ContentObserver {

    private WeakReference<Context> reference;

    public ScreenshotObserver(Context context) {
        super(null);
        reference = new WeakReference(context);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        if (uri.toString().matches(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString() + "/[0-9]+")) {
            Cursor cursor = null;
            try {
                cursor = getContentResolver().query(uri, new String[]{
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.DATA
                }, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    final String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                    final String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    // TODO: apply filter on the file name to ensure it's screen shot event
                    Log.d(TAG, "screen shot added " + fileName + " " + path);
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
    }
}