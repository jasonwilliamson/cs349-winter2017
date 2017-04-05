public class Main {

    /*
     * Example main method
     * You can use this main as a playground to test the model with your API key
     * Again, the API key can be obtained by following this guide:
     * https://developers.google.com/youtube/registering_an_application#create_project
     */ 
    public static void main(String[] args) {
        
        ModelYouTube youtubeModel = new ModelYouTube("AIzaSyB1QDnVeZI94Qr-NY5o2hJy9gDMtE3299Q");
        //youtubeModel.searchVideos("puppies");
        View view = new View(youtubeModel);

    }

}