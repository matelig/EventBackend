package model;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;

public class AddEventRequest {
    @JsonProperty(value = "name")
    private String name;
    @JsonProperty(value = "description")
    private String description;
    @JsonProperty(value = "latitude")
    private String latitude;
    @JsonProperty(value = "longitude")
    private String longitude;
    @JsonProperty(value = "startDate")
    private String startDate;
    @JsonProperty(value = "endDate")
    private String endDate;
    @JsonProperty(value = "showGuestList")
    private String showGuestList;
    @JsonProperty(value = "maxParticipants")
    private String maxParticipants;
    @JsonProperty(value = "onlyRegistered")
    private String onlyRegistered;
    @JsonProperty(value = "externalUrl")
    private String externalUrl;
    @JsonProperty(value = "categoryId")
    private String categoryId;
    @JsonProperty(value = "cost")
    private String cost;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String isShowGuestList() {
        return showGuestList;
    }

    public void setShowGuestList(String showGuestList) {
        this.showGuestList = showGuestList;
    }

    public String getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(String maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public String isOnlyRegistered() {
        return onlyRegistered;
    }

    public void setOnlyRegistered(String onlyRegistered) {
        this.onlyRegistered = onlyRegistered;
    }

    public String getExternalUrl() {
        return externalUrl;
    }

    public void setExternalUrl(String externalUrl) {
        this.externalUrl = externalUrl;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }
}
