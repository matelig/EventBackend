package model;

import database.entity.Event;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class EventsFilter {
    private List<String> tagIds;
    private List<String> cities;
    private List<String> categoryIds;
    private Boolean freeEntry;
    private Double maxPrice;
    private Long startDate;
    private Long endDate;

    public EventsFilter(HttpServletRequest request) {
        String[] tagIdsList = request.getParameterMap().get("tagId");
        if (tagIdsList != null && tagIdsList.length > 0) {
            this.tagIds = Arrays.asList(tagIdsList);
        }
        String[] citiesList = request.getParameterMap().get("city");
        if (citiesList != null && citiesList.length > 0) {
            this.cities = Arrays.asList(citiesList);
        }
        String[] categoryIdsList = request.getParameterMap().get("categoryId");
        if (categoryIdsList != null && categoryIdsList.length > 0) {
            this.categoryIds = Arrays.asList(categoryIdsList);
        }
        String freeEntryParam = request.getParameter("freeEntry");
        if (freeEntryParam != null) {
            freeEntry = Boolean.parseBoolean(freeEntryParam);
        }
        String maxPriceParam = request.getParameter("maxPrice");
        if (maxPriceParam != null) {
            maxPrice = Double.parseDouble(maxPriceParam);

        }
        String startDateParam = request.getParameter("startDate");
        if (startDateParam != null) {
            startDate = Long.parseLong(startDateParam);
        }
        String endDateParam = request.getParameter("endDate");
        if (endDateParam != null) {
            endDate = Long.parseLong(endDateParam);
        }
    }

    public Predicate<Event> getCategoryPredicate() {
        return event -> categoryIds == null || categoryIds.isEmpty() || categoryIds.contains(event.getCategoryId());
    }

    public Predicate<Event> getCityPredicate() {
        return event -> cities == null
                || cities.isEmpty()
                || event.getAddress() == null
                || event.getAddress().getCity() == null
                || cities.contains(event.getAddress().getCity());
    }

    public Predicate<Event> getFreeEntryPredicate() {
        return event -> freeEntry == null || (!freeEntry && event.getCost() > (double) 0)
                || (freeEntry && event.getCost().equals((double) 0));
    }

    public Predicate<Event> getMaxPricePredicate() {
        return event -> maxPrice == null || event.getCost() < maxPrice;
    }

    public Predicate<Event> getStartDatePredicate() {
        return event -> startDate == null || event.getStartDate() < startDate;
    }

    public Predicate<Event> getEndDatePredicate() {
        return event -> endDate == null || event.getEndDate() < endDate;
    }

}
