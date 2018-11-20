package helpers;

import com.byteowls.jopencage.JOpenCageGeocoder;
import com.byteowls.jopencage.model.JOpenCageComponents;
import com.byteowls.jopencage.model.JOpenCageResponse;
import com.byteowls.jopencage.model.JOpenCageReverseRequest;
import database.entity.Address;

public class GeocodingHelper {
    private static JOpenCageGeocoder instance;

    private static JOpenCageGeocoder getGeocoderInstance() {
        if (instance == null)
            instance = new JOpenCageGeocoder(Config.geocodingApiKey);
        return instance;
    }

    public static Address reverseGeocode(Double lat, Double lon) {
        JOpenCageReverseRequest request = new JOpenCageReverseRequest(lat, lon);
        request.setNoAnnotations(true);

        JOpenCageResponse response = getGeocoderInstance().reverse(request);
        JOpenCageComponents components = null;
        Address address = null;
        if (response == null || response.getStatus().getCode() != 200 || lat == null || lon == null)
            return address;
        if (response.getResults() != null && !response.getResults().isEmpty())
            components = response.getResults().get(0).getComponents();
        if (components != null) {
            address = new Address(components.getCity(), components.getRoad(), components.getPostcode(),
                    components.getHouseNumber(), components.getCountry());
        }
        return address;
    }

}
