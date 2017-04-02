package com.twismart.barcafansclub.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import org.json.JSONObject;

/**
 * Created by sneyd on 12/5/2016.
 **/

public class PreferencesLogin {

    private static final String TAG = "PreferencesLogin";
    private SharedPreferences preferences;

    public PreferencesLogin(Context context){
        preferences = context.getSharedPreferences("pgfhdgh", Context.MODE_PRIVATE);
    }

    public void saveJsonUserData(String jsonUserData){
        Log.d(TAG, "saveJsonUserData " + jsonUserData);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.JSON_USER_DATA, jsonUserData);
        editor.apply();
    }

    public boolean isLogged(){
        return !preferences.getString(Constants.JSON_USER_DATA, "").equals("");
    }

    public void logOut(){
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    public String getMyId(){
        String json = getJsonDataUser();
        try {
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject.getString(Constants.User.USER_ID.value);
        }
        catch (Exception e){
            Log.e(TAG, "Bug in getMyId()");
            return "";
        }
    }

    public void setMyName(String myNewName){
        String json = getJsonDataUser();
        try {
            JSONObject jsonObject = new JSONObject(json);
            jsonObject.put(Constants.User.NAME.value, myNewName);
            saveJsonUserData(jsonObject.toString());
        }
        catch (Exception e){
            Log.d(TAG, "Bug in setMyName() " + e.getMessage());
        }
    }

    public String getMyName(){
        String json = getJsonDataUser();
        try {
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject.getString(Constants.User.NAME.value);
        }
        catch (Exception e){
            Log.d(TAG, "Bug in getMyName() " + e.getMessage());
            return "";
        }
    }

    public String getMyEmail(){
        String json = getJsonDataUser();
        try {
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject.getString(Constants.User.EMAIL.value);
        }
        catch (Exception e){
            Log.d(TAG, "Bug in getMyEmail()");
            return "";
        }
    }

    public void setMyLanguage(String myNewLanguage){
        String json = getJsonDataUser();
        try {
            JSONObject jsonObject = new JSONObject(json);
            jsonObject.put(Constants.User.LANGUAGE.value, myNewLanguage);
            saveJsonUserData(jsonObject.toString());
        }
        catch (Exception e){
            Log.d(TAG, "Bug in setMyLanguage() " + e.getMessage());
        }
    }

    public String getMyLanguage(){
        String json = getJsonDataUser();
        try {
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject.getString(Constants.User.LANGUAGE.value);
        }
        catch (Exception e){
            Log.d(TAG, "Bug in getMyLanguage()");
            return "";
        }
    }

    public String getMyPassword(){
        String json = getJsonDataUser();
        try {
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject.getString(Constants.User.PASSWORD.value);
        }
        catch (Exception e){
            Log.d(TAG, "Bug in getMyPassword()");
            return "";
        }
    }

    public String getMyImgProfile(){
        String json = getJsonDataUser();
        try {
            JSONObject jsonObject = new JSONObject(json);
            Log.d(TAG, "getMyImgProfile " + jsonObject.getString(Constants.User.IMG_PROFILE.value));
            return jsonObject.getString(Constants.User.IMG_PROFILE.value);
        }
        catch (Exception e){
            Log.d(TAG, "Bug in getMyImgProfile()");
            return "";
        }
    }

    public String getMyImgCover(){
        String json = getJsonDataUser();
        try {
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject.getString(Constants.User.IMG_COVER.value);
        }
        catch (Exception e){
            Log.d(TAG, "Bug in getMyImgCover()");
            return "";
        }
    }

    public String getJsonDataUser(){
        Log.d(TAG, "getJsonDataUser " + preferences.getString(Constants.JSON_USER_DATA, ""));
        return preferences.getString(Constants.JSON_USER_DATA, "");
    }
}
