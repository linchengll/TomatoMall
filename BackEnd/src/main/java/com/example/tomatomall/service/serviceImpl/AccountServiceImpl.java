package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.po.Account;
import com.example.tomatomall.repository.AccountRepository;
import com.example.tomatomall.service.AccountService;
import com.example.tomatomall.util.SecurityUtil;
import com.example.tomatomall.vo.AccountVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    SecurityUtil securityUtil;
    @Autowired
    AccountRepository accountRepository;

    @Override
    public AccountVO getUser(String username) {
        //Account account=securityUtil.getCurrentUser();
        //路由传参
        Account account=accountRepository.findByUsername(username);
        return account.toVO();
    }

    @Override
    public Boolean updateUser(AccountVO accountVO) {
        //Account account=securityUtil.getCurrentUser();
        //必填用户名？用户名可修改？其他均非必填等于可以更新完全没变化？？？
        Account account=accountRepository.findByUsername(accountVO.getUsername());

        if (accountVO.getPassword()!=null)
            account.setPassword(accountVO.getPassword());//这里也设置了密码，需要加密

        if (accountVO.getName()!=null)
            account.setName(accountVO.getName());
        if (accountVO.getAvatar()!=null)
            account.setAvatar(accountVO.getAvatar());
        if (accountVO.getTelephone()!=null)
            account.setTelephone(accountVO.getTelephone());
        if (accountVO.getEmail()!=null)
            account.setEmail(accountVO.getEmail());
        if (accountVO.getLocation()!=null)
            account.setLocation(accountVO.getLocation());
        accountRepository.save(account);
        return true;
    }
}
