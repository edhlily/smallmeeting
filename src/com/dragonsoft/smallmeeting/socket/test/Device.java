package com.dragonsoft.smallmeeting.socket.test;
import java.io.Serializable;
import java.net.InetSocketAddress;
public class Device implements Serializable {
	private static final long serialVersionUID = 1L;
	public InetSocketAddress socketAddress;
    public String sid;
    public String tvType;
    public String model;
    public boolean master;
    public long time;
    public int port;
    public String syncCode;

    @Override
    public boolean equals(Object o) {
        if(o instanceof Device){
            if(((Device) o).sid == null){
                return  sid == null;
            }else{
                return ((Device) o).sid.equals(sid);
            }
        }
        return false;
    }
}
