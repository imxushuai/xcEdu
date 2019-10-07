package com.xuecheng.manage_media.service;

import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.response.MediaCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.service.BaseService;
import com.xuecheng.manage_media.config.MediaServiceProperties;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MediaUploadService extends BaseService {

    @Autowired
    private MediaServiceProperties mediaServiceProperties;

    @Autowired
    private MediaFileRepository mediaFileRepository;


    /**
     * 文件上传准备
     * 1. 检查文件是否已存在
     * 2. 创建文件存放目录
     *
     * @param fileMd5  文件md5码
     * @param fileName 文件名称
     * @param fileSize 文件大小
     * @param mimetype 文件mimetype
     * @param fileExt  文件扩展名
     */
    public void register(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt) {
        // 获取文件路径
        String filePath = getFilePath(fileMd5, fileExt);
        File file = new File(filePath);

        // 获取数据库记录
        Optional<MediaFile> mediaFile = mediaFileRepository.findById(fileMd5);

        if (file.exists() && mediaFile.isPresent()) {// 文件已存在
            ExceptionCast.cast(MediaCode.UPLOAD_FILE_REGISTER_EXIST);
        }

        // 创建目录
        if (!createFileFolder(fileMd5)) {// 创建目录失败
            ExceptionCast.cast(MediaCode.UPLOAD_FILE_REGISTER_FAIL);
        }

    }

    /**
     * 检查分块文件
     *
     * @param fileMd5   文件md5码
     * @param chunk     当前分块编号
     * @param chunkSize 分块大小
     * @return boolean
     */
    public boolean checkChunk(String fileMd5, Integer chunk, Integer chunkSize) {
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        File file = new File(chunkFileFolderPath + chunk);
        return file.exists();
    }

    /**
     * 上传分块
     *
     * @param file    分块文件
     * @param chunk   当前分块编号
     * @param fileMd5 文件md5码
     */
    public void uploadChunk(MultipartFile file, Integer chunk, String fileMd5) {
        isNullOrEmpty(file, CommonCode.PARAMS_ERROR);
        // 创建分块文件目录
        createChunkFileFolder(fileMd5);
        File chunkFile = new File(getChunkFileFolderPath(fileMd5) + chunk);
        // 保存文件
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = file.getInputStream();
            outputStream = new FileOutputStream(chunkFile);
            IOUtils.copy(inputStream, outputStream);
        } catch (IOException e) {
            // 记录日志
            log.error("[上传分块文件] 保存分块文件失败, e = ", e);
            ExceptionCast.cast(MediaCode.UPLOAD_FILE_REGISTER_FAIL);
        } finally {
            // 关闭资源
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ignored) {
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    /**
     * 合并分块并保存数据库
     * 1. 合并文件
     * 2. 校验md5码
     * 3. 保存至数据库
     *
     * @param fileMd5  文件md5码
     * @param fileName 文件名称
     * @param fileSize 文件大小
     * @param mimetype 文件mimetype
     * @param fileExt  文件扩展名
     */
    public void mergeChunks(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt) {
        // 合并文件
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        File file = new File(chunkFileFolderPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        // 创建合并文件
        File mergeFile = new File(getFilePath(fileMd5, fileExt));
        if (mergeFile.exists()) {// 删除原有文件
            mergeFile.delete();
        } else {
            try {
                mergeFile.createNewFile();
            } catch (IOException e) {
                log.error("[合并文件分块] 合并文件分块时，创建合并文件失败, e = ", e);
                ExceptionCast.cast(MediaCode.MERGE_FILE_FAIL);
            }
        }
        List<File> chunkFiles = getChunkFiles(file);
        mergeFile = mergeFile(mergeFile, chunkFiles);

        // 校验md5码
        boolean b = checkMd5(mergeFile, fileMd5);
        if (!b) {
            ExceptionCast.cast(MediaCode.MERGE_FILE_CHECKFAIL);
        }

        // 保存数据库
        MediaFile mediaFile = new MediaFile();
        mediaFile.setFileId(fileMd5);
        mediaFile.setFileName(fileMd5 + "." + fileExt);
        mediaFile.setFileOriginalName(fileName);
        // 文件路径保存相对路径
        mediaFile.setFilePath(getFileFolderRelativePath(fileMd5, fileExt));
        mediaFile.setFileSize(fileSize);
        mediaFile.setUploadTime(new Date());
        mediaFile.setMimeType(mimetype);
        mediaFile.setFileType(fileExt);
        // 状态为上传成功
        mediaFile.setFileStatus("301002");
        mediaFileRepository.save(mediaFile);
    }


    /**
     * 得到指定md5码所在文件目录
     *
     * @param fileMd5 文件md5码
     * @return String 文件目录
     */
    private String getFileFolderPath(String fileMd5) {
        return mediaServiceProperties.getUploadLocation() + fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/";

    }

    /**
     * 根据指定的md5码和文件扩展名获取文件路径
     *
     * @param fileMd5 文件md5码
     * @param fileExt 文件扩展名
     * @return String 文件路径
     */
    private String getFilePath(String fileMd5, String fileExt) {
        return getFileFolderPath(fileMd5) + fileMd5 + "." + fileExt;
    }

    /**
     * 获取文件相对路径
     *
     * @param fileMd5 文件md5码
     * @param fileExt 文件扩展名
     * @return String 文件相对路径
     */
    private String getFileFolderRelativePath(String fileMd5, String fileExt) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/";
    }

    /**
     * 创建文件存放目录
     *
     * @param fileMd5 文件md5码
     * @return boolean
     */
    private boolean createFileFolder(String fileMd5) {
        String fileFolderPath = getFileFolderPath(fileMd5);
        File file = new File(fileFolderPath);
        if (!file.exists()) {
            // 创建
            return file.mkdirs();
        }
        return true;
    }

    /**
     * 获取存放文件分块目录路径
     *
     * @param fileMd5 文件md5码
     * @return String 分块目录路径
     */
    private String getChunkFileFolderPath(String fileMd5) {
        return getFileFolderPath(fileMd5) + "/chunks/";
    }

    /**
     * 创建存放分块文件的目录
     *
     * @param fileMd5 文件md5码
     * @return boolean
     */
    private boolean createChunkFileFolder(String fileMd5) {
        //创建上传文件目录
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        File chunkFileFolder = new File(chunkFileFolderPath);
        if (!chunkFileFolder.exists()) {
            //创建文件夹
            return chunkFileFolder.mkdirs();
        }
        return true;
    }

    /**
     * 获取分块文件列表
     *
     * @param file 分块文件目录
     * @return List<File> 排好序的分块文件列表
     */
    private List<File> getChunkFiles(File file) {
        // 获取分块文件集合
        File[] files = file.listFiles();
        assert files != null;
        List<File> fileList = Arrays.asList(files);
        return fileList.stream().sorted(Comparator.comparing(f -> Integer.valueOf(f.getName()))).collect(Collectors.toList());
    }

    /**
     * 合并文件
     *
     * @param mergeFile  合并后的文件
     * @param chunkFiles 分块文件列表
     * @return File
     */
    private File mergeFile(File mergeFile, List<File> chunkFiles) {
        try {
            // 写入流
            RandomAccessFile write = new RandomAccessFile(mergeFile, "rw");
            // 开始写入
            byte[] b = new byte[1024];
            for (File chunkFile : chunkFiles) {
                RandomAccessFile read = new RandomAccessFile(chunkFile, "r");
                int len = -1;
                while ((len = read.read(b)) != -1) {
                    // 写入数据
                    write.write(b, 0, len);
                }
                read.close();
            }
            write.close();

            return mergeFile;
        } catch (IOException e) {
            // 记录日志
            log.error("[合并文件分块] 执行文件合并时发生异常, e = ", e);
            return null;
        }
    }

    /**
     * 校验文件md5码
     *
     * @param file    待校验文件
     * @param fileMd5 正确的md5码
     * @return boolean
     */
    private boolean checkMd5(File file, String fileMd5) {
        if (file == null || StringUtils.isBlank(fileMd5)) {
            return false;
        }
        // 校验
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            String md5Hex = DigestUtils.md5Hex(fileInputStream);
            if (fileMd5.equalsIgnoreCase(md5Hex)) {
                return true;
            }
        } catch (IOException e) {
            // 记录日志
            log.error("[合并文件分块] 校验文件md5码发生异常, e = ", e);
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException ignored) {
                }
            }
        }
        return false;
    }


}
