import java.sql.*;

/**
 * Created by kartik.k on 9/25/2014.
 */
public class SqlQueryExecutor {
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://" + Constants.SQL_DB_HOST + "/tripplanner";

    static final String USER = "root";
    static final String PASS = "password";
    public static Connection getConnection(){
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
    public static boolean addAttractionToDB(Attraction attraction){

        Connection conn = getConnection();
        PreparedStatement addToAttractionMappingStatement;
        boolean executedSuccessfully =true;
        try {

            int cityID = getCityIdByName(attraction.getCityName());
            if(cityID == -1){
                PreparedStatement addToCityMappingStatement =
                        conn.prepareStatement("insert into cityMapping (cityName) VALUES (?)");
                addToCityMappingStatement.setString(1, attraction.getCityName());
                executedSuccessfully &= addToCityMappingStatement.execute();
                cityID = getCityIdByName(attraction.getCityName());
                addToCityMappingStatement.close();
            }

            addToAttractionMappingStatement = conn.prepareStatement("insert into attractionMapping (attractionName,cityID) VALUES (?,?)");
            addToAttractionMappingStatement.setString(1, attraction.getName());
            addToAttractionMappingStatement.setInt(2,cityID);
            executedSuccessfully = addToAttractionMappingStatement.execute();
            int attractionID = getAttractionIdByName(attraction.getName());

            PreparedStatement addToAttractionDetailStatement =
                    conn.prepareStatement("insert ignore into attractiondetail" +
                            "(attractionID, attractionReviewURL, attractionType, attractionFee, " +
                            "attractionVisitTime, attractionDescription, attractionLatitude, attractionLongitude," +
                            " attractionImageURL, noOfReviews, noOfStars, additionalInformation, activities)" +
                            " VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?,?);");


            addToAttractionDetailStatement.setInt(1,attractionID);
            addToAttractionDetailStatement.setString(2,attraction.getReviewLink());
            addToAttractionDetailStatement.setString(3,attraction.getType());
            addToAttractionDetailStatement.setBoolean(4,attraction.isFee());
            addToAttractionDetailStatement.setDouble(5,attraction.getRecommendedTimeForVisitInHrs());
            addToAttractionDetailStatement.setString(6,attraction.getDescription());
            addToAttractionDetailStatement.setDouble(7,attraction.getLatitude());
            addToAttractionDetailStatement.setDouble(8,attraction.getLongitude());
            addToAttractionDetailStatement.setString(9,attraction.getImageLink());
            addToAttractionDetailStatement.setInt(10,attraction.getNoOfReviews());
            addToAttractionDetailStatement.setDouble(11,attraction.getNoOfStars());
            addToAttractionDetailStatement.setString(12,attraction.getAdditionalInformation());
            addToAttractionDetailStatement.setString(13,attraction.getActivities());

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
        PreparedStatement selectCityIdStatement=connection.prepareStatement(
                "SELECT cityID FROM cityMapping WHERE cityName = ?");
        selectCityIdStatement.setString(1,cityName);
        ResultSet cityIdSet = selectCityIdStatement.executeQuery();
        int cityID = -1;
        if(cityIdSet.next()){
            cityID = cityIdSet.getInt("cityID");
        }
        selectCityIdStatement.close();
        connection.close();
        return cityID;
    }

    private static int getAttractionIdByName(String attractionName) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement selectAttractionIdStatement=connection.prepareStatement(
                "SELECT attractionID FROM attractionMapping WHERE attractionName = ?");
        selectAttractionIdStatement.setString(1, attractionName);
        ResultSet attractionIdSet = selectAttractionIdStatement.executeQuery();
        int attractionID = -1;
        if(attractionIdSet.next()){
            attractionID = attractionIdSet.getInt("attractionID");
        }
        selectAttractionIdStatement.close();
        connection.close();
        return attractionID;
    }

    public static void cleanAllTables(){
        try {
            Connection connection = getConnection();
            PreparedStatement cleanAttractionDetails=connection.prepareStatement(
                    "DELETE FROM attractiondetail");
            PreparedStatement cleanAttractions=connection.prepareStatement(
                    "DELETE FROM attractionMapping");
            PreparedStatement cleanAttractionCities=connection.prepareStatement(
                    "DELETE FROM cityMapping");
            cleanAttractionDetails.execute();
            cleanAttractions.execute();
            cleanAttractionCities.execute();

            cleanAttractionCities = connection.prepareStatement(
            "ALTER TABLE cityMapping AUTO_INCREMENT = 1");
            cleanAttractions = connection.prepareStatement(
                    "ALTER TABLE attractionMapping AUTO_INCREMENT = 1");

            cleanAttractionCities.execute();
            cleanAttractions.execute();

            cleanAttractionDetails.close();
            cleanAttractions.close();
            cleanAttractionCities.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}