package com.twismart.barcafansclub.Fragments;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.twismart.barcafansclub.Activities.UserProfileActivity;
import com.twismart.barcafansclub.Activities.ViewPostActivity;
import com.twismart.barcafansclub.Adapters.NewsRecyclerViewAdapter;
import com.twismart.barcafansclub.Adapters.NotificationsRecyclerViewAdapter;
import com.twismart.barcafansclub.AppController;
import com.twismart.barcafansclub.Pojos.Notification;
import com.twismart.barcafansclub.Pojos.Post;
import com.twismart.barcafansclub.R;
import com.twismart.barcafansclub.Util.Constants;
import com.twismart.barcafansclub.Util.CustomJsonRequest;
import com.twismart.barcafansclub.Util.PreferencesLogin;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class NotificationsFragment extends Fragment {

    private static final String TAG = "NotificationsFragment";
    PreferencesLogin preferencesLogin;
    private NotificationsRecyclerViewAdapter notificationsRecyclerViewAdapter;
    public static NotificationsFragment instance = null;

    public NotificationsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notification, container, false);

        instance = this;

        preferencesLogin = new PreferencesLogin(getContext());

        notificationsRecyclerViewAdapter = new NotificationsRecyclerViewAdapter(getContext(), getActivity());

        //
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.listNotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(notificationsRecyclerViewAdapter);

        //
        getNotifications();

        return v;
    }

    private void getNotifications(){
        final Map<String, String> params = new HashMap<>();
        params.put(Constants.Notification.NOTIFICATION_RECEIVER.value, preferencesLogin.getMyId());
        params.put(Constants.User.LANGUAGE.value, preferencesLogin.getMyLanguage());

        CustomJsonRequest getNotificationsRequest = new CustomJsonRequest(Request.Method.POST, Constants.Urls.GET_NOTIFICATIONS.link, params, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, "response getNotificationsRequest " + response.toString());
                ArrayList<Notification> listNotifications = new ArrayList<>();
                if(!response.toString().equals("false")){
                    for (int n = 0 ; n < response.length() ; n++){
                        try{
                            JSONObject jsonObject = response.getJSONObject(n);
                            listNotifications.add(new Notification(jsonObject.getString(Constants.Notification.NOTIFICATE_SENDER.value), jsonObject.getString(Constants.Notification.STAFF_ID.value),
                                    jsonObject.getString(Constants.Notification.NOTIFICATION_TYPE.value), jsonObject.getString(Constants.Notification.DATE.value),
                                    jsonObject.getString(Constants.Notification.NAME.value), jsonObject.getString(Constants.Notification.IMG_PROFILE.value),
                                    jsonObject.getString(Constants.Notification.IMG_COVER.value)));
                        }
                        catch (Exception e){
                            Log.e(TAG, "Exception getNotificationsRequest " + e.getMessage());
                        }
                    }
                }
                notificationsRecyclerViewAdapter.setListNotifications(listNotifications);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: getNotificationsRequest " + error.getMessage());
            }
        });
        AppController.getInstance().addToRequestQueue(getNotificationsRequest);
    }


    public static class NotificationService extends FirebaseMessagingService {

        public static final String TAG = "NotificationService";

        @Override
        public void onMessageReceived(RemoteMessage remoteMessage) {
            Log.d(TAG, "onMessageReceived " + remoteMessage.getData().toString());

            if(instance != null) {
                Log.d(TAG, "if (instance != null) {");
                instance.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MediaPlayer mp = MediaPlayer.create(instance.getContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                        //  mp.prepare();
                        mp.start();
                    }
                });
            } else {
                Log.d(TAG, "else {");
                final String notificationType = remoteMessage.getData().get("notificationType");
                if(notificationType.equals("newFollower") && remoteMessage.getData().containsKey("notificationType") && remoteMessage.getData().containsKey("userId") &&
                        remoteMessage.getData().containsKey("name") && remoteMessage.getData().containsKey("imgProfile") && remoteMessage.getData().containsKey("imgCover")){
                    final String fromUserId = remoteMessage.getData().get("userId");
                    final String fromName = remoteMessage.getData().get("name");
                    final String fromImgProfile = remoteMessage.getData().get("imgProfile");
                    final String fromImgCover = remoteMessage.getData().get("imgCover");

                    Intent intent = new Intent(this, UserProfileActivity.class);
                    intent.putExtra(Constants.User.USER_ID.value, fromUserId);
                    intent.putExtra(Constants.User.NAME.value, fromName);
                    intent.putExtra(Constants.User.IMG_PROFILE.value, fromImgProfile);
                    intent.putExtra(Constants.User.IMG_COVER.value, fromImgCover);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(String.format(getString(R.string.notification_text_followed), fromName))
                            .setContentText(fromName)
                            .setAutoCancel(true)
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                            .setContentIntent(pendingIntent);

                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(0, builder.build());
                }
                else if(notificationType.equals("likeMyPost") && remoteMessage.getData().containsKey("notificationType") && remoteMessage.getData().containsKey("postId") &&
                        remoteMessage.getData().containsKey("name")){
                    final String fromPostId = remoteMessage.getData().get("postId");
                    final String fromName = remoteMessage.getData().get("name");

                    Intent intent2 = new Intent(this, ViewPostActivity.class);
                    intent2.putExtra(Constants.Post.POST_ID.value, fromPostId);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent2, PendingIntent.FLAG_ONE_SHOT);

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(String.format(getString(R.string.notification_text_liked_post), fromName))
                            .setContentText(fromName)
                            .setAutoCancel(true)
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                            .setContentIntent(pendingIntent);

                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(0, builder.build());
                }
                else if(notificationType.equals("likeMyComment") && remoteMessage.getData().containsKey("notificationType") && remoteMessage.getData().containsKey("postId") &&
                        remoteMessage.getData().containsKey("name")){
                    final String fromPostId = remoteMessage.getData().get("postId");
                    final String fromName = remoteMessage.getData().get("name");

                    Intent intent2 = new Intent(this, ViewPostActivity.class);
                    intent2.putExtra(Constants.Post.POST_ID.value, fromPostId);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent2, PendingIntent.FLAG_ONE_SHOT);

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(String.format(getString(R.string.notification_text_liked_comment), fromName))
                            .setContentText(fromName)
                            .setAutoCancel(true)
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                            .setContentIntent(pendingIntent);

                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(0, builder.build());
                }
                else if(notificationType.equals("commentMyPost") && remoteMessage.getData().containsKey("notificationType") && remoteMessage.getData().containsKey("postId") &&
                        remoteMessage.getData().containsKey("name")){
                    final String fromPostId = remoteMessage.getData().get("postId");
                    final String fromName = remoteMessage.getData().get("name");

                    Intent intent2 = new Intent(this, ViewPostActivity.class);
                    intent2.putExtra(Constants.Post.POST_ID.value, fromPostId);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent2, PendingIntent.FLAG_ONE_SHOT);

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(String.format(getString(R.string.notification_text_comment_post), fromName))
                            .setContentText(fromName)
                            .setAutoCancel(true)
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                            .setContentIntent(pendingIntent);

                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(0, builder.build());
                }
            }
        }
    }
}
