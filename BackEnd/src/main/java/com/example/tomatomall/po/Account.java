package com.example.tomatomall.po;

import com.example.tomatomall.enums.RoleEnum;
import com.example.tomatomall.vo.AccountVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Account {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;

    @Basic
    @Column(name = "username")//用户名 UNIQUE
    private String username;

    @Basic
    @Column(name = "password")//密码 NOT NULL 应加密
    private String password;

    @Basic
    @Column(name = "name")//真名 NOT NULL 居然必填？
    private String name;

    @Basic
    @Column(name = "avatar")//头像url
    private String avatar;

    @Basic
    @Column(name = "role")//身份 NOT NULL
    @Enumerated(EnumType.STRING)
    private RoleEnum role;

    @Basic
    @Column(name = "telephone")//手机号 1开头 前端验证
    private String telephone;

    @Basic
    @Column(name = "email")//邮箱 *神秘正则表达式* 前端验证
    private String email;

    @Basic
    @Column(name = "location")//
    private String location;

    public AccountVO toVO() {
        AccountVO accountVO = new AccountVO();
        accountVO.setId(id);
        accountVO.setUsername(username);
        accountVO.setPassword(password);
        accountVO.setName(name);
        accountVO.setAvatar(avatar);
        accountVO.setRole(role);
        accountVO.setTelephone(telephone);
        accountVO.setEmail(email);
        accountVO.setLocation(location);
        return accountVO;
    }


}
