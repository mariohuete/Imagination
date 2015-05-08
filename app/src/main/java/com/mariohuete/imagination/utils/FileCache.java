package com.mariohuete.imagination.utils;

import android.content.Context;
import android.util.Log;

import com.mariohuete.imagination.R;

import java.io.File;


/**
 *
 * Created by Mario Huete Jiménez on 08/05/15.
 */
public class FileCache {
    private File cacheDir;

    public FileCache(Context context) {
        // Find the dir at SDCARD to save cached images
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            // If SDCARD is mounted (SDCARD is present on device and mounted)
            cacheDir = new File(android.os.Environment.getExternalStorageDirectory(),
                    context.getString(R.string.external));
        }
        else {
            // If checking on simulator the create cache dir in your application context
            cacheDir = context.getCacheDir();
        }
        if(!cacheDir.exists()) {
            // Create cache dir in your application context
            if(cacheDir.mkdirs())
                Log.d("File", "Directory successfully created");
            else
                Log.d("File", "Couldn't create directory");
        }
    }

    public File getFile(String url) {
        // Identify images by hashcode or encode by URLEncoder.encode.
        String filename = String.valueOf(url.hashCode());
        return new File(cacheDir, filename);
    }

    public void clear(){
        // List all files inside cache directory
        File[] files = cacheDir.listFiles();
        if(files == null)
            return;
        // Delete all cache directory files
        for(File f : files) {
            if (f.delete())
                Log.d("File", "Directory successfully deleted");
            else
                Log.d("File", "Couldn't delete directory");
        }
    }

}