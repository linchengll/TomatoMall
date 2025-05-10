package com.example.tomatomall.service;

import com.example.tomatomall.vo.AccountVO;

public interface AccountService {

    String register(AccountVO accountVO);
    String login(String username, String password);
    AccountVO getUser(String username);
    AccountVO getById(String id);
    Boolean updateUser(AccountVO accountVO);
}
