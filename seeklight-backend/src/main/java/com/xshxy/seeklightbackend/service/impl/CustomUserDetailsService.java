package com.xshxy.seeklightbackend.service.impl;

import com.xshxy.seeklightbackend.constant.SecurityRoles;
import com.xshxy.seeklightbackend.domain.TUser;
import com.xshxy.seeklightbackend.mapper.TUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private TUserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        TUser user = userMapper.selectByAccount(username);

        if (user == null || user.getIsDeleted() == 1 || user.getEnabled() == 0) {
            throw new UsernameNotFoundException("User does not exist or has been disabled");
        }

        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(SecurityRoles.normalize(user.getRole()))
        );

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUserAccount())
                .password(user.getPassword())
                .authorities(authorities)
                .disabled(false)
                .build();
    }
}
