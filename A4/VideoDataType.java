// ====================================================
// Jason Williamson (20552360)
// CS 349 Winter 2017
// Assignment 04
// File: VideoDataType.java
// ====================================================
//
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import java.io.Serializable;

import java.io.IOException;
import java.util.List;
import java.util.Iterator;
import java.util.*;
import java.text.SimpleDateFormat;

public class VideoDataType implements Serializable{
    public Date datePublished;
    public String thumbnail;
    public String title;
    private final int ONE = 1;
    private final int TWO = 2;
    private final int THREE = 3;
    private final int FOUR = 4;
    private final int FIVE = 5;
    private final int ZERO = 0;
    private long milliSeconds;
    public int index;
    public String query;
    public String videoId;
    public int starRating = 0;

    public VideoDataType(){}

    public VideoDataType(SearchResultSnippet snippet,String videoId){
        title = snippet.getTitle();
        thumbnail = snippet.getThumbnails().getDefault().getUrl();
        this.videoId = videoId;
        milliSeconds = snippet.getPublishedAt().getValue();
        
        System.out.println( "VIDEO_DATA_TYPE" );
        System.out.println( title );
        System.out.println( thumbnail );
        System.out.println(snippet.getPublishedAt().getValue()); 
        System.out.println( "XXXXXXXXXXXXXXX" );
    }

    public long getPublishedSeconds(){
        return milliSeconds;
    }

    public void setStarRating(int rating){
        this.starRating = rating;
    }
}