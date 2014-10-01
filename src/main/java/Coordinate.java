/**
 * Created by kartik.k on 9/30/2014.
 */
public class Coordinate {
    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    double latitude;
    double longitude;
    public Coordinate(double lat,double lng){
        latitude = lat;
        longitude = lng;
    }

    @Override
    public String  toString(){
        return Double.toString(latitude)+","+Double.toString(longitude);
    }

}
