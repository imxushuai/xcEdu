package com.xuecheng.api.filesystem;

import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import io.swagger.annotations.Api;
import org.springframework.web.multipart.MultipartFile;

@Api(value = "文件管理服务", description = "文件管理，提供文件的上传、下载等操作")
public interface FileSystemControllerApi {

    /**
     * 文件上传
     *
     * @param file        文件
     * @param filetag     文件标签
     * @param businesskey 业务key
     * @param metadata    元数据, JSON格式
     * @return UploadFileResult 上传结果
     */
    UploadFileResult upload(MultipartFile file,
                            String filetag,
                            String businesskey,
                            String metadata);

}
