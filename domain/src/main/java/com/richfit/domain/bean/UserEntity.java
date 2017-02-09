package com.richfit.domain.bean;


import java.util.List;

/**
 * 用户信息.
 * data":{"authOrgs":"C271|C273","loginFirst":"","loginId":"admin","menus":"SJ|XJ|PD|TZ","orgId":"","orgName":"","password":"","storageNums":"1E1|1Q2","userId":1,"userName":"1"}}</resultParam>

 * Created by monday on 2016/8/26.
 */

public class UserEntity {
    public String userName;
    public String userId;
    public String password;
    public String loginId;
    public String authOrgs;
    public long lastLoginDate;
    public String companyId;
    public String companyCode;
    public String batchFlag;
    public List<MobileMenuEntity> listMenu;

}
