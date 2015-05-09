package com.mariohuete.imagination.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.widget.ImageView;

import com.mariohuete.imagination.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 *
 * Created by Mario Huete Jiménez on 08/05/15.
 */
public class ImageLoader {
    // Initialize MemoryCache
    private static MemoryCache memoryCache = new MemoryCache();
    private static FileCache fileCache;
    // Create Map to store image and image url in key value pair
    private static Map<ImageView, String> imageViews = Collections.synchronizedMap(
            new WeakHashMap<ImageView, String>());
    private static ExecutorService executorService;
    // Handler to display images in UI thread
    private static Handler handler = new Handler();
    // Default image show in list
    private final static int stub_id = R.drawable.holder;


    public ImageLoader(Context context) {
        fileCache = new FileCache(context);
        // Creates a thread pool that reuses a fixed number of
        // threads operating off a shared unbounded queue.
        executorService = Executors.newFixedThreadPool(5);

    }

    public static void displayImage(String url, ImageView imageView) {
        // Store image and url in Map
        imageViews.put(imageView, url);
        // Check image is stored in MemoryCache Map or not
        Bitmap bitmap = memoryCache.get(url);
        if(bitmap != null) {
            // If image is stored in MemoryCache Map then
            // show image in listview row
            imageView.setImageBitmap(bitmap);
        }
        else {
            // Queue Photo to download from url
            queuePhoto(url, imageView);
            // Before downloading image show default image
            imageView.setImageResource(stub_id);
        }
    }

    private static void queuePhoto(String url, ImageView imageView) {
        // Store image and url in PhotoToLoad object
        PhotoToLoad p = new PhotoToLoad(url, imageView);
        // Pass PhotoToLoad object to PhotosLoader runnable class
        // and submit PhotosLoader runnable to executers to run runnable.
        // Submits a PhotosLoader runnable task for execution
        if(executorService != null)
            executorService.submit(new PhotosLoader(p));
    }

    // Task for the queue
    private static class PhotoToLoad {
        public String url;
        public ImageView imageView;
        public PhotoToLoad(String u, ImageView i){
            url = u;
            imageView = i;
        }
    }

    private static class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;
        PhotosLoader(PhotoToLoad photoToLoad){
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            try {
                // Check if image already downloaded
                if(imageViewReused(photoToLoad))
                    return;
                // Download image from web url
                Bitmap bmp = getBitmap(photoToLoad.url);
                // Set image data in Memory Cache
                memoryCache.put(photoToLoad.url, bmp);
                if(imageViewReused(photoToLoad))
                    return;
                // Get bitmap to display
                BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
                // Causes the Runnable bd (BitmapDisplayer) to be added to the message queue.
                // The runnable will be run on the thread to which this handler is attached.
                // BitmapDisplayer run method will call
                handler.post(bd);
            } catch(Throwable th) {
                th.printStackTrace();
            }
        }
    }

    public static void copyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for(;;) {
                // Read byte from input stream
                int count=is.read(bytes, 0, buffer_size);
                if(count == -1)
                    break;
                //Write byte from output stream
                os.write(bytes, 0, count);
            }
        }
        catch(Exception ignored){}
    }

    private static Bitmap getBitmap(String url) {
        File f = fileCache.getFile(url);
        // From SD cache
        // If trying to decode file which not exist in cache return null
        Bitmap b = decodeFile(f);
        if(b != null)
            return b;
        // Download image file from url
        try {
            Bitmap bitmap;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is = conn.getInputStream();
            // Constructs a new FileOutputStream that writes to file
            // if file not exist then it will create file
            OutputStream os = new FileOutputStream(f);
            // See copyStream method
            // It will each pixel from input stream and
            // write pixels to output stream (file)
            copyStream(is, os);
            os.close();
            conn.disconnect();
            // Now file created and going to resize file with defined height
            // Decodes image and scales it to reduce memory consumption
            bitmap = decodeFile(f);
            return bitmap;
        } catch (Throwable ex) {
            ex.printStackTrace();
            if(ex instanceof OutOfMemoryError)
                memoryCache.clear();
            return null;
        }
    }

    // Decodes image and scales it to reduce memory consumption
    private static Bitmap decodeFile(File f){
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream stream1 = new FileInputStream(f);
            BitmapFactory.decodeStream(stream1, null, o);
            stream1.close();
            //Find the correct scale value. It should be the power of 2.
            // Set width/height of recreated image
            final int REQUIRED_SIZE = 85;
            int width_tmp = o.outWidth, height_tmp=o.outHeight;
            int scale = 1;
            while(true) {
                if(width_tmp/2 < REQUIRED_SIZE || height_tmp/2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }
            // Decode with current scale values
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            FileInputStream stream2 = new FileInputStream(f);
            Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
            stream2.close();
            return bitmap;
        } catch (FileNotFoundException ignored) {}
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean imageViewReused(PhotoToLoad photoToLoad){
        String tag = imageViews.get(photoToLoad.imageView);
        // Check url is already exist in imageViews MAP
        return tag == null || !tag.equals(photoToLoad.url);
    }

    // Used to display bitmap in the UI thread
    static class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;
        public BitmapDisplayer(Bitmap b, PhotoToLoad p){
            bitmap = b;
            photoToLoad = p;
        }
        public void run() {
            if(imageViewReused(photoToLoad))
                return;
            // Show bitmap on UI
            if(bitmap!=null)
                photoToLoad.imageView.setImageBitmap(bitmap);
            else
                photoToLoad.imageView.setImageResource(stub_id);
        }
    }

    public static void clearCache() {
        // Clear cache directory downloaded images and stored data in maps
        memoryCache.clear();
        if(fileCache != null)
            fileCache.clear();
    }

}