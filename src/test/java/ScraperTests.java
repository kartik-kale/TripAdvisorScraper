import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * Created by kartik.k on 9/30/2014.
 */
public class ScraperTests {
    @Test
    public void shouldProduceCorrectJsonOfDistanceMatrixFromDBValues(){
        Map<Integer,Coordinate> coordinateMap = SqlQueryExecutor.getCoordinatesOfAllAttractions("Bangkok");
        System.out.println(DistanceMatrixCalculator.getDistanceBetweenAttractionsSingleSource(
                coordinateMap.get(1),
                new Coordinate[]{coordinateMap.get(2),coordinateMap.get(3),coordinateMap.get(4)}
        ));
    }

    @Test
    public void shouldFetchAllCoordinates(){
        System.out.println(SqlQueryExecutor.getCoordinatesOfAllAttractions("Bangkok"));
    }

    @Test
    public void shouldAllowStoringDistancesIntoDB(){
        SqlQueryExecutor.saveDistanceToDB(1,3,2000,"Bangkok");
    }

    @Test
    public void shouldCheckIfAttractionInDBCorrectly(){
        Assert.assertTrue(SqlQueryExecutor.isThisAttractionPresentInDistanceMatrix(64,Constants.getNoOfAttractions("London")));
//        Assert.assertFalse(SqlQueryExecutor.isThisAttractionPresentInDistanceMatrix(9, 30));
    }




}
