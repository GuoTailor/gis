package com.gyh.gis.lib;


import com.gyh.gis.module.LoginModule;

import java.text.SimpleDateFormat;

public class ToolKits {


    // 获取当前时间
    public static String getDate() {
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return simpleDate.format(new java.util.Date()).replace(" ", "_").replace(":", "-");
    }


    /**
     * 获取接口错误码和错误信息，用于打印
     */
    public static String getErrorCodePrint() {
        return "\n{error code: (0x80000000|" + (LoginModule.netsdk.CLIENT_GetLastError() & 0x7fffffff) + ").参考  NetSDKLib.java }"
                + " - {error info:" + ErrorCode.getErrorCode(LoginModule.netsdk.CLIENT_GetLastError()) + "}\n";
    }

}
