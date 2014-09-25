/**
 * Created by kartik.k on 9/24/2014.
 */
public class Attraction {
    private String name;
    private String reviewLink;
    private String imageLink;
    private int noOfReviews;
    private float noOfStars;

    private String type;
    private String category;
    private String description;

    private boolean fee;
    private float recommendedTimeForVisitInHrs;
    private float latitude;
    private float longitude;
    private String cityName;

    private String activities;
    private String additionalInformation;

    public String getName() {
        return name;
    }

    public String getReviewLink() {
        return reviewLink;
    }

    public String getImageLink() {
        return imageLink;
    }

    public int getNoOfReviews() {
        return noOfReviews;
    }

    public float getNoOfStars() {
        return noOfStars;
    }

    public String getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public boolean isFee() {
        return fee;
    }

    public float getRecommendedTimeForVisitInHrs() {
        return recommendedTimeForVisitInHrs;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public String getCityName() {
        return cityName;
    }

    public String getActivities() {
        return activities;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public Attraction(String name, String reviewLink, String imageLink, int noOfReviews, float noOfStars, String type, String category, String description, boolean fee, float recommendedTimeForVisitInHrs, float latitude, float longitude, String cityName, String activities, String additionalInformation) {
        if(name==null || cityName == null ){
            System.out.println("Invalid attraction");
        }
        this.name = name;
        this.reviewLink = reviewLink;
        this.imageLink = imageLink;
        this.noOfReviews = noOfReviews;
        this.noOfStars = noOfStars;
        this.type = type;
        this.category = category;
        this.description = description;
        this.fee = fee;
        this.recommendedTimeForVisitInHrs = recommendedTimeForVisitInHrs;
        this.latitude = latitude;
        this.longitude = longitude;
        this.cityName = cityName;
        this.activities = activities;
        this.additionalInformation = additionalInformation;
    }
}
