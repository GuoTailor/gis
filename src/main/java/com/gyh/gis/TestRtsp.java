package com.gyh.gis;

import com.gyh.gis.lib.NetSDKLib;
import com.gyh.gis.lib.ToolKits;
import com.gyh.gis.module.LoginModule;
import com.sun.jna.CallbackThreadInitializer;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * create by GYH on 2023/9/15
 */
public class TestRtsp {
    private static final Logger log = LoggerFactory.getLogger(TestRtsp.class);
    // device disconnect callback instance
    private static final DisConnect disConnect = new DisConnect();

    // device reconnect callback instance
    private static final HaveReConnect haveReConnect = new HaveReConnect();
    public fCaptureReceiveCB m_CaptureReceiveCB = new fCaptureReceiveCB();
    public fCaptureReceiveCB m_CaptureReceiveCB2 = new fCaptureReceiveCB();
    private final Semaphore semaphore = new Semaphore(1);
    private final AtomicInteger chnAtom = new AtomicInteger(0);
    private int type = 1;

    /**
     * 解析视频地址并截图
     */
    public void getVideoImagePathByRSTP(String ip, String username, String password) throws InterruptedException {
        //创建rstp流对象
        LoginModule loginModule = new LoginModule();
        if (loginModule.login(ip, 37777, username, password)) {
            log.info("登录成功");
            if (type == 2)
                LoginModule.netsdk.CLIENT_SetSnapRevCallBack(m_CaptureReceiveCB, null);
            else if (type == 3) {
                LoginModule.netsdk.CLIENT_SetSnapRevCallBack(m_CaptureReceiveCB2, null);
            }
            for (int i = 1; i < loginModule.m_stDeviceInfo.byChanNum + 1; i++) {
                int oldChn;
                do {
                    oldChn = chnAtom.get();
                    semaphore.tryAcquire(10, TimeUnit.SECONDS);
                } while (!chnAtom.compareAndSet(oldChn, i));
                log.info("通道{}", i);
                boolean b = snapPicture(i, loginModule);
                log.info("通道" + i + " 抓拍" + b);
            }
//            loginModule.logout();
        } else {
            log.info("登录失败 {} {}", ip, 37777);
        }
    }

    private static boolean snapPicture(int chn, LoginModule loginModule) {
        // send caputre picture command to device
        NetSDKLib.SNAP_PARAMS stuSnapParams = new NetSDKLib.SNAP_PARAMS();
        stuSnapParams.Channel = chn;            // channel
        stuSnapParams.mode = 0;                // capture picture mode
        stuSnapParams.Quality = 3;                // picture quality
        stuSnapParams.InterSnap = 0;    // timer capture picture time interval
        stuSnapParams.CmdSerial = 0;            // request serial

        IntByReference reserved = new IntByReference(0);
        if (!LoginModule.netsdk.CLIENT_SnapPictureEx(loginModule.m_hLoginHandle, stuSnapParams, reserved)) {
            System.err.printf("CLIENT_SnapPictureEx Failed!" + ToolKits.getErrorCodePrint());
            return false;
        } else {
            System.out.println("CLIENT_SnapPictureEx success");
        }
        return true;
    }

    /////////////////function///////////////////
    // device disconnect callback class
    // set it's instance by call CLIENT_Init, when device disconnect sdk will call it.
    private static class DisConnect implements NetSDKLib.fDisConnect {
        public void invoke(NetSDKLib.LLong m_hLoginHandle, String pchDVRIP, int nDVRPort, Pointer dwUser) {
            System.out.printf("Device[%s] Port[%d] DisConnect!\n", pchDVRIP, nDVRPort);

            SwingUtilities.invokeLater(() -> log.info("捕获图片：断开重新连接"));
        }
    }

    // device reconnect(success) callback class
    // set it's instance by call CLIENT_SetAutoReconnect, when device reconnect success sdk will call it.
    private static class HaveReConnect implements NetSDKLib.fHaveReConnect {
        public void invoke(NetSDKLib.LLong m_hLoginHandle, String pchDVRIP, int nDVRPort, Pointer dwUser) {
            System.out.printf("ReConnect Device[%s] Port[%d]\n", pchDVRIP, nDVRPort);

            SwingUtilities.invokeLater(() -> log.info("定时截取活动中窗口:在线"));
        }
    }

    public class fCaptureReceiveCB implements NetSDKLib.fSnapRev {
        BufferedImage bufferedImage = null;

        public void invoke(NetSDKLib.LLong lLoginID, Pointer pBuf, int RevLen, int EncodeType, int CmdSerial, Pointer dwUser) {
            log.info("回调 {} {} {} {} {}", pBuf, RevLen, EncodeType, CmdSerial, dwUser);
            try {
                if (pBuf != null && RevLen > 0) {
                    String strFileName = "./pic-test/" + type + "-" + chnAtom.get() + ".jpg";

                    log.info("strFileName = " + strFileName);

                    byte[] buf = pBuf.getByteArray(0, RevLen);
                    ByteArrayInputStream byteArrInput = new ByteArrayInputStream(buf);
                    try {
                        bufferedImage = ImageIO.read(byteArrInput);
                        if (bufferedImage == null) {
                            return;
                        }
                        ImageIO.write(bufferedImage, "jpg", new File(strFileName));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } finally {
                semaphore.release();
            }
        }
    }

    public static void main(String[] args) {
        Map<String, LoginInfo> map = Map.of("192.168.2.203", new LoginInfo("admin", "admin123"), "192.168.2.205", new LoginInfo("admin", "admin123"));
        AtomicInteger i = new AtomicInteger();

        TestRtsp testRtsp = new TestRtsp();
        LoginModule.init(disConnect, haveReConnect);   // init sdk
        Native.setCallbackThreadInitializer(testRtsp.m_CaptureReceiveCB,
                new CallbackThreadInitializer(false, false, "snapPicture callback thread"));
        Native.setCallbackThreadInitializer(testRtsp.m_CaptureReceiveCB2,
                new CallbackThreadInitializer(false, false, "snapPicture callback thread2"));
        map.entrySet().parallelStream().forEach(it -> {
            try {
                testRtsp.type++;
                int andIncrement = i.getAndIncrement();
                log.info(" {} 开始 {}", new Date(), andIncrement);
                testRtsp.getVideoImagePathByRSTP(it.getKey(), it.getValue().username, it.getValue().password);
                Thread.sleep(10_000);
                log.info("{}结束", new Date());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
