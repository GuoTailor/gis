package com.gyh.gis.util;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * create by GYH on 2023/9/16
 */
public class VideoUtil {
    private static final Logger log = LoggerFactory.getLogger(VideoUtil.class);

    public static String saveImg(Integer stationId, String path, String url) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy/MM/dd-HH-mm-ss-");
        LocalDateTime dateTime = LocalDateTime.now();
        String format = fmt.format(dateTime);
        String filePath = path + format + stationId + ".jpg";
        String filePathUrl = format + stationId + ".jpg";
        try {
            getVideoImagePathByRSTP(url, filePath, 100);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
            return e.getLocalizedMessage();
        }
        return filePathUrl;
    }

    /**
     * 解析视频地址并截图
     *
     * @param url    rstp 流地址
     * @param picPath 图片存放地址
     */
    public static void getVideoImagePathByRSTP(String url, String picPath, int count) {
        //创建rstp流对象
        try (FFmpegFrameGrabber grabber = FFmpegFrameGrabber.createDefault(url);
             Java2DFrameConverter converter = new Java2DFrameConverter()) {
            grabber.setOption("stimeout", "10000000");
            grabber.setOption("timeout", "10000000");
            //设置帧率
            grabber.setFrameRate(25);
            //设置获取的视频宽度
            grabber.setImageWidth(1920);
            //设置获取的视频高度
            grabber.setImageHeight(1080);
//            grabber.setVideoBitrate(709);

            //开启流获取
            grabber.start();
            //由于视频第一帧的流可能为黑屏 为了确保实时能截取到准确图像
            // 故此做了个for循环用于覆盖生成图片
            for (int i = 0; i < count; ) {
                //获取流视频框内的图像
                Frame frame = grabber.grabFrame();
                if (frame == null || frame.image == null) {
                    log.info("跳过 》》》》》》》》》》{}", i);
                    continue;
                }
                i++;
                if (i >= (count - 1)) {
                    //转换图像
                    BufferedImage srcImage = converter.getBufferedImage(frame);
                    if (srcImage != null) {
                        //创建文件
                        File file = new File(picPath);
                        if (!file.exists()) {
                            file.getParentFile().mkdirs();
                            file.createNewFile();
                        }
                        //输出文件
                        ImageIO.write(srcImage, "jpg", file);
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }
}
