package com.xuecheng.filesystem.controller;

import com.xuecheng.api.filesystem.FileSystemControllerApi;
import com.xuecheng.filesystem.service.FileSystemService;
import com.xuecheng.framework.domain.filesystem.FileSystem;
import com.xuecheng.framework.domain.filesystem.response.FileSystemCode;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("filesystem")
public class FileSystemController extends BaseController implements FileSystemControllerApi {

    @Autowired
    private FileSystemService fileSystemService;

    /**
     * 文件上传
     *
     * @param file        文件
     * @param filetag     文件标签
     * @param businesskey 业务key
     * @param metadata    元数据, JSON格式
     * @return UploadFileResult 上传结果
     */
    @Override
    @PostMapping("upload")
    public UploadFileResult upload(@RequestParam("file") MultipartFile file,
                                   @RequestParam("filetag")String filetag,
                                   @RequestParam(value = "businesskey", required = false)String businesskey,
                                   @RequestParam(value = "metadata", required = false)String metadata) {
        FileSystem upload = fileSystemService.upload(file, filetag, businesskey, metadata);
        isNullOrEmpty(upload, FileSystemCode.FS_UPLOADFILE_SERVERFAIL);
        return new UploadFileResult(CommonCode.SUCCESS, upload);
    }
}
