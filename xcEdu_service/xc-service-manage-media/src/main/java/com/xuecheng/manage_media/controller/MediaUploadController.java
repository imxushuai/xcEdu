package com.xuecheng.manage_media.controller;

import com.xuecheng.api.media.MediaUploadControllerApi;
import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.domain.media.response.MediaCode;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_media.service.MediaUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("media/upload")
public class MediaUploadController implements MediaUploadControllerApi {

    @Autowired
    private MediaUploadService mediaUploadService;


    /**
     * 文件上传准备
     *
     * @param fileMd5  文件md5码
     * @param fileName 文件名称
     * @param fileSize 文件大小
     * @param mimetype 文件mimetype
     * @param fileExt  文件扩展名
     * @return ResponseResult
     */
    @Override
    @PostMapping("register")
    public ResponseResult register(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt) {
        mediaUploadService.register(fileMd5, fileName, fileSize, mimetype, fileExt);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 检查分块文件
     *
     * @param fileMd5   文件md5码
     * @param chunk     当前分块编号
     * @param chunkSize 分块大小
     * @return CheckChunkResult
     */
    @Override
    @PostMapping("checkchunk")
    public CheckChunkResult checkchunk(String fileMd5, Integer chunk, Integer chunkSize) {
        boolean checkChunk = mediaUploadService.checkChunk(fileMd5, chunk, chunkSize);
        return new CheckChunkResult(MediaCode.CHUNK_FILE_EXIST_CHECK, checkChunk);
    }

    /**
     * 上传分块
     *
     * @param file    分块文件
     * @param chunk   当前分块编号
     * @param fileMd5 文件md5码
     * @return ResponseResult
     */
    @Override
    @PostMapping("uploadchunk")
    public ResponseResult uploadchunk(MultipartFile file, Integer chunk, String fileMd5) {
        mediaUploadService.uploadChunk(file, chunk, fileMd5);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 合并分块并保存数据库
     *
     * @param fileMd5  文件md5码
     * @param fileName 文件名称
     * @param fileSize 文件大小
     * @param mimetype 文件mimetype
     * @param fileExt  文件扩展名
     * @return ResponseResult
     */
    @Override
    @PostMapping("mergechunks")
    public ResponseResult mergechunks(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt) {
        mediaUploadService.mergeChunks(fileMd5, fileName, fileSize, mimetype, fileExt);
        return new ResponseResult(CommonCode.SUCCESS);
    }
}
