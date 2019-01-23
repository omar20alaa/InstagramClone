package com.instagram.instagramclone.Model;

public class Story {

    private String  imageurl;
    private long timestart;
    private long timeend;
    private String storyid;
    private String userid;

    public Story() {
    }

    public Story(String imageurl, long timestart, long timeend, String storyid, String userid) {
        this.imageurl = imageurl;
        this.timestart = timestart;
        this.timeend = timeend;
        this.storyid = storyid;
        this.userid = userid;
    }

    public String getImageurl() {
        return imageurl;
    }

    public long getTimestart() {
        return timestart;
    }

    public long getTimeend() {
        return timeend;
    }

    public String getStoryid() {
        return storyid;
    }

    public String getUserid() {
        return userid;
    }
}
