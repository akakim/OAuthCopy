package com.dreamsecurity.ssooauth.util;

import com.dreamsecurity.ssooauth.common.logger.Logger;

import java.util.List;
import java.util.Map;

public class HttpConnectionUtil {

    private static final String TAG = "httpConnectionUtil";


    public static String getCharsetFromContenttypeHeader(Map<String, List<String>> headerMap){
        String rt = "utf-8";

        for( String key : headerMap.keySet() ){
            if ("Content-Type".equalsIgnoreCase(key))	{
                List<String> headerList = headerMap.get(key);

                for (String contentType : headerList) {

                    String[] elems = contentType.split(";");

                    if (null != elems) {
                        for (String elem : elems) {
                            if (elem.contains("charset")) {
                                String[] elems2 = elem.split("=");
                                if (null != elems2 && elems2[1].length() > 2) {
                                    rt = elems2[1];
                                }
                            }
                        }
                    }

                    if (!Logger.isRealVersion()) {
                        Logger.i(TAG, "encoding type from response : " + rt);
                    }

                }
            }
        }

        return rt;
    }
}
