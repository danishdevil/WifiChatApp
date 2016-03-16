package com.toutcanny.wifichat.Data_Model;

import java.util.ArrayList;

/**
 * Created by Farhan on 16-03-2016.
 */
public class DeviceList {
    private ArrayList<String> names;
    private ArrayList<String> ipAddresses;

    public DeviceList() {
        this.names = new ArrayList<>();
        this.ipAddresses = new ArrayList<>();
    }

    public ArrayList<String> getNames() {
        return names;
    }

    public void setNames(ArrayList<String> names) {
        this.names = names;
    }

    public ArrayList<String> getIpAddresses() {
        return ipAddresses;
    }

    public void setIpAddresses(ArrayList<String> ipAddresses) {
        this.ipAddresses = ipAddresses;
    }

    public void addItem(String ip, String name)
    {
        if(ipAddresses.contains(ip))
        {
            int index=ipAddresses.indexOf(ip);
            names.remove(index);
            names.add(index,name);
            return;
        }
        names.add(name);
        ipAddresses.add(ip);
    }

    public String getName(String ip)
    {
        for(int i=0;i<=ipAddresses.size();++i)
        {
            if(ipAddresses.get(i).equals(ip))
                return names.get(i);
        }
        return null;
    }
}
