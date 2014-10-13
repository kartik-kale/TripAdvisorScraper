import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by kartik.k on 9/30/2014.
 */
public class DistanceMatrixCalculator {

    public static void main(String[] args) {
//        for(int cityIndex = 0;cityIndex<Constants.LIST_OF_CITIES.length;cityIndex++){
//
//
//            String city = Constants.LIST_OF_CITIES[cityIndex];
//            if(cityIndex<16){
//                continue;
//            }
//            System.out.println("getting distances for "+ city);
//            findAndStoreDistanceMAtrixForOneCity(city);
//        }
findAndStoreDistanceMAtrixForOneCity("Mumbai");
    }

    public static void findAndStoreDistanceMAtrixForOneCity(String cityName) {
        Map<Integer,Coordinate> coordinateMap = SqlQueryExecutor.getCoordinatesOfAllAttractions(cityName);
        List<Integer> listOfAttractionID=new ArrayList<Integer>(coordinateMap.keySet());

        int noOfAttractions = listOfAttractionID.size();
        for(int srcAttractionIndex = 0;srcAttractionIndex<noOfAttractions-1;srcAttractionIndex++){
            if(SqlQueryExecutor.isThisAttractionPresentInDistanceMatrix(
                    listOfAttractionID.get(srcAttractionIndex),coordinateMap.size())){
                continue;
            }
            Coordinate[] destAttractionCoordinateArray = new Coordinate[noOfAttractions -srcAttractionIndex-1];

            for(int destAttractionIndex = srcAttractionIndex+1;destAttractionIndex< noOfAttractions;destAttractionIndex++){

                destAttractionCoordinateArray[destAttractionIndex-srcAttractionIndex-1] =
                        coordinateMap.get(listOfAttractionID.get(destAttractionIndex));
            }
            Coordinate srcCoordinate = coordinateMap.get(listOfAttractionID.get(srcAttractionIndex));

            ArrayList<Long> listOfDistances =
                    getDistanceBetweenAttractionsSingleSource(srcCoordinate,destAttractionCoordinateArray);

            if(listOfDistances == null || listOfDistances.size()==0){
                System.out.println("error in city "+cityName+" attractionID "+Integer.toString(srcAttractionIndex));
            }
            for(int destAttractionIndex = srcAttractionIndex+1;destAttractionIndex< noOfAttractions;destAttractionIndex++){
                int destID = (int) listOfAttractionID.get(destAttractionIndex);
                int srcID = (int) listOfAttractionID.get(srcAttractionIndex);
                Long distance = listOfDistances.get(destAttractionIndex-srcAttractionIndex-1);
                SqlQueryExecutor.saveDistanceToDB(srcID,destID,distance,cityName);
            }

            try {
                Thread.sleep(8000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static ArrayList<Long> getDistanceBetweenAttractionsSingleSource(Coordinate srcCoordinate, Coordinate[] destCoordinate) {
        URL url = null;
        InputStream urlStream = null;
        String jsonResponse = null;
        String destinationList = "";
        for(Coordinate destination:destCoordinate){
            destinationList+=destination.toString();
            destinationList+="|";
        }
        if(destinationList.contains("|")){
            destinationList = destinationList.substring(0,destinationList.length()-1);
        }
        try {
            url = new URL("https://maps.googleapis.com/maps/api/distancematrix/json?origins="
                    +srcCoordinate.toString()+
                    "&destinations="+destinationList + "&mode=walking&key=" + Constants.GOOGLE_MAP_API_KEY);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        };
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            urlStream = connection.getInputStream();
            jsonResponse = getStringFromInputStream(urlStream);
        } catch (IOException ioe) {
            // handle your exception here
        }finally {
            try {
                urlStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(jsonResponse==null){
            return null;
        }
        else {
            try {
                return parseToGetDistanceInMetres(jsonResponse);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    private static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();

    }
    public static ArrayList<Long> parseToGetDistanceInMetres(String s) throws ParseException {
        ArrayList<Long> listOfDistances = new ArrayList<Long>();
        JSONParser parser=new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(s);
        String globalQueryStatus = (String) jsonObject.get("status");
        if(!globalQueryStatus.equals("OK")){
            System.out.println("query failed. error response: "+globalQueryStatus);
            return null;
        }
        JSONArray rows = (JSONArray) jsonObject.get("rows");
            JSONObject elements = (JSONObject) rows.get(0);
            JSONArray pairsOfPlaces = (JSONArray) elements.get("elements");

        for(Object pair:pairsOfPlaces){
            JSONObject pairOfCoordinates = (JSONObject) pair;
            JSONObject distanceInfo = (JSONObject) pairOfCoordinates.get("distance");
            String currentPairStaus = (String) pairOfCoordinates.get("status");
            if(!currentPairStaus.equals("OK")){
                System.out.println("error response for this pair of locations :"+currentPairStaus);
                System.out.println(pairsOfPlaces.toString());
            }
            else {
                Long distanceInMetres = (Long) distanceInfo.get("value");
                listOfDistances.add(distanceInMetres);
            }
        }

        return listOfDistances;
    }

}

