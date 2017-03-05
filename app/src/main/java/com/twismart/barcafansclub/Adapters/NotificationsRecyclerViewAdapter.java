package com.twismart.barcafansclub.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.twismart.barcafansclub.Activities.MainActivity;
import com.twismart.barcafansclub.Activities.UserProfileActivity;
import com.twismart.barcafansclub.Activities.ViewPostActivity;
import com.twismart.barcafansclub.Pojos.Notification;
import com.twismart.barcafansclub.R;
import com.twismart.barcafansclub.Util.Constants;
import com.twismart.barcafansclub.Util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sneyd on 2/14/2017.
* */

public class NotificationsRecyclerViewAdapter extends RecyclerView.Adapter<NotificationsRecyclerViewAdapter.MyItemNoticiationViewHolder> {

    private static final String TAG = "NotificationsRecyclerViewAdapter";
    private List<Notification> listNotifications = new ArrayList<>();
    private Context context;
    private Activity activity;

    public NotificationsRecyclerViewAdapter(Context context, Activity activity){
        this.context = context;
        this.activity = activity;
    }

    public void setListNotifications(List<Notification> listNotifications) {
        this.listNotifications = listNotifications;
        notifyDataSetChanged();
    }

    @Override
    public MyItemNoticiationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyItemNoticiationViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_notification_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MyItemNoticiationViewHolder holder, int position) {
        holder.bindData(listNotifications.get(position));
    }

    @Override
    public int getItemCount() {
        if(!listNotifications.isEmpty()){
            return listNotifications.size();
        }
        else{
            return 0;
        }
    }

    class MyItemNoticiationViewHolder extends RecyclerView.ViewHolder {

        private final View mView;
        private final TextView textViewNotification, date;
        private final CircleImageView userImg;

        MyItemNoticiationViewHolder(View view){
            super(view);
            mView = view;
            textViewNotification = (TextView) view.findViewById(R.id.textViewNotification);
            date = (TextView) view.findViewById(R.id.date);
            userImg = (CircleImageView) view.findViewById(R.id.userImg);
        }

        private void bindData(final Notification notification){
            switch (notification.getNotificationType()){
                case Notification.NEW_FOLLOWER:
                    textViewNotification.setText(String.format(context.getString(R.string.notification_text_followed), notification.getName()));
                    break;
                case Notification.LIKED_MY_POST:
                    textViewNotification.setText(String.format(context.getString(R.string.notification_text_liked_post), notification.getName()));
                    break;
                case Notification.LIKED_MY_COMMENT:
                    textViewNotification.setText(String.format(context.getString(R.string.notification_text_liked_comment), notification.getName()));
                    break;
                case Notification.COMMENT_MY_POST:
                    textViewNotification.setText(String.format(context.getString(R.string.notification_text_comment_post), notification.getName()));
                    break;
            }
            date.setText(notification.getDate());
            final String url = Util.generateURL(MainActivity.s3, notification.getImgProfile());
            new Handler().post(new Runnable() {
                public void run() {
                    try{
                        final int large = Util.getWidthAndHeightForImgUser(activity);
                        Picasso.with(context).load(url).resize(large, large).centerCrop().placeholder(R.drawable.profile).error(R.drawable.profile).into(userImg, new Callback() {
                            @Override
                            public void onSuccess() {

                            }
                            @Override
                            public void onError() {

                            }
                        });
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (notification.getNotificationType()){
                        case Notification.NEW_FOLLOWER:
                            Intent intent = new Intent(activity, UserProfileActivity.class);
                            intent.putExtra(Constants.User.USER_ID.value, notification.getNotificationSender());
                            intent.putExtra(Constants.User.NAME.value, notification.getName());
                            intent.putExtra(Constants.User.IMG_PROFILE.value, notification.getImgProfile());
                            intent.putExtra(Constants.User.IMG_COVER.value, notification.getImgCover());
                            activity.startActivity(intent);
                            break;
                        case Notification.LIKED_MY_POST:
                            Intent intent2 = new Intent(context, ViewPostActivity.class);
                            intent2.putExtra(Constants.Post.POST_ID.value, notification.getStaffId());
                            activity.startActivity(intent2);
                            break;
                        case Notification.LIKED_MY_COMMENT:
                            Intent intent3 = new Intent(context, ViewPostActivity.class);
                            intent3.putExtra(Constants.Post.POST_ID.value, notification.getStaffId());
                            activity.startActivity(intent3);
                            break;
                        case Notification.COMMENT_MY_POST:
                            Intent intent4 = new Intent(context, ViewPostActivity.class);
                            intent4.putExtra(Constants.Post.POST_ID.value, notification.getStaffId());
                            activity.startActivity(intent4);
                            break;
                    }
                }
            });
        }
    }
}
