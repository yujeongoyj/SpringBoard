package com.nc13.springBoard.model;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
public class UserDTO implements UserDetails {
    private int id;
    private String username;
    private String password;
    private String nickname;
    private String role;
    private List<GrantedAuthority> authorities;

    // GrantedAuthority는 스프링시큐리티가 제공하는 직렬로 저장하는 방식의 데이터타입
    @Override
    public List<GrantedAuthority> getAuthorities() {
        authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));
        return authorities;
    }

}
