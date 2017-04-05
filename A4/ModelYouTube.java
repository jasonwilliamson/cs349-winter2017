import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;

import java.io.IOException;
import java.util.List;
import java.util.Iterator;
import java.util.*;
import java.io.Serializable;

public class ModelYouTube implements Serializable {

    private static long MAX_NUM_RESULTS = 25;

    private String apiKey;
    private YouTube youtube;
    private List<Observer> observers;
    public List<VideoDataType> vidList;
    
    // good idea to dis-allow default constructor usage because an API key is strictly necessary
    private ModelYouTube() {}

    /*
     * Primary constructor to be used, this requires the creation of a YouTube API key.
     * see: https://developers.google.com/youtube/registering_an_application#create_project
     * returns null or raises an exception if something went wrong.
     */
    public ModelYouTube(String apiKey) {
        
        this.apiKey = apiKey;

        try {
            YouTube.Builder builder = new YouTube.Builder(
                new NetHttpTransport(), new JacksonFactory(),new HttpRequestInitializer() {
                    @Override 
                    public void initialize(HttpRequest httpRequest) throws IOException {}
            });
            builder.setApplicationName("a4-youtube");
            youtube = builder.build();
            
        } catch (Exception e) {
            e.printStackTrace();
            this.throwModelInitializationException();
        }

        this.observers = new ArrayList();
        this.vidList = new ArrayList<VideoDataType>();

    }

    /*
     * Use this method as a template to search and extract video information for your data model.
     * Searches for videos and prints some metadata.
     * Refer to the API documentation to see how the API can be used to extract information.
     * https://developers.google.com/youtube/v3/code_samples/java#search_by_keyword
     *
     */
    public void searchVideos(String query) {

        if (youtube == null) {
            this.throwModelInitializationException();
        }

        try {
            YouTube.Search.List search = youtube.search().list("id,snippet");
            
            search.setKey(this.apiKey);
            search.setQ(query);
            search.setType("video");
            search.setSafeSearch("strict");
            search.setMaxResults(this.MAX_NUM_RESULTS);

            SearchListResponse searchResponse = search.execute();
            List<SearchResult> resultsList = searchResponse.getItems();

            vidList.clear();
            if (resultsList != null) {
                Iterator<SearchResult> resultsIterator = resultsList.iterator();

                int indexCounter = 0;
                while (resultsIterator.hasNext()) {
                    System.out.println("--------------------------------------------------");
                    
                    SearchResult searchResult = resultsIterator.next();
                    
                    ResourceId rId              = searchResult.getId();
                    SearchResultSnippet snippet = searchResult.getSnippet();
                    
                    System.out.println("Title: "+snippet.getTitle());
                    System.out.println("video ID: "+rId.getVideoId());
                    System.out.println("Raw data:"+searchResult.toString());

                    System.out.println("--------------------------------------------------");

                    //Fill the VideoDataType of each video returned from search...
                    VideoDataType vid = new VideoDataType(snippet, rId.getVideoId());
                    vid.query = query; //each video stores the search term, cheat..
                    vid.index = indexCounter; //keep track of index 
                    vidList.add(vid);
                    ++indexCounter;
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            this.throwModelInitializationException();
        }

        notifyObservers();  //

    }

    // helper calls for error reporting and debugging
    private void throwModelInitializationException() {

        throw new RuntimeException("Couldn't initialize the YouTube object. You may want to check your API Key.");

    }

     /**
     * Add an observer to be notified when this model changes.
     */
    public void addObserver(Observer observer) {
        this.observers.add(observer);
    }

    /**
     * Remove an observer from this model.
     */
    public void removeObserver(Observer observer) {
        this.observers.remove(observer);
    }

    /**
     * Notify all observers that the model has changed.
     */
    public void notifyObservers() {
        for (Observer observer: this.observers) {
            observer.update(this);
        }
    }

    public void updateVideoData(int index, int rating){
        System.out.println("MODEL updateVideoData: ");
        VideoDataType vdt = (VideoDataType) this.vidList.get(index);
        vdt.setStarRating(rating);
        notifyObservers();
    }

}