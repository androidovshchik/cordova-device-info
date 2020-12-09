package ru.androidovshchik;

import android.database.ContentObserver;
import android.net.Uri;

public class ScreenshotObserver extends ContentObserver {

    @Override
    public boolean deliverSelfNotifications() {
        Log.d(TAG, "deliverSelfNotifications");
        return super.deliverSelfNotifications();
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        Log.d(TAG, "onChange " + uri.toString());
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
        super.onChange(selfChange, uri);
    }
}