package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.course.Category;
import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.manage_course.dao.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * 查询分类列表
     *
     * @return CategoryNode
     */
    public CategoryNode findCategoryList() {
        CategoryNode sourceCategoryNode = buildCategoryNode(categoryRepository.findByParentid("0").get(0));

        // 查询第二级分类列表
        List<Category> secondaryCategoryList = categoryRepository.findByParentid(sourceCategoryNode.getId());
        List<CategoryNode> secondaryCategoryNodeList = buildCategoryNodeList(secondaryCategoryList);

        sourceCategoryNode.setChildren(secondaryCategoryNodeList);

        return sourceCategoryNode;
    }

    /**
     * 构造分类节点列表
     *
     * @param categoryList
     * @return List<CategoryNode>
     */
    private List<CategoryNode> buildCategoryNodeList(List<Category> categoryList) {
        return categoryList.stream().map(secondaryCategory -> {
            CategoryNode categoryNode = buildCategoryNode(secondaryCategory);
            // 查询子节点
            List<Category> children = categoryRepository.findByParentid(categoryNode.getId());
            List<CategoryNode> categoryNodes = buildCategoryNodeList(children);
            if (!categoryNodes.isEmpty()) {
                categoryNode.setChildren(categoryNodes);
            }

            return categoryNode;
        }).collect(Collectors.toList());
    }


    /**
     * 构造分类节点数据
     *
     * @param category 分类
     * @return CategoryNode
     */
    private CategoryNode buildCategoryNode(Category category) {
        CategoryNode categoryNode = new CategoryNode();
        categoryNode.setId(category.getId());
        categoryNode.setIsleaf(category.getIsleaf());
        categoryNode.setLabel(category.getLabel());
        categoryNode.setName(category.getName());

        return categoryNode;
    }
}
