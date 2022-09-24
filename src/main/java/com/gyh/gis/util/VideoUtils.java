package com.gyh.gis.util;


import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class VideoUtils {
    private static final Logger log = LoggerFactory.getLogger(VideoUtils.class);

    private static String rtspTransportType = "tcp";
    /**
     * * 视频帧率
     */
    private static int frameRate = 25;
    /**
     * 视频宽度
     */
    private static int frameWidth = 1920;
    /**
     * 视频高度
     */
    private static int frameHeight = 1080;
    /**
     * 遍历100次确保实时图片显示正常图片
     */
    private static int count = 100;

    /**
     * 解析视频地址并截图
     *
     * @param path    rstp 流地址
     * @param picPath 图片存放地址
     * @throws Exception
     */
    public static void getVideoImagePathByRSTP(String path, String picPath) throws Exception {
        //创建rstp流对象
        FFmpegFrameGrabber grabber = createGrabber(path);
        try {
            //开启流获取
            grabber.start();
            //由于视频第一帧的流可能为黑屏 为了确保实时能截取到准确图像
            // 故此做了个for循环用于覆盖生成图片
            for (int i = 0; i < count; i++) {
                //获取流视频框内的图像
                Frame frame = grabber.grabFrame();
                //转换图像
                Java2DFrameConverter converter = new Java2DFrameConverter();
                BufferedImage srcImage = converter.getBufferedImage(frame);
                //自定义画图
//            int w = srcImage.getWidth();
//            int h = srcImage.getHeight();
//            System.out.println(srcImage.);
//            int weight = 1280;
//            int height = new BigDecimal(weight).divide(new BigDecimal(w), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(h)).setScale(0, RoundingMode.HALF_UP).intValue();
//            BufferedImage bufferedImage = new BufferedImage(weight, height, BufferedImage.TYPE_3BYTE_BGR);
//            bufferedImage.getGraphics().drawImage(srcImage.getScaledInstance(weight, height, Image.SCALE_SMOOTH), 0, 0, null);
                if (srcImage != null) {
                    //创建文件
                    File file = new File(picPath);
                    if (!file.exists()) {
                        file.mkdirs();
                        file.createNewFile();
                    }
                    //输出文件
                    ImageIO.write(srcImage, "jpg", file);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            grabber.stop();
            grabber.close();
        }


    }


    /**
     * 构造视频抓取器
     *
     * @param rtsp 拉流地址
     * @return
     */
    private static FFmpegFrameGrabber createGrabber(String rtsp) {
        // 获取视频源
        try {
            FFmpegFrameGrabber grabber = FFmpegFrameGrabber.createDefault(rtsp);
            //设置传输方式 TCP | UDP
            grabber.setOption("rtsp_transport", rtspTransportType);
            //设置帧率
            grabber.setFrameRate(frameRate);
            //设置获取的视频宽度
            grabber.setImageWidth(frameWidth);
            //设置获取的视频高度
            grabber.setImageHeight(frameHeight);
            // 设置采集器构造超时时间(单位微秒，1秒=1000000微秒)
            //  grabber.setOption("stimeout", "2000000000");
            //设置视频bit率
            //   grabber.setVideoBitrate(2000000);
            return grabber;
        } catch (FrameGrabber.Exception e) {
            log.error("创建解析rtsp FFmpegFrameGrabber 失败");
            log.error("create rtsp FFmpegFrameGrabber exception: ", e);
            return null;
        }
    }


    public static void main(String[] args) {
        try {
            //参数1 rtsp 地址自行获取  参数2  截取图片存放地址
            VideoUtils.getVideoImagePathByRSTP("rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mp4", "E:/pic/1000.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
