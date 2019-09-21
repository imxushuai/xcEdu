package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.Category;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CategoryRepository extends CrudRepository<Category, String> {

    List<Category> findByParentid(String parentId);
}
