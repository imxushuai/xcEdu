package com.xuecheng.manage_media.controller;

import com.xuecheng.api.media.MediaFileControllerApi;
import com.xuecheng.framework.domain.media.request.QueryMediaFileRequest;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_media.service.MediaFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("media/file")
public class MediaFileController extends BaseController implements MediaFileControllerApi {


    @Autowired
    private MediaFileService mediaFileService;

    @Override
    @GetMapping("list/{page}/{size}")
    public QueryResponseResult findList(@PathVariable int page,
                                        @PathVariable int size, QueryMediaFileRequest queryMediaFileRequest) {
        return mediaFileService.findList(page, size, queryMediaFileRequest);
    }

    @Override
    @DeleteMapping("{id}")
    public ResponseResult delete(@PathVariable String id) {
        mediaFileService.delete(id);
        return new ResponseResult(CommonCode.SUCCESS);
    }
}
