package se.elbus.oaakee.restapi;

import java.util.List;

import se.elbus.oaakee.restapi.ecmodel.busInfo;

public interface ECCallback {
    void handleSensorData(List<busInfo> busInfo);

    void handleSensorDataFromAllBuses(List<busInfo> busInfo);

    void handleResourceData(List<busInfo> busInfo);

    void handleResourceDataFromAllBuses(List<busInfo> busInfo);

    void handleError(String during_method, String error_msg);
}
