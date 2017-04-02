package com.twismart.barcafansclub.Util;

/**
 * Created by sneyd on 12/5/2016.
 **/

public class Constants {
    private static final String URL_SERVER = "http://34.199.104.254/";
    public static final String AMAZON_AD_KEY = "8b7fb221cb174c988c0b36baf18a1b3c";
    public static final String MY_USER_ID = "myUserId";
    public static final String JSON_USER_DATA = "jsonUserData", POST_TO_VIEW_IN_DETAIL = "postToView", POST_TO_EDIT = "postToEdit";
    public static final String[] languages = {"en", "es", "ca"};


    public enum Urls {
        GET_INFO_POST(URL_SERVER + "getBasicInfo.php"), ADD_NEW_COMMENT(URL_SERVER + "addNewComment.php"), GET_COMMENTS(URL_SERVER + "getComments.php"),
        GET_POSTS(URL_SERVER + "getPosts.php"), PUT_THUMB_POST(URL_SERVER + "putThumbPost.php"), REMOVE_THUMB_POST(URL_SERVER + "removeThumbPost.php"),
        GET_POSTS_BY_USER_ID(URL_SERVER + "getPostsByUserId.php"), DELETE_POST(URL_SERVER + "deletePost.php"), UPDATE_POST(URL_SERVER + "updatePost.php"),
        SIGN_UP(URL_SERVER + "signUp.php"), GET_INFO_COMMENT(URL_SERVER + "getInfoComment.php"), PUT_THUMB_COMMENT(URL_SERVER + "putThumbComment.php"),
        REMOVE_THUMB_COMMENT(URL_SERVER + "removeThumbComment.php"), NEW_POST(URL_SERVER + "newPost.php"), LOGIN(URL_SERVER + "login.php"), UNMUTE(URL_SERVER + "unmute.php"),
        GET_NEWS_FCB(URL_SERVER + "getNews.php"), GET_MATCHES(URL_SERVER + "getMatches.php"), GET_PREVIEW_MATCH(URL_SERVER + "getPreviewMatch.php"), BLOCK(URL_SERVER + "block.php"),
        GET_FEATURES_OF_MATCH(URL_SERVER + "getFeaturesOfMatch.php"), GET_AM_I_FOLLOWING_THAT_USER(URL_SERVER + "amIFollowingThatUser.php"), UNBLOCK(URL_SERVER + "unblock.php"),
        FOLLOW(URL_SERVER + "follow.php"), UNFOLLOW(URL_SERVER + "unfollow.php"), MUTE(URL_SERVER + "mute.php"), GET_AM_I_MUTING_THAT_USER(URL_SERVER + "amIMutingThatUser.php"),
        GET_TABLE(URL_SERVER + "getTable.php"), UDATE_MY_PROFILE(URL_SERVER + "updateMyProfile.php"), CHECK_MY_SESION(URL_SERVER + "checkMySesion.php"),
        SET_MY_TOKEN_ID(URL_SERVER + "setMyTokenId.php"),
        REPORT_PROBLEM(URL_SERVER + "reportProblem.php"), PRIVACY_POLICY(URL_SERVER + "privacypolicy.html"), TERMS_OF_USE(URL_SERVER + "termsofuse.html"),
        GET_AM_I_BLOCKING_THAT_USER(URL_SERVER + "amIBlockingThatUser.php"), GET_NOTIFICATIONS(URL_SERVER + "getNotifications.php"), GET_POST_DATA_FROM_ID(URL_SERVER + "getPostDataFromId.php"),
        FEED_EN_SPORTS("http://www.sport-english.com/en/rss/barca/rss.xml"), FEED_ES_SPORTS("http://www.sport.es/es/rss/barca/rss.xml"),
        FEED_ES_FCBARCELONANOTICIAS("http://www.fcbarcelonanoticias.com/rss/todas-las-noticias-de-fcbn-001.xml"), FEED_ES_MUNDODEPORTIVO("http://www.mundodeportivo.com/feed/rss/futbol/fc-barcelona"),
        FEED_ES_AS("http://masdeporte.as.com/tag/rss/fc_barcelona/a");

        public String link;

        Urls(String link){
            this.link = link;
        }
    }

    public enum User {
        USER_ID("userId"), NAME("name"), EMAIL("email"), PASSWORD("password"), IMG_PROFILE("imgProfile"), IMG_COVER("imgCover"), GENDER("gender"), BIRTHDAY("birthday"), LANGUAGE("language"),
        COUNTRY("country"), TOKEN_ID("tokenId");

        public String value;

        User(String value){
            this.value = value;
        }
    }

    public enum Post {
        POST_ID("postId"), DATE("date"), DATE_TEXT("dateText"), TEXT("text"), IMG("img"), MY_VOTE("myVote"), LIKES("likes"), DISLIKES("dislikes"), NAME_USER("nameUser"), IMG_USER("imgUser"),
        IMG_USER_COVER("imgUserCover"), CREATOR_ID("creatorId"), FROM_DATE("fromDate");

        public String value;

        Post(String value){
            this.value = value;
        }
    }

    public enum Comment {
        COMMENT_ID("commentId"), DATE("date"), TEXT("text"), IMG("img"), MY_VOTE("myVote"), LIKES("likes"), DISLIKES("dislikes"), NAME_USER("nameUser"), IMG_USER("imgUser"), CREATOR_ID("creatorId");

        public String value;

        Comment(String value){
            this.value = value;
        }
    }

    public enum Thumb {
        LIKE("like"), NEITHER("neither"), DISLIKE("dislike"), TYPE_THUMB("typeThumb"), LIKE_ID("likeId");

        public String value;

        Thumb(String value){
            this.value = value;
        }
    }
/*
    public enum News {
        TITLE("title"), LINK("link"), DESCRIPTION("description"), IMG_URL("imgUrl");

        public String value;

        News(String value){
            this.value = value;
        }
    }
*/
    public enum Match {
        DATE("date"), TIME("time"), HOME_TEAM("homeTeam"), AWAY_TEAM("awayTeam"), VS("vs"), AWAY_ICON("awayIcon"), HOME_ICON("homeIcon"), LINK("link");

        public String value;

        Match(String value){
            this.value = value;
        }
    }

    public enum MatchPreview {
        DATE("date"), TIME("time"), HOME_TEAM("homeTeam"), AWAY_TEAM("awayTeam"), AWAY_ICON("awayIcon"), HOME_ICON("homeIcon"), LINK("link"), COMPETITION("competition"), LOCATION("location"),
        REFEREE("referee"), ATTENDANCE("attendance"), HOME_SCORE("homeScore"), AWAY_SCORE("awayScore"), SEPARATOR("separator"), AGGREGATE("aggregate");

        public String value;

        MatchPreview(String value){
            this.value = value;
        }
    }

    public enum Rank {
        ICON("icon"), TEAM("team"), MATCHES("matches"), WON("won"), DRAWN("drawn"), LOST("lost"), GD("gd"), PTS("pts");

        public String value;

        Rank(String value){
            this.value = value;
        }
    }

    public enum Problem {
        DESCRIPTION("description"), TYPE_PROBLEM("typeProblem");

        public String value;

        Problem(String value){
            this.value = value;
        }
    }

    public enum Notification {
        NOTIFICATION_RECEIVER("notificationReceiver"), NOTIFICATE_SENDER("notificationSender"), STAFF_ID("staffId"), NOTIFICATION_TYPE("notificationType"), DATE("date"), NAME("name"), IMG_PROFILE("imgProfile"), IMG_COVER("imgCover");

        public String value;

        Notification(String value){
            this.value = value;
        }
    }

    public enum Follow {
        FOLLOWER_ID("followerId"), FOLLOWED_ID("followedId");

        public String value;

        Follow(String value){
            this.value = value;
        }
    }

    public enum Mute {
        MUTER_ID("muterId"), USER_ID("userId");

        public String value;

        Mute(String value){
            this.value = value;
        }
    }

    public enum Block {
        BLOCKER_ID("blockerId"), USER_ID("userId");

        public String value;

        Block(String value){
            this.value = value;
        }
    }
}