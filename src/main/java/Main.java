import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kartik.k on 9/24/2014.
 */
public class Main {
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
        String newCities[] = {"Mumbai","Delhi","Rio de Janeiro","Sri Lanka"};
        setupLogger();
//        SqlQueryExecutor.cleanAllTables();
//        String[] listOfCities = Constants.LIST_OF_CITIES;
//        for (int i = 0; i < listOfCities.length; i++) {
//            String cityName = listOfCities[i];
//            if(cityName.equals("Riyadh")){
//                continue;
//            }
//            LOGGER.info("trying city " + cityName);
//            while (!getPrimeAttractionsForCity(cityName)) ;
//        }
        for (int i = 0; i < newCities.length; i++) {
            getDetailsOfOneCity(newCities[i]);
        }
    }

    public static void getDetailsOfOneCity(String newCity) {
        String cityName = newCity;
//            if(cityName.equals("Riyadh")){
//                continue;
//            }
        LOGGER.info("trying city " + cityName);
        while (!getPrimeAttractionsForCity(cityName)) ;
    }

    private static boolean getPrimeAttractionsForCity(String cityName) {
        try {

            String linkOfCityHomePage = ScraperHelper.getCityHomePageLink(cityName);
            String linkOfCityAttractions = linkOfCityHomePage.replace("Tourism","Attractions");
            String linkOfCityHotels= linkOfCityHomePage.replace("Tourism","Hotels");
            Document cityHomePage = Jsoup.connect(linkOfCityHomePage).get();
            Elements cityImageContainer = cityHomePage.getElementsByClass("heroPhotoImg");
            String linkOfCityImage = null;
            if(cityImageContainer!= null){
                try{
                    linkOfCityImage = cityImageContainer.first().attr("src");
                }
                catch (NullPointerException e){
                    LOGGER.warning("city image not found for " + cityName);
                }
            }

            List<Attraction> listOfAttractions = new ArrayList<Attraction>();
            String linkOfCityAttractionsSecondPage = linkOfCityAttractions.replace(".html", "-oa30.html");
            listOfAttractions.addAll(getListOfAttractions(linkOfCityAttractions, cityName));
            listOfAttractions.addAll(getListOfAttractions(linkOfCityAttractionsSecondPage, cityName));

            Collections.sort(listOfAttractions,new Comparator<Attraction>() {
                        @Override
                        public int compare(Attraction o1, Attraction o2) {
                            return o1.getNoOfReviews()-o2.getNoOfReviews();
                        }
                    }
            );
            listOfAttractions = listOfAttractions.subList(listOfAttractions.size()-30,listOfAttractions.size());
            for(Attraction attraction:listOfAttractions){
                SqlQueryExecutor.addAttractionToDB(attraction);
            }
            LOGGER.fine("image link for "+cityName + " is "+linkOfCityImage);
            return true;
        } catch (IOException e) {
            LOGGER.warning("IO exception in getting the top attractions page");
            return false;
        }
        catch (NullPointerException nullException){
            LOGGER.warning("null pointer exception on the top attractions page");
            nullException.printStackTrace();
            return false;
        }
    }


    private static List<Attraction> getListOfAttractions(String linkOfCityAttractions, String cityName) throws IOException {
        Document attractionsPage = Jsoup.connect(linkOfCityAttractions).get();
        Elements attractionsElementList = attractionsPage.getElementsByClass("listing");
        List<Attraction> listOfAttractions = new ArrayList<Attraction>();
        for (Element attractionElement: attractionsElementList){
            Attraction attraction = getAttraction(attractionElement,cityName );
            listOfAttractions.add(attraction);
        }
        return listOfAttractions;
    }

    private static Attraction getAttraction(Element attractionElement, String cityName) {
        String reviewLink;
        String attractionName;
        float noOfStars;
        int noOfReviews;
        String imageLink;
        String description = null;
        float recommendedLengthOfVisit = 0;
        String type = null;
        Boolean fee = null;
        String activities = null;
        String information = null;
        double latitude = 0;
        double longitude = 0;
        try {
            Elements attractionLinkNameContainer = attractionElement.getElementsByClass("quality");
            Element attractionNameContainer = attractionLinkNameContainer.first().getElementsByTag("a").first();

            attractionName = attractionNameContainer.html();
            LOGGER.fine("attraction name is "+attractionName);
            reviewLink = Constants.tripAdvisorUrlPrefix + attractionNameContainer.attr("href");
            LOGGER.fine("review link for attraction "+attractionName + " "+ reviewLink);
            Element ratingElement = attractionElement.getElementsByClass("sprite-ratings").first();
            noOfStars = Float.parseFloat(ratingElement.attr("content"));
            LOGGER.fine("no of stars for attraction "+attractionName + " "+ Float.toString(noOfStars));
            Element noOfReviewsLinkElement  = attractionElement.getElementsByClass("more").first().
                    getElementsByTag("a").first();
            String noOfReviewsString = noOfReviewsLinkElement.html();
            noOfReviews = Integer.parseInt(noOfReviewsString.replaceAll(" reviews","").replaceAll(",",""));
            LOGGER.fine("no of reviews for attraction "+attractionName + " "+Integer.toString(noOfReviews));
        }
        catch (NullPointerException nullException){
            LOGGER.warning("got null pointer exception for attraction element " + attractionElement);
            return null;
        }
        LOGGER.info("processing the attraction "+attractionName);

        try {
            Element imageContainerElement = attractionElement.getElementsByClass("photo_image").first();
             imageLink = imageContainerElement.attr("src");
        }
        catch (NullPointerException e){
            imageLink = Constants.IMAGE_NOT_FOUND_URL;
        }

        Elements detailKeyElements = null;
        for(int attemptNo = 0;attemptNo<15;attemptNo++) {
            boolean isAttemptSuccessful = true;
            try {
                Document reviewPage = Jsoup.connect(reviewLink).get();
                detailKeyElements = reviewPage.getElementsByClass("listing_details").first().getElementsByTag("b");


                String[] splitOnLng = reviewPage.html().toString().split("lng:");
                if(splitOnLng.length!=2){
                    LOGGER.warning("latlong not found in right condition..");
                }else {
                    String lngString = splitOnLng[1].split(",")[0];
                    String latString = splitOnLng[0].split("lat:")[1];
                    latitude = Double.parseDouble(latString.replaceAll("[,:\\s]",""));
                    longitude = Double.parseDouble(lngString.replaceAll("[,:\\s]",""));
//                    LOGGER.severe(Double.toString(latitude) + " " + Double.toString(longitude));
                }
            } catch (IOException e) {
                isAttemptSuccessful = false;
                LOGGER.warning("review page could not be opened for " + attractionName + " in attempt no "+
                Integer.toString(attemptNo));
                //                e.printStackTrace();
            }
            if(isAttemptSuccessful){
                break;
            }
        }
        if(detailKeyElements != null){
            for(Element detailKeyElement:detailKeyElements) {
                try {
                    Boolean isDetailDiscarded = true;
                    String detailValue = detailKeyElement.parent().ownText();
                    String detailKey = detailKeyElement.html().replace(":", "");
                    if (detailKey.toLowerCase().contains("description")) {
                        isDetailDiscarded = false;
                        description = detailValue;
                    }
                    if (detailKey.toLowerCase().contains("recommended length of visit")) {
                        String recommendedLengthOfVisitAsString = null;
                        isDetailDiscarded = false;
                        recommendedLengthOfVisitAsString = detailValue;
                        Pattern p = Pattern.compile("\\d+");
                        Matcher m = p.matcher(recommendedLengthOfVisitAsString);
                        int noOfIntsFound = 0;
                        float sumOfInts = 0;
                        while (m.find()) {
                            sumOfInts+= Float.parseFloat(m.group());
                            noOfIntsFound ++;
                        }
                        if(noOfIntsFound == 1){
                            if(recommendedLengthOfVisitAsString.toLowerCase().contains("less") || recommendedLengthOfVisitAsString.contains("<")){
                                sumOfInts*=0.75;
                            }
                            else if(recommendedLengthOfVisitAsString.toLowerCase().contains("more")||recommendedLengthOfVisitAsString.contains(">")){
                                sumOfInts*=1.5;
                            }
                            else {
                                LOGGER.info("the recommended length did neither contain < or >; yet it contained only one no. assuming" +
                                        "it gave the length precisely.");
                            }
                            recommendedLengthOfVisit = sumOfInts;
                        }
                        if(noOfIntsFound == 2){
                            recommendedLengthOfVisit = sumOfInts/noOfIntsFound;
                        }
                        if(noOfIntsFound<1 || noOfIntsFound>2){
                            LOGGER.warning("unexpected no of ints found for attraction "+attractionName);
                        }
                    }
                    if (detailKey.toLowerCase().contains("type")) {
                        isDetailDiscarded = false;
                        type = detailValue;
                    }
                    if (detailKey.toLowerCase().contains("activities")) {
                        isDetailDiscarded = false;
                        activities = detailValue;
                    }
                    if (detailKey.toLowerCase().contains("fee")) {
                        isDetailDiscarded = false;
                        if(detailValue.toLowerCase().equals("yes")){
                            fee = true;
                        }
                        else if(detailValue.toLowerCase().equals("no")){
                            fee = false;
                        }
                        else {
                            LOGGER.warning("while logging fee for "+attractionName +
                                    ", unknown value found: "+detailValue.toLowerCase() +".");
                        }
                    }
                    if (detailKey.toLowerCase().contains("information")) {
                        isDetailDiscarded = false;
                        information = detailValue;
                    }
                    if(isDetailDiscarded){
                        LOGGER.info("Discarded detail "+detailKey + " for "+ attractionName);
                    }
                }
                catch (NullPointerException nullException){
                    LOGGER.warning("got null pointer exception in this detail for "+attractionName);
//                    nullException.printStackTrace();
                }

            }


        }
        Attraction attraction = new Attraction(attractionName,reviewLink,imageLink,noOfReviews,noOfStars,type,null,description,fee,
                recommendedLengthOfVisit,latitude,longitude, cityName, activities, information);
//        SqlQueryExecutor.addAttractionToDB(attraction);
        return  attraction;
    }


}
