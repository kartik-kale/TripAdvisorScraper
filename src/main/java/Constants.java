/**
 * Created by kartik.k on 9/24/2014.
 */
public class Constants {
    public static final String IMAGE_NOT_FOUND_URL = "http://www.fodors.com/ee/files/blog_images/10420/travel-quote-hero__main.jpg";

    public static final String SQL_DB_HOST = "localhost";

    public static final String[] LIST_OF_CITIES = {"Bangkok", "Seoul", "London", "Milan", "Paris", "Rome",
            "Singapore", "Shanghai", "New York", "Amsterdam", "Istanbul", "Tokyo",
            "Dubai", "Vienna", "Kuala Lumpur", "Taipei", "Hong Kong", "Riyadh",
            "Barcelona", "Los Angeles"};

    public static final int getNoOfAttractions(String cityName){
        if(cityName.equals("Riyadh")){
            return 17;
        }
        else {
            return 30;
        }
    }
}
