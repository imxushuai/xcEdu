package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcCompanyUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface XcCompanyUserRepository extends JpaRepository<XcCompanyUser, String> {

    XcCompanyUser findByUserId(String userId);

}
