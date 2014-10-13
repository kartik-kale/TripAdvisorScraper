import java.sql.*;
import java.util.*;

/**
 * Created by kartik.k on 9/25/2014.
 */
public class SqlQueryExecutor {
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DATABASE_NAME = "tripplanner";
    static final String DB_URL = "jdbc:mysql://" + Constants.SQL_DB_HOST + "/" + DATABASE_NAME;

    static final String USER = "root";
    static final String PASS = "password";
    public static final String CITY_MAPPING = "cityMapping";
    public static final String ATTRACTION_DETAIL = "attractiondetail";
    public static final String ATTRACTION_MAPPING = "attractionmapping";
    public static final String DISTANCE_BETWEEN_ATTRACTIONS = "distanceBetweenAttractions";
    private static final String ATTRACTION_CATEGORY_MAPPING = "attractionCategoryMapping";

    public static Connection getConnection() {
        try {
            Class.forName(JDBC_DRIVER);
            return DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean addAttractionToDB(Attraction attraction) {

        Connection conn = getConnection();
        PreparedStatement addToAttractionMappingStatement;
        boolean executedSuccessfully = true;

        try {
            PreparedStatement cleanupTables = conn.prepareStatement("ALTER TABLE " + DATABASE_NAME + "." + ATTRACTION_DETAIL + " AUTO_INCREMENT = 1;");
            cleanupTables.execute();
            cleanupTables = conn.prepareStatement("ALTER TABLE " + DATABASE_NAME + "." + ATTRACTION_MAPPING + " AUTO_INCREMENT = 1;");
            cleanupTables.execute();
            int cityID = getCityIdByName(attraction.getCityName());
            if (cityID == -1) {
                PreparedStatement addToCityMappingStatement =
                        conn.prepareStatement("insert into " + CITY_MAPPING + " (cityName) VALUES (?)");
                addToCityMappingStatement.setString(1, attraction.getCityName());
                executedSuccessfully &= addToCityMappingStatement.execute();
                cityID = getCityIdByName(attraction.getCityName());
                addToCityMappingStatement.close();
            }

            addToAttractionMappingStatement = conn.prepareStatement("insert into " + ATTRACTION_MAPPING + " (attractionName,cityID) VALUES (?,?)");
            addToAttractionMappingStatement.setString(1, attraction.getName());
            addToAttractionMappingStatement.setInt(2, cityID);
            executedSuccessfully = addToAttractionMappingStatement.execute();
            int attractionID = getAttractionIdByName(attraction.getName());

            PreparedStatement addToAttractionDetailStatement =
                    conn.prepareStatement("insert ignore into " + ATTRACTION_DETAIL +
                            "(attractionID, attractionReviewURL, attractionType, attractionFee, " +
                            "attractionVisitTime, attractionDescription, attractionLatitude, attractionLongitude," +
                            " attractionImageURL, noOfReviews, noOfStars, additionalInformation, activities)" +
                            " VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?,?);");


            addToAttractionDetailStatement.setInt(1, attractionID);
            addToAttractionDetailStatement.setString(2, attraction.getReviewLink());
            addToAttractionDetailStatement.setString(3, attraction.getType());
            if (attraction.isFee() == null) {
                addToAttractionDetailStatement.setNull(4, Types.BOOLEAN);
            } else {
                addToAttractionDetailStatement.setBoolean(4, attraction.isFee());
            }
            float recommendedVisitTime = attraction.getRecommendedTimeForVisitInHrs();
            if (recommendedVisitTime == 0) {
                addToAttractionDetailStatement.setNull(5, Types.FLOAT);
            } else {
                addToAttractionDetailStatement.setDouble(5, recommendedVisitTime);
            }
            addToAttractionDetailStatement.setString(6, attraction.getDescription());
            addToAttractionDetailStatement.setDouble(7, attraction.getLatitude());
            addToAttractionDetailStatement.setDouble(8, attraction.getLongitude());
            addToAttractionDetailStatement.setString(9, attraction.getImageLink());
            addToAttractionDetailStatement.setInt(10, attraction.getNoOfReviews());
            addToAttractionDetailStatement.setDouble(11, attraction.getNoOfStars());
            addToAttractionDetailStatement.setString(12, attraction.getAdditionalInformation());
            addToAttractionDetailStatement.setString(13, attraction.getActivities());

            executedSuccessfully &= addToAttractionDetailStatement.execute();
            addToAttractionDetailStatement.close();
            addToAttractionMappingStatement.close();
            conn.close();


        } catch (SQLException e) {
            executedSuccessfully = false;
            e.printStackTrace();
        }

        return executedSuccessfully;
    }

    private static int getCityIdByName(String cityName) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement selectCityIdStatement = connection.prepareStatement(
                "SELECT cityID FROM " + CITY_MAPPING + " WHERE cityName = ?");
        selectCityIdStatement.setString(1, cityName);
        ResultSet cityIdSet = selectCityIdStatement.executeQuery();
        int cityID = -1;
        if (cityIdSet.next()) {
            cityID = cityIdSet.getInt("cityID");
        }
        selectCityIdStatement.close();
        connection.close();
        return cityID;
    }

    private static int getAttractionIdByName(String attractionName) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement selectAttractionIdStatement = connection.prepareStatement(
                "SELECT attractionID FROM " + ATTRACTION_MAPPING + " WHERE attractionName = ?");
        selectAttractionIdStatement.setString(1, attractionName);
        ResultSet attractionIdSet = selectAttractionIdStatement.executeQuery();
        int attractionID = -1;
        if (attractionIdSet.next()) {
            attractionID = attractionIdSet.getInt("attractionID");
        }
        selectAttractionIdStatement.close();
        connection.close();
        return attractionID;
    }

    public static void cleanAllTables() {
        try {
            Connection connection = getConnection();
            PreparedStatement cleanAttractionDetails = connection.prepareStatement(
                    "DELETE FROM " + ATTRACTION_DETAIL + "Large");
            PreparedStatement cleanAttractionCategories = connection.prepareStatement(
                    "DELETE FROM " + ATTRACTION_CATEGORY_MAPPING);
            PreparedStatement cleanAttractionDistances = connection.prepareStatement(
                    "DELETE FROM " + DISTANCE_BETWEEN_ATTRACTIONS);
            PreparedStatement cleanAttractions = connection.prepareStatement(
                    "DELETE FROM " + ATTRACTION_MAPPING);
            PreparedStatement cleanAttractionCities = connection.prepareStatement(
                    "DELETE FROM " + CITY_MAPPING);
            cleanAttractionCategories.execute();
            cleanAttractionDistances.execute();
            cleanAttractionDetails.execute();
            cleanAttractions.execute();
            cleanAttractionCities.execute();

            cleanAttractionCities = connection.prepareStatement(
                    "ALTER TABLE " + CITY_MAPPING + " AUTO_INCREMENT = 1");
            cleanAttractions = connection.prepareStatement(
                    "ALTER TABLE " + ATTRACTION_MAPPING + " AUTO_INCREMENT = 1");

            cleanAttractionCities.execute();
            cleanAttractions.execute();
            cleanAttractionCategories.close();
            cleanAttractionDistances.close();
            cleanAttractionDetails.close();
            cleanAttractions.close();
            cleanAttractionCities.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static Map<Integer, Coordinate> getCoordinatesOfAllAttractions(String city) {
        try {
            Integer cityID = getCityIdByName(city);

            Connection conn = getConnection();

            PreparedStatement findAttractionsStatement;
            findAttractionsStatement = conn.prepareStatement(
                    "SELECT " + DATABASE_NAME + "." + ATTRACTION_DETAIL + ".attractionLongitude," + DATABASE_NAME + "." + ATTRACTION_DETAIL + ".attractionLatitude," +
                            DATABASE_NAME + "." + ATTRACTION_DETAIL + ".attractionID FROM " + DATABASE_NAME + "." + ATTRACTION_MAPPING + " INNER JOIN " + DATABASE_NAME + "." + ATTRACTION_DETAIL +
                            " ON " + DATABASE_NAME + "." + ATTRACTION_MAPPING + ".attractionID = " + DATABASE_NAME + "." + ATTRACTION_DETAIL + ".attractionID" +
                            " WHERE " + DATABASE_NAME + "." + ATTRACTION_MAPPING + ".cityID = ?");
            findAttractionsStatement.setInt(1, cityID);

            ResultSet resultSet = findAttractionsStatement.executeQuery();
            Map<Integer, Coordinate> attrationIDCoordinatesMap = new HashMap<Integer, Coordinate>();
            while (resultSet.next()) {
                int attractionID = resultSet.getInt("attractionID");
                Coordinate attractionCoordinates = new Coordinate(
                        resultSet.getDouble("attractionLatitude"),
                        resultSet.getDouble("attractionLongitude"));
                if (attractionCoordinates.equals(new Coordinate(0, 0))) {
                    System.out.println("null coordinate found for attraction ID " + Integer.toString(attractionID));
                } else {
                    attrationIDCoordinatesMap.put(attractionID, attractionCoordinates);
                }
            }

            conn.close();
            findAttractionsStatement.close();


            return attrationIDCoordinatesMap;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean saveDistanceToDB(int srcAttractionID, int destAttractionID, double distanceInMetres, String cityName) {
        int cityID = 0;
        try {
            cityID = getCityIdByName(cityName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        boolean executionSucceeded = addOneRowToDistanceTable(srcAttractionID, destAttractionID, distanceInMetres, cityID);
        executionSucceeded &= addOneRowToDistanceTable(destAttractionID, srcAttractionID, distanceInMetres, cityID);
        return executionSucceeded;
    }

    private static boolean addOneRowToDistanceTable(int srcAttractionID, int destAttractionID, double distanceInMetres, int cityID) {
        Connection conn = getConnection();
        PreparedStatement addToAttractionMappingStatement = null;
        boolean executedSuccessfully = true;
        try {
            PreparedStatement addDistanceStatement =
                    conn.prepareStatement(
                            "insert into " + DISTANCE_BETWEEN_ATTRACTIONS + " (attractionIDFirst, attractionIDSecond, cityID, distance)" +
                                    " VALUES (?,?,?,?)");
            addDistanceStatement.setInt(1, srcAttractionID);
            addDistanceStatement.setInt(2, destAttractionID);
            addDistanceStatement.setInt(3, cityID);
            addDistanceStatement.setDouble(4, distanceInMetres);
            executedSuccessfully &= addDistanceStatement.execute();
            return executedSuccessfully;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                conn.close();
                addToAttractionMappingStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (NullPointerException npe) {

            }
        }
    }

    public static boolean isThisAttractionPresentInDistanceMatrix(int attractionID, int totalNoOfAttractionsInTheCity) {
        Connection conn = getConnection();
        PreparedStatement addToAttractionMappingStatement = null;
        boolean attractionExists = false;
        boolean executedSuccessfully = true;
        try {
            PreparedStatement checkIfAttractionPresentInDB =
                    conn.prepareStatement(
                            "SELECT " + DATABASE_NAME + "." + DISTANCE_BETWEEN_ATTRACTIONS + ".attractionIDFirst" +
                                    " FROM " + DATABASE_NAME + "." + DISTANCE_BETWEEN_ATTRACTIONS +
                                    " WHERE " + DATABASE_NAME + "." + DISTANCE_BETWEEN_ATTRACTIONS + ".attractionIDFirst = ?");
            checkIfAttractionPresentInDB.setInt(1, attractionID);
            ResultSet resultSet = checkIfAttractionPresentInDB.executeQuery();
            int noOfEntriesPresent = 0;
            while (resultSet.next()) {
                noOfEntriesPresent++;
            }
            System.out.println(noOfEntriesPresent);
            attractionExists = (noOfEntriesPresent == (totalNoOfAttractionsInTheCity - 1));
            checkIfAttractionPresentInDB.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return attractionExists;
    }


    public static boolean addCategoryAttractionMappingAndReturnWhetherAttractionRelevant(String category, String attractionName) {
        Connection conn = getConnection();
        PreparedStatement addToAttractionMappingStatement = null;
        try {
            PreparedStatement addDistanceStatement =
                    conn.prepareStatement(
                            "insert into " + ATTRACTION_CATEGORY_MAPPING + " (attractionID, category)" +
                                    " VALUES (?,?)");
            int attractionID = getAttractionIdByName(attractionName);
            if (attractionID < 1) {
//                System.out.println("invalid attractionID; skipping");
                return false;
            }
            addDistanceStatement.setInt(1, attractionID);
            addDistanceStatement.setString(2, category);
            addDistanceStatement.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        } finally {
            try {
                conn.close();
                addToAttractionMappingStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (NullPointerException npe) {

            }
        }
    }

    public static void pickRelevantAttractionsToSaveInDb() {
        for (int i = 0; i < Constants.LIST_OF_CITIES.length; i++) {
            String cityName = Constants.LIST_OF_CITIES[i];
            if (cityName.equals("Riyadh")) {
                continue;
            }
            ArrayList<Attraction> listOfAllAttractions = getAllAttractionsForACity(cityName);
            listOfAllAttractions.sort(new Comparator<Attraction>() {
                @Override
                public int compare(Attraction o1, Attraction o2) {
                    return o1.getNoOfReviews() - o2.getNoOfReviews();
                }
            });
            List<Attraction> popularAttractionList = listOfAllAttractions.subList(listOfAllAttractions.size() - 30, listOfAllAttractions.size());

            Connection connection = getConnection();
            for (Attraction popularAttraction : popularAttractionList) {
                addAttractionToDB(popularAttraction);
            }
        }
    }

    public static ArrayList<Attraction> getAllAttractionsForACity(String city) {
        try {

            Integer cityID = getCityIdByName(city);

            //ArrayList<Attraction> attractionNames = new ArrayList<Attraction>();
            ArrayList<Attraction> attractionList = new ArrayList<Attraction>();

            Connection conn = getConnection();

            PreparedStatement findAttractionsStatement;
            findAttractionsStatement = conn.prepareStatement(
                    "SELECT " + DATABASE_NAME + "." + ATTRACTION_MAPPING + ".attractionName," + DATABASE_NAME + "." + ATTRACTION_MAPPING + ".attractionID," +
                            "  " + DATABASE_NAME + "." + ATTRACTION_DETAIL + ".noOfReviews, " + DATABASE_NAME + "." + ATTRACTION_DETAIL + ".noOfStars," +
                            "  " + DATABASE_NAME + "." + ATTRACTION_DETAIL + ".attractionReviewURL," + DATABASE_NAME + "." + ATTRACTION_DETAIL + ".attractionType," +
                            "  " + DATABASE_NAME + "." + ATTRACTION_DETAIL + ".attractionFee," + DATABASE_NAME + "." + ATTRACTION_DETAIL + ".attractionVisitTime," +
                            "  " + DATABASE_NAME + "." + ATTRACTION_DETAIL + ".attractionDescription," + DATABASE_NAME + "." + ATTRACTION_DETAIL + ".attractionLongitude," +
                            "  " + DATABASE_NAME + "." + ATTRACTION_DETAIL + ".attractionLatitude," + DATABASE_NAME + "." + ATTRACTION_DETAIL + ".attractionImageURL," +
                            "  " + DATABASE_NAME + "." + ATTRACTION_DETAIL + ".additionalInformation," + DATABASE_NAME + "." + ATTRACTION_DETAIL + ".activities FROM " +
                            DATABASE_NAME + "." + ATTRACTION_MAPPING + " INNER JOIN " + DATABASE_NAME + "." + ATTRACTION_DETAIL + " " +
                            "ON " + DATABASE_NAME + "." + ATTRACTION_MAPPING + ".attractionID = " +
                            DATABASE_NAME + "." + ATTRACTION_DETAIL + ".attractionID " +
                            "WHERE " + DATABASE_NAME + "." + ATTRACTION_MAPPING + ".cityID = ?;");
            findAttractionsStatement.setInt(1, cityID);

            ResultSet resultSet = findAttractionsStatement.executeQuery();

            while (resultSet.next()) {

                Attraction attraction = new Attraction();
                attraction.setName(resultSet.getString("attractionName"));
                attraction.setNoOfReviews(resultSet.getInt("noOfReviews"));
                attraction.setNoOfStars((float) resultSet.getDouble("noOfStars"));
                attraction.setReviewLink(resultSet.getString("attractionReviewURL"));
                attraction.setType(resultSet.getString("attractionType"));
                attraction.setFee(resultSet.getBoolean("attractionFee"));
                attraction.setRecommendedTimeForVisitInHrs((float) resultSet.getDouble("attractionVisitTime"));
                attraction.setDescription(resultSet.getString("attractionDescription"));
                attraction.setLongitude(resultSet.getDouble("attractionLongitude"));
                attraction.setLatitude(resultSet.getDouble("attractionLatitude"));
                attraction.setImageLink(resultSet.getString("attractionImageURL"));
                attraction.setAdditionalInformation(resultSet.getString("additionalInformation"));
                attraction.setActivities(resultSet.getString("activities"));
                attraction.setCityName(city);
                attractionList.add(attraction);
            }

            conn.close();
            findAttractionsStatement.close();


            return attractionList;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean checkIfAllLatLongFoundCorrectly(String cityName) {

        int noOfAttractionsWithRightCoordinates = getCoordinatesOfAllAttractions(cityName).size();
        Connection conn = getConnection();

        PreparedStatement findAttractionsStatement;
        try {
            findAttractionsStatement = conn.prepareStatement("SELECT attractionID,attractionName FROM " +
                    "tripplanner.attractiondetail NATURAL JOIN tripplanner.attractionmapping WHERE " +
                    "attractionLatitude=0 AND attractionLongitude=0;");
            ResultSet resultSet = findAttractionsStatement.executeQuery();
            int noOfAttractions = 0;
            while (resultSet.next()) {

                noOfAttractions = resultSet.getInt("noOfAttractions");
            }
            return (noOfAttractionsWithRightCoordinates == noOfAttractions);

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean checkIfCategoryScrapedForAllAttractions(String cityName) {

        Connection conn = getConnection();

        PreparedStatement findAttractionWithCategoryNullStatement;
        try {
            findAttractionWithCategoryNullStatement = conn.prepareStatement("SELECT * FROM " + ATTRACTION_MAPPING +
                    " LEFT OUTER JOIN " + ATTRACTION_CATEGORY_MAPPING + " ON " +
                    "  tripplanner.attractionmapping.attractionID = tripplanner.attractioncategorymapping.attractionID" +
                    "  WHERE category IS null AND cityID = ?");
            findAttractionWithCategoryNullStatement.setInt(1, getCityIdByName(cityName));
            ResultSet resultSet = findAttractionWithCategoryNullStatement.executeQuery();
            boolean doesAnyAttractionHaveNullCategory = true;
            if (!resultSet.next()) {
                doesAnyAttractionHaveNullCategory = false;
            }

            conn.close();
            return doesAnyAttractionHaveNullCategory;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void fillAvgOfThatCategoryInVisitTimeIfVisitTimeNull(String cityName) {
        Connection conn = getConnection();

        PreparedStatement getAvgVisitTimeByCategory;
        try {
            getAvgVisitTimeByCategory = conn.prepareStatement("SELECT avgVisitTimes.Avg AS Avg, nullVisitTimes.ID AS ID " +
                    " FROM " +
                        "(SELECT AVG(attractionVisitTime) AS Avg, category AS category" +
                        " FROM tripplanner.attractioncategorymapping NATURAL JOIN tripplanner.attractiondetail" +
                        " WHERE attractionVisitTime IS NOT NULL     GROUP BY category)" +
                    " AS avgVisitTimes " +
                    "NATURAL JOIN " +
                        "(SELECT attractionID AS ID, category AS category FROM" +
                    " tripplanner.attractiondetail NATURAL JOIN tripplanner.attractioncategorymapping " +
                            "WHERE attractionVisitTime IS NULL)" +
                    " AS nullVisitTimes;");
            ResultSet resultSet = getAvgVisitTimeByCategory.executeQuery();
            HashMap<Integer, Double> avgVisitTimeIdMap = new HashMap<Integer, Double>();
            while (resultSet.next()) {
                avgVisitTimeIdMap.put(resultSet.getInt("ID"), resultSet.getDouble("Avg"));
            }


            for (Integer id : avgVisitTimeIdMap.keySet()) {
                Double avgVisitTimeForThisCategory = avgVisitTimeIdMap.get(id);
                PreparedStatement updateVisitTimeStatement = conn.prepareStatement("UPDATE " + ATTRACTION_DETAIL + " SET " +
                        "attractionVisitTime = ? WHERE attractionID = ?");
                updateVisitTimeStatement.setInt(2, id);
                updateVisitTimeStatement.setDouble(1, avgVisitTimeForThisCategory);
                updateVisitTimeStatement.execute();
                updateVisitTimeStatement.close();
                getAvgVisitTimeByCategory.close();
                resultSet.close();
            }

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
