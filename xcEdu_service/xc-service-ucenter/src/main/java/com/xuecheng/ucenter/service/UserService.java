package com.xuecheng.ucenter.service;

import com.xuecheng.framework.domain.ucenter.XcCompanyUser;
import com.xuecheng.framework.domain.ucenter.XcUser;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import com.xuecheng.ucenter.dao.XcCompanyUserRepository;
import com.xuecheng.ucenter.dao.XcUserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private XcUserRepository xcUserRepository;

    @Autowired
    private XcCompanyUserRepository xcCompanyUserRepository;


    /**
     * 按用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    public XcUserExt findByUsername(String username) {
        XcUserExt result = new XcUserExt();

        // 查询用户信息
        XcUser userInfo = xcUserRepository.findByUsername(username);
        if (userInfo == null) {
            return null;
        }

        BeanUtils.copyProperties(userInfo, result);

        // 查询用户公司信息
        XcCompanyUser companyUser = xcCompanyUserRepository.findByUserId(userInfo.getId());
        if (companyUser != null) {
            result.setCompanyId(companyUser.getCompanyId());
        }

        return result;
    }
}
