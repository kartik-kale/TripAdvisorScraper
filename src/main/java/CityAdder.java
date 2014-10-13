import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Created by kartik.k on 10/13/2014.
 */
public class CityAdder {
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
        String[] citiesToAdd = {"Delhi"};
        for (String city : citiesToAdd) {
//            Main.getDetailsOfOneCity(city);
//            AttractionCategoryFinder.findCategoriesForOneCity(city);
            if(SqlQueryExecutor.checkIfAllLatLongFoundCorrectly(city)){
                DistanceMatrixCalculator.findAndStoreDistanceMAtrixForOneCity(city);
            }
            else{
                LOGGER.warning("not all latLong values scraped properly; cannot proceed to find distances.");
            }
            if (SqlQueryExecutor.checkIfCategoryScrapedForAllAttractions(city)) {
                SqlQueryExecutor.fillAvgOfThatCategoryInVisitTimeIfVisitTimeNull(city);
            } else {
                LOGGER.warning("not all attractions have a category.");
            }
        }
    }
}
