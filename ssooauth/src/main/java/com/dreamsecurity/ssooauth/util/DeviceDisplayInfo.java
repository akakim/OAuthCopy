package com.dreamsecurity.ssooauth.util;

public class DeviceDisplayInfo {

    public static boolean isMdpi(int densityDpi){ return ( densityDpi <= 160);}

    public static boolean isHdpi(int densityDpi){
        if( isMdpi( densityDpi )) return false;

        return  (densityDpi<= 240);
    }

    public static boolean isXhdpi(int densityDpi){
        if( isMdpi( densityDpi )) return false;
        if( isHdpi( densityDpi )) return false;

        return true;
    }
}
