package com.twismart.barcafansclub.Util;


import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import com.twismart.barcafansclub.Fragments.NewsFragment;
import com.twismart.barcafansclub.Pojos.NewFCB;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sneyd on 2/5/2017.
 **/

public class HandleXML {
    private static final String TAG = "HandleXML";
    private List<NewFCB> listNews = new ArrayList<>();

    private String title = "";
    private String link = "";
    private String description = "";
    private XmlPullParserFactory xmlFactoryObject;

    public HandleXML(final Activity activity, final String urlString, final NewsFragment.INewsFromRSSReadyListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setReadTimeout(10000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);

                    // Starts the query
                    conn.connect();
                    InputStream stream = conn.getInputStream();

                    xmlFactoryObject = XmlPullParserFactory.newInstance();
                    XmlPullParser myparser = xmlFactoryObject.newPullParser();

                    myparser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                    myparser.setInput(stream, null);

                    parseXMLAndStoreIt(activity, myparser, urlString, listener);
                    stream.close();
                } catch (Exception e) {
                    listener.onError();
                    Log.e(TAG, "Exception HandleXML " + e.getMessage());
                }
            }
        }).start();
    }


    private void parseXMLAndStoreIt(Activity activity, XmlPullParser myParser, String url, final NewsFragment.INewsFromRSSReadyListener listener) {
        int event;
        String text=null;

        try {
            if (url.equals(Constants.Urls.FEED_EN_SPORTS.link) || url.equals(Constants.Urls.FEED_ES_SPORTS.link)) {
                event = myParser.getEventType();
                while (event != XmlPullParser.END_DOCUMENT) {
                    String name = myParser.getName();
                    switch (event) {
                        case XmlPullParser.TEXT:
                            text = myParser.getText();
                            break;
                        case XmlPullParser.END_TAG:
                            if (name.equals("title")) {
                                title = text;
                            }
                            else if (name.equals("link")) {
                                link = text;
                            }
                            else if (name.equals("description")) {
                                description = text;
                                listNews.add(new NewFCB(title, link, description, "null"));
                            }
                            break;
                    }
                    event = myParser.next();
                }
            }
            else{
                event = myParser.getEventType();
                while (event != XmlPullParser.END_DOCUMENT) {
                    String name = myParser.getName();

                    switch (event) {
                        case XmlPullParser.START_TAG:
                            if (name.equals("enclosure")) {
                                String urlImg = myParser.getAttributeValue(myParser.getNamespace(), "url");
                                listNews.add(new NewFCB(title, link, description, urlImg));
                            }
                            break;
                        case XmlPullParser.TEXT:
                            text = myParser.getText();
                            break;
                        case XmlPullParser.END_TAG:
                            if (name.equals("title")) {
                                title = text;
                            }
                            else if (name.equals("description")) {
                                description = text;
                            }
                            else if (name.equals("link")) {
                                link = text;
                            }
                            break;
                    }
                    event = myParser.next();
                }
            }
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listener.onNewsReady(listNews);
                }
            });
        }
        catch (Exception e) {
            Log.e(TAG, "Exception parseXMLAndStoreIt " + e.getMessage());
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listener.onError();
                }
            });
        }
    }

    public String getTitle(){
        return title;
    }

    public String getLink(){
        return link;
    }

    public String getDescription(){
        return description;
    }
}