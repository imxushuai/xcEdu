package com.xuecheng.framework.domain.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "xc_course_media", type = "doc", shards = 1)
public class EsTeachplanMediaPub {

    @Id
    private String courseid;

    @Field
    private String media_fileoriginalname;
    @Field
    private String media_id;
    @Field
    private String media_url;
    @Field
    private String teachplan_id;

}
