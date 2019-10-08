package com.xuecheng.manage_media_process.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.MediaFileProcess_m3u8;
import com.xuecheng.framework.domain.media.response.MediaCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.utils.HlsVideoUtil;
import com.xuecheng.framework.utils.Mp4VideoUtil;
import com.xuecheng.manage_media_process.dao.MediaFileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class MediaProcessTask {


    //ffmpeg绝对路径
    @Value("${xc-service-manage-media.ffmpeg-path}")
    private String ffmpeg_path;

    //上传文件根目录
    @Value("${xc-service-manage-media.video-location}")
    private String serverPath;

    @Autowired
    private MediaFileRepository mediaFileRepository;


    /**
     * 接收视频处理消息并处理对应视频格式
     *
     * @param msg 消息
     */
    @RabbitListener(queues = "${xc-service-manage-media.mq.queue-media-video-processor}", containerFactory = "customContainerFactory")
    public void receiveMediaProcessTask(String msg) {
        Map<String, String> msgMap = JSON.parseObject(msg, Map.class);
        log.info("[视频处理] 收到视频处理消息, msg = {}", msgMap.toString());

        String mediaId = msgMap.get("mediaId");

        // 获取文件
        MediaFile mediaFile = mediaFileRepository.findById(mediaId).orElse(null);
        if (mediaFile == null) {
            ExceptionCast.cast(MediaCode.MEDIA_FILE_NOT_EXIST);
        }
        // 判断文件类型
        String fileType = mediaFile.getFileType();
        if (fileType == null || !fileType.equals("avi")) {//目前只处理avi文件
            mediaFile.setProcessStatus("303004");//处理状态为无需处理
            mediaFileRepository.save(mediaFile);
            return;
        } else {
            mediaFile.setProcessStatus("303001");//处理状态为未处理
            mediaFileRepository.save(mediaFile);
        }

        // 生成mp4
        String mp4_name = mediaFile.getFileId() + ".mp4";
        if (!buildMp4(mediaFile)) {
            ExceptionCast.cast(MediaCode.MEDIA_BUILD_MP4_FAIL);
        }

        // 生成m3u8
        if (!buildM3u8(mediaFile, mp4_name)) {
            ExceptionCast.cast(MediaCode.MEDIA_BUILD_M3U8_FAIL);
        }

        log.info("[视频处理] 视频处理完成, mediaId = [{}]", mediaId);

    }

    /**
     * 使用MP4文件生成m3u8文件并保存信息到数据库
     *
     * @param mediaFile 源文件
     * @param mp4Name   mp4文件名
     * @return boolean
     */
    private boolean buildM3u8(MediaFile mediaFile, String mp4Name) {
        // mp4 url
        String video_path = serverPath + mediaFile.getFilePath() + mp4Name;
        // 生成后文件的存放位置
        String m3u8_name = mediaFile.getFileId() + ".m3u8";
        String m3u8folder_path = serverPath + mediaFile.getFilePath() + "hls/";
        // 生成m3u8文件
        HlsVideoUtil hlsVideoUtil = new HlsVideoUtil(ffmpeg_path, video_path, m3u8_name, m3u8folder_path);
        String result = hlsVideoUtil.generateM3u8();
        if (result == null || !result.equals("success")) {
            //操作失败写入处理日志
            processFail(result, mediaFile);
            return false;
        }
        //获取m3u8列表
        List<String> ts_list = hlsVideoUtil.get_ts_list();
        //更新处理状态为成功
        mediaFile.setProcessStatus("303002");//处理状态为处理成功
        MediaFileProcess_m3u8 mediaFileProcess_m3u8 = new MediaFileProcess_m3u8();
        mediaFileProcess_m3u8.setTslist(ts_list);
        mediaFile.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);
        //m3u8文件url
        mediaFile.setFileUrl(mediaFile.getFilePath() + "hls/" + m3u8_name);
        mediaFileRepository.save(mediaFile);

        return true;
    }

    /**
     * 生成MP4文件
     *
     * @param mediaFile 源文件
     * @return boolean
     */
    private boolean buildMp4(MediaFile mediaFile) {
        String video_path = serverPath + mediaFile.getFilePath() + mediaFile.getFileName();
        String mp4_name = mediaFile.getFileId() + ".mp4";
        String mp4folder_path = serverPath + mediaFile.getFilePath();
        Mp4VideoUtil videoUtil = new Mp4VideoUtil(ffmpeg_path, video_path, mp4_name, mp4folder_path);
        String result = videoUtil.generateMp4();
        if (result == null || !result.equals("success")) {
            //操作失败写入处理日志
            processFail(result, mediaFile);
            return false;
        }

        return true;
    }

    /**
     * 操作失败
     *
     * @param result    操作结果
     * @param mediaFile 文件
     */
    private void processFail(String result, MediaFile mediaFile) {
        mediaFile.setProcessStatus("303003");//处理状态为处理失败
        MediaFileProcess_m3u8 mediaFileProcess_m3u8 = new MediaFileProcess_m3u8();
        mediaFileProcess_m3u8.setErrormsg(result);
        mediaFile.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);
        mediaFileRepository.save(mediaFile);
    }


}
