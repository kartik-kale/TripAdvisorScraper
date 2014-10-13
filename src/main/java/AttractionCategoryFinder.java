import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Created by kartik.k on 10/1/2014.
 */
public class AttractionCategoryFinder {
    private static Logger LOGGER;

    public static void setupLogger() {
        LOGGER = Logger.getLogger(Main.class.getName());
        LOGGER.setLevel(Level.FINEST);
        FileHandler logFileHandler;

        try {

            // This block configure the logger with handler and formatter
            logFileHandler = new FileHandler("C:/Users/kartik.k/Desktop/tripPlannerDocumentation/logs/MyLogFile.log");
            LOGGER.addHandler(logFileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            logFileHandler.setFormatter(formatter);

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        setupLogger();
        String[] list_of_cities = Constants.LIST_OF_CITIES;
        for (int i = 0; i < list_of_cities.length; i++) {
            String cityName = list_of_cities[i];
            if (cityName.equals("Riyadh")||i<5)
                continue;
            findCategoriesForOneCity(cityName);
        }
    }

    public static boolean findCategoriesForOneCity(String cityName) {
        setupLogger();
        System.out.println("-----------------------------------------------");
        System.out.println("doing it for "+cityName);
        boolean successfullyFetchedAttractionsPage = false;
        Map<String, Integer> categoryNameIdMapping = new HashMap<String, Integer>();
        String linkOfCityAttractions = null;
        while (!successfullyFetchedAttractionsPage) {
            try {
                linkOfCityAttractions = ScraperHelper.getLinkOfAttractionsPage(cityName);
                System.out.println(linkOfCityAttractions);
                Document attractionsPage = Jsoup.connect(linkOfCityAttractions).get();
                Elements categoryButtons = attractionsPage.getElementsByClass("styleIcon");
                for (Element categoryButton : categoryButtons) {
                    Set<String> classesOnCategoryButton = categoryButton.classNames();
                    int categoryId = -1;
                    String categoryName = null;
                    for (String className : classesOnCategoryButton) {
                        if (className.contains("sprite-filter-attraction-")) {
                            categoryId = Integer.parseInt(className.replaceAll("sprite-filter-attraction-", ""));
                            categoryName = categoryButton.parent().ownText();
                            categoryNameIdMapping.put(categoryName, categoryId);
                            System.out.println(categoryName + " " + Integer.toString(categoryId));

                        }
                    }
                }
                successfullyFetchedAttractionsPage = true;
            } catch (IOException e) {
                System.out.println("retrying..");
                successfullyFetchedAttractionsPage = false;
//            e.printStackTrace();
            }
        }
        if(categoryNameIdMapping.size()!=9){
            LOGGER.warning("just how many categories?");
        }
        if(linkOfCityAttractions==null){
            LOGGER.warning("attractions URL is incorrect..");
            return false;
        }
        for(String category:categoryNameIdMapping.keySet()) {
            if(category.equals("All")){
                System.out.println("skipping all attractions page");
                continue;
            }
            String linkOfCategorizedAttractionList = linkOfCityAttractions.replace("Vacations", "Vacations-c" + Integer.toString(categoryNameIdMapping.get(category)));
            getListOfAttractionsForOneCategory(linkOfCategorizedAttractionList, category);
        }
        return false;
    }

    private static void getListOfAttractionsForOneCategory(String linkOfCityAttractions, String category){
        Document attractionsPage = null;
        boolean attractionPageOpened =false;
        while (!attractionPageOpened) {
            try {
                attractionsPage = Jsoup.connect(linkOfCityAttractions).get();
                attractionPageOpened = true;
            } catch (IOException e) {
                attractionPageOpened = false;
                System.out.println("could not open the attractions-category page for attraction " + attractionsPage + " for category " + category);
            }
        }
        Elements attractionsElementList = attractionsPage.getElementsByClass("listing");
        for (Element attractionElement: attractionsElementList){
            Elements attractionLinkNameContainer = attractionElement.getElementsByClass("quality");
            if(attractionElement.toString().contains("appu")){
                int a = 5;
            }
            if(attractionLinkNameContainer==null){
                System.out.println(attractionElement.toString());
                continue;
            }
            Element firstAtag = attractionLinkNameContainer.first();
            if(firstAtag==null){
                System.out.println(attractionLinkNameContainer);
                continue;
            }
            Elements atagElementsContainingAttractionName = firstAtag.getElementsByTag("a");
            if(atagElementsContainingAttractionName==null){
                System.out.println(attractionLinkNameContainer.toString());
                continue;
            }
            Element attractionNameContainer = atagElementsContainingAttractionName.first();

            String attractionName = attractionNameContainer.html();
//            LOGGER.fine("attraction name is "+attractionName+ " with category "+category);
            if (!SqlQueryExecutor.addCategoryAttractionMappingAndReturnWhetherAttractionRelevant(category, attractionName)) {
//            System.out.println("done for category "+category);
//            break;
            }

        }
    }

}
