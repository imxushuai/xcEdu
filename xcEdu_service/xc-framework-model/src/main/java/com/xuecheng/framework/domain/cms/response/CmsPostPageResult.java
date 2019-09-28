package com.xuecheng.framework.domain.cms.response;

import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CmsPostPageResult extends ResponseResult {
    private String pageUrl;

    public CmsPostPageResult(ResultCode resultCode, String pageUrl) {
        super(resultCode);
        this.pageUrl = pageUrl;
    }
}
