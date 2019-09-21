package com.xuecheng.framework.domain.course.response;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CourseBaseResult extends ResponseResult {
    private CourseBase courseBase;
    public CourseBaseResult(ResultCode resultCode, CourseBase courseBase) {
        super(resultCode);
        this.courseBase = courseBase;
    }

    public static CourseBaseResult SUCCESS(CourseBase courseBase) {
        return new CourseBaseResult(CommonCode.SUCCESS, courseBase);
    }

    public static CourseBaseResult ERROR(ResultCode resultCode) {
        return new CourseBaseResult(resultCode, null);
    }

}
