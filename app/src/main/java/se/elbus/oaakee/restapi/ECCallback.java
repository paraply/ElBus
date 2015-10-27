package se.elbus.oaakee.restapi;

import se.elbus.oaakee.restapi.ecmodel.busInfo;

import java.util.List;

public interface ECCallback {
    void got_sensor_data(List<busInfo> busInfo);

    void got_sensor_data_from_all_buses(List<busInfo> busInfo);

    void got_reource_data(List<busInfo> busInfo);

    void got_reource_data_from_all_buses(List<busInfo> busInfo);

    void got_error(String during_method, String error_msg);
}
