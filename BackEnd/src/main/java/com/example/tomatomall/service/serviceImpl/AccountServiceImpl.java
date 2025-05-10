package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Account;
import com.example.tomatomall.repository.AccountRepository;
import com.example.tomatomall.service.AccountService;
import com.example.tomatomall.util.SecurityUtil;
import com.example.tomatomall.util.TokenUtil;
import com.example.tomatomall.vo.AccountVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    SecurityUtil securityUtil;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    private TokenUtil tokenUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public String register(AccountVO accountVO){
        Account account=accountRepository.findByUsername(accountVO.getUsername());
        if(account!=null){
            throw TomatoMallException.usernameAlreadyExists();
        }
        String rawPassword=accountVO.getPassword();
        String encodedPassword=passwordEncoder.encode(rawPassword);
        accountVO.setPassword(encodedPassword);
        Account newAccount=accountVO.toPO();
        accountRepository.save(newAccount);
        return "注册成功";
    }
    @Override
    public String login(String username, String password){
        Account account=accountRepository.findByUsername(username);
        if(account==null){
            throw TomatoMallException.usernameNotExists();
        }
        if(passwordEncoder.matches(password,account.getPassword())){
        return tokenUtil.getToken(account);
        }else{
            throw TomatoMallException.passwordError();
        }
    }
    @Override
    public AccountVO getUser(String username) {
        //路由传参
        Account account=accountRepository.findByUsername(username);
        return account.toVO();
    }

    @Override
    public AccountVO getById(String id) {
        if(accountRepository.findById(new Integer(id)).isPresent()){
        Account account=accountRepository.findById(new Integer(id)).get();
        return account.toVO();
        }else
            throw TomatoMallException.usernameNotExists();
    }

    @Override
    public Boolean updateUser(AccountVO accountVO) {
        //Account account=securityUtil.getCurrentUser();
        //必填用户名？用户名可修改？其他均非必填等于可以更新完全没变化？？？
        Account account=accountRepository.findByUsername(accountVO.getUsername());
        try{
            if (!Objects.equals(account.getId(), securityUtil.getCurrentUser().getId()))
                throw TomatoMallException.userMismatch();
        }catch (NullPointerException n){
            //throw TomatoMallException.notLogin();
            System.err.println("###nullpointerexception###");
        }
        if (accountVO.getPassword()!=null){
            String rawPassword=accountVO.getPassword();
            String encodedPassword=passwordEncoder.encode(rawPassword);
            account.setPassword(encodedPassword);
        }

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
