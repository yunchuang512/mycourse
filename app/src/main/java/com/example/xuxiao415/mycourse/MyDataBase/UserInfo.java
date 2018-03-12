package com.example.xuxiao415.mycourse.MyDataBase;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Unique;

/**
 * Created by Kai on 2016/10/18.
 * 用户登录信息实体类
 */

@Entity
public class UserInfo {
    @Id
    private Long id;
    @Unique
    @Property(nameInDb = "UserName")
    private String UserName;
    @Property(nameInDb = "UserPassword")
    private String UserPassword;
    public String getUserPassword() {
        return this.UserPassword;
    }
    public void setUserPassword(String UserPassword) {
        this.UserPassword = UserPassword;
    }
    public String getUserName() {
        return this.UserName;
    }
    public void setUserName(String UserName) {
        this.UserName = UserName;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    @Generated(hash = 1445144831)
    public UserInfo(Long id, String UserName, String UserPassword) {
        this.id = id;
        this.UserName = UserName;
        this.UserPassword = UserPassword;
    }
    @Generated(hash = 1279772520)
    public UserInfo() {
    }

}
