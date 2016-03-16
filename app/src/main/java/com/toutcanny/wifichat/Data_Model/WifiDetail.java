package com.toutcanny.wifichat.Data_Model;

/**
 * Created by Farhan on 09-03-2016.
 */
public class WifiDetail {
    private String SSID;
    private String port;
    private String IP_Address;

    public WifiDetail()
    {
        SSID=port=IP_Address=null;
    }

    public WifiDetail(String SSID, String IP_Address, String port) {
        this.SSID = SSID;
        this.IP_Address = IP_Address;
        this.port = port;
    }

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getIP_Address() {
        return IP_Address;
    }

    public void setIP_Address(String IP_Address) {
        this.IP_Address = IP_Address;
    }
}
