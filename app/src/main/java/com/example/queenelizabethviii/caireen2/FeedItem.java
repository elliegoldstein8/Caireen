package com.example.queenelizabethviii.caireen2;

public class FeedItem {
    private String title;
    private String url;
    private String description;

    FeedItem(String title, String url, String description){
        this.title = title;
        this.url = url;
        this.description = description;
    }

    public String getTitle(){
        return title;
    }

    public String getLink(){
        return url;
    }

    public String getDescription(){
        return description;
    }

    @Override
    public String toString() {
        return title + "\n \n" + url;
    }
}
