package se.elbus.oaakee.restapi;

import java.util.List;

import se.elbus.oaakee.restapi.ecmodel.Bus_info;

/**
 * Created by paraply on 2015-10-05.
 */
public interface ECCallback {
    void got_sensor_data(List<Bus_info> bus_info);

    void got_sensor_data_from_all_buses(List<Bus_info> bus_info);

    void got_reource_data(List<Bus_info> bus_info);

    void got_reource_data_from_all_buses(List<Bus_info> bus_info);

    void got_error(String during_method, String error_msg);
}
