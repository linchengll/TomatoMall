package com.example.tomatomall.service;

import com.example.tomatomall.vo.AccountVO;

public interface AccountService {



    AccountVO getUser(String username);

    Boolean updateUser(AccountVO accountVO);
}
