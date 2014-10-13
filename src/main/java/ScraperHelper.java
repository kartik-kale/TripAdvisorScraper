import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

/**
 * Created by kartik.k on 10/1/2014.
 */
public class ScraperHelper {
    static String extractCityNameToBeQueried(String cityName) {
        String formattedCityName = cityName.trim();
                formattedCityName= formattedCityName.toLowerCase();
        formattedCityName = formattedCityName.replace(" ","+");
        return formattedCityName;
    }

    public static String getCityHomePageLink(String cityName){
        Document document = null;
        boolean fetchedSuccessfully = false;
        while (!fetchedSuccessfully) {
            try {
                document = Jsoup.connect("http://www.tripadvisor.in/Search?q=" + extractCityNameToBeQueried(cityName)).get();
                fetchedSuccessfully = true;
            } catch (IOException e) {
                fetchedSuccessfully = false;
                System.out.println("the search page not found. retrying..");
            }
        }
        Element firstResult = document.getElementsByClass("item1").first();
        if(firstResult==null){
            return null;
        }
        Element headOfFirstResult = firstResult.getElementsByClass("srHead").first();
        Element aTagOfFirstSearchResult = headOfFirstResult.getElementsByTag("a").first();

        return Constants.tripAdvisorUrlPrefix + aTagOfFirstSearchResult.attr("href");
    }

    public static String getLinkOfAttractionsPage(String cityName) {
        String linkOfCityHomePage = getCityHomePageLink(cityName);
        if(linkOfCityHomePage == null)
            return null;
        return linkOfCityHomePage.replace("Tourism","Attractions");
    }
}
