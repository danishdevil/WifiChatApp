package com.toutcanny.wifichat.Data_Model;

/**
 * Created by Farhan on 15-03-2016.
 */
public class DeviceDetail {
    private String ip_address;
    private String name;

    public DeviceDetail(String ip_address, String name) {
        this.ip_address = ip_address;
        this.name = name;
    }

    public String getIp_address() {
        return ip_address;
    }

    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
