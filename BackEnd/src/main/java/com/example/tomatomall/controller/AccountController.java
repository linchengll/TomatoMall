package com.example.tomatomall.controller;

import com.example.tomatomall.po.Account;
import com.example.tomatomall.service.AccountService;
import com.example.tomatomall.vo.AccountVO;
import com.example.tomatomall.vo.Response;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Resource
    AccountService accountService;

    /**
     * 获取用户详情
     */
    @GetMapping("/{username}")
    public Response<AccountVO> getUser(@PathVariable String username) {
        return Response.buildSuccess(accountService.getUser(username));
    }

    /**
     * 创建新的用户
     */
    @PostMapping
    public Response<String> createUser(@RequestBody AccountVO accountVO) {
        return Response.buildSuccess(accountService.register(accountVO));
    }

    /**
     * 更新用户信息
     */
    @PutMapping
    public Response<Boolean> updateUser(@RequestBody AccountVO accountVO) {
        // 输出接收到的 accountVO 数据，检查是否为空或者包含异常值
        System.out.println("Received AccountVO: " + accountVO);
        System.out.println("Username: " + accountVO.getUsername());
        System.out.println("Email: " + accountVO.getEmail());
        // 继续打印其他字段...

        // 如果有必要，可以尝试捕获并打印异常
        try {
            accountService.updateUser(accountVO);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.buildError("Update failed due to error: " + e.getMessage());
        }

        return Response.buildSuccess(true);
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public Response<String> login(@RequestBody AccountVO accountVO) {
        return Response.buildSuccess(accountService.login(accountVO.getUsername(), accountVO.getPassword()));
    }
    //惨痛教训：默认路由不用写，接受数据结构应当匹配
}