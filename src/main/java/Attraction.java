/**
 * Created by kartik.k on 9/24/2014.
 */
public class Attraction {
    private String name;

    public Attraction() {
    }

    public void setName(String name) {

        this.name = name;
    }

    public void setReviewLink(String reviewLink) {
        this.reviewLink = reviewLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public void setNoOfReviews(int noOfReviews) {
        this.noOfReviews = noOfReviews;
    }

    public void setNoOfStars(float noOfStars) {
        this.noOfStars = noOfStars;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFee(Boolean fee) {
        this.fee = fee;
    }

    public void setRecommendedTimeForVisitInHrs(float recommendedTimeForVisitInHrs) {
        this.recommendedTimeForVisitInHrs = recommendedTimeForVisitInHrs;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setActivities(String activities) {
        this.activities = activities;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    private String reviewLink;
    private String imageLink;
    private int noOfReviews;
    private float noOfStars;

    private String type;
    private String category;
    private String description;

    private Boolean fee;
    private float recommendedTimeForVisitInHrs;
    private double latitude;
    private double longitude;
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

    public Boolean isFee() {
        return fee;
    }

    public float getRecommendedTimeForVisitInHrs() {
        return recommendedTimeForVisitInHrs;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
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

    public Attraction(String name, String reviewLink, String imageLink, int noOfReviews, float noOfStars, String type, String category, String description, Boolean fee, float recommendedTimeForVisitInHrs, double latitude, double longitude, String cityName, String activities, String additionalInformation) {
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
