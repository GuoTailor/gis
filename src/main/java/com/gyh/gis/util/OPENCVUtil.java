//package com.gyh.gis.util;
//
//import org.apache.commons.lang3.StringUtils;
//import org.bytedeco.javacv.FFmpegFrameGrabber;
//import org.bytedeco.javacv.Frame;
//import org.bytedeco.javacv.FrameGrabber;
//import org.bytedeco.javacv.Java2DFrameConverter;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.imageio.ImageIO;
//import javax.imageio.stream.ImageOutputStream;
//import java.awt.image.BufferedImage;
//import java.io.*;
//import java.util.UUID;
//
///**
// * create by GYH on 2022/9/24
// */
//public class OPENCVUtil {
//    //使用javacv
//    public String cutImage(MultipartFile multipartFile, Integer number) throws IOException, ApiException {
//        String picPath = StringUtils.EMPTY;
//        FFmpegFrameGrabber ff = new FFmpegFrameGrabber(MultipartFileToFile(multipartFile));
//        try {
//            ff.start();
//            int i = 0;
//            int length = ff.getLengthInFrames();
//            Frame frame = null;
//            while (i < length) {
//                frame = ff.grabFrame();
//                //截取第几帧图片
//                if ((i > number) && (frame.image != null)) {
//                    //获取生成图片的路径
//                    //执行截图并放入指定位置
//                    picPath = doExecuteFrame(frame);
//                    break;
//                }
//                i++;
//            }
//            ff.stop();
//        } catch (FrameGrabber.Exception e) {
//            e.printStackTrace();
//        }
//        return picPath;
//    }
//    /**
//     * 截取缩略图
//     *
//     * @param f Frame
//     */
//    private static String doExecuteFrame(Frame f) throws IOException, ApiException {
//        String imagemat = "png";
//        if (null == f || null == f.image) {
//            return null;
//        }
//        Java2DFrameConverter converter = new Java2DFrameConverter();
//        BufferedImage bi = converter.getBufferedImage(f);
//        ByteArrayOutputStream bs = new ByteArrayOutputStream();
//
//        ImageOutputStream imOut = ImageIO.createImageOutputStream(bs);
//
//        ImageIO.write(bi, "png", imOut);
//        InputStream is = new ByteArrayInputStream(bs.toByteArray());
//
//        Integer available = is.available();
//        Long size = available.longValue();
//
//        String fileName = UUID.randomUUID().toString() + ".png";
//        StringBuilder stringBuilder = new StringBuilder(OssConstant.objectNamePrefix);
//        MtpOssUtil.OssConfig ossConfig = new MtpOssUtil().new OssConfig(OssConstant.bucketName, OssConstant.endpoint, OssConstant.accessKeyId, OssConstant.accessKeySecret);
//
//        String filePath = MtpOssUtil.upload(ossConfig, fileName, is, size);
//        return filePath;
//    }
//
//    //文件转化
//    private static File MultipartFileToFile(MultipartFile multiFile) {
//        // 获取文件名
//        String fileName = multiFile.getOriginalFilename();
//        // 获取文件后缀
//        String prefix = fileName.substring(fileName.lastIndexOf("."));
//        // 若需要防止生成的临时文件重复,可以在文件名后添加随机码
//
//        try {
//            File file = File.createTempFile(fileName, prefix);
//            multiFile.transferTo(file);
//            return file;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//}
