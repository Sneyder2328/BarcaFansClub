package com.twismart.barcafansclub.Pojos;

/**
 * Created by sneyd on 2/1/2017.
 **/
public class Match {

    private String date, time, homeTeam, awayTeam, vs, homeIcon, awayIcon, link;

    public Match(String date, String time, String homeTeam, String awayTeam, String vs, String homeIcon, String awayIcon, String link) {
        this.date = date;
        this.time = time;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.vs = vs;
        this.homeIcon = homeIcon;
        this.awayIcon = awayIcon;
        this.link = link;
    }


    public String getHomeIcon() {
        return homeIcon;
    }

    public String getAwayIcon() {
        return awayIcon;
    }
    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public String getVs() {
        return vs;
    }

    public String getLink() {
        return link;
    }
}
