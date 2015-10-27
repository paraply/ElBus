package se.elbus.oaakee.restapi;

import se.elbus.oaakee.restapi.ecmodel.busInfo;

import java.util.List;

public interface mEcCallback {
    void handleSensorData(List<busInfo> busInfo);

    void handleSensorDataFromAllBuses(List<busInfo> busInfo);

    void handleResourceData(List<busInfo> busInfo);

    void handleResourceDataFromAllBuses(List<busInfo> busInfo);

    void handleError(String during_method, String error_msg);
}
