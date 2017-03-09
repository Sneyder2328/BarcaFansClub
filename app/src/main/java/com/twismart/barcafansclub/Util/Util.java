package com.twismart.barcafansclub.Util;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.mobile.AWSConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by sneyd on 12/19/2016.
 **/

public class Util {
    public static final String TAG = "Util";

    public static void setLanguageLocale(Resources res, String language){
        // Change locale settings in the app.
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.locale = new Locale(language);
        res.updateConfiguration(conf, dm);
    }

    public static File saveToInternalStorage(Context context, Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(context);

        File directory = cw.getDir("imageDir", Context.MODE_WORLD_WRITEABLE);

        File mypath = new File(directory, "BarcaFansClub" + (System.currentTimeMillis()/1000) + ".png");

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(mypath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mypath;
    }

    public static String generateURL(AmazonS3 amazonS3, String filePath){
        Log.d("Util2", "generateURL2 " + filePath);
        try {
            GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(AWSConfiguration.AMAZON_S3_USER_FILES_BUCKET, "public/" + filePath);
            String url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toString();
            //String url = amazonS3.generatePresignedUrl(AWSConfiguration.AMAZON_S3_USER_FILES_BUCKET, "public/" + filePath, new Date(new Date().getTime() + 1000 *60 * 60)).toString();
            Log.d("Util", "generateURL " + url);
            return url;
        }catch (Exception exception) {
            Log.e(TAG, "getMessage: " + exception.getMessage());
            Log.e(TAG, "getClass: " + exception.getClass());
            Log.e(TAG, "getLocalizedMessage:" + exception.getLocalizedMessage());
            Log.e(TAG, "getCause:    " + exception.getCause());
            return "";
        }
    }

    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static int getWidthAndHeightForImgUser(Activity activity){
        return activity.getWindowManager().getDefaultDisplay().getWidth()/6;
    }
}
