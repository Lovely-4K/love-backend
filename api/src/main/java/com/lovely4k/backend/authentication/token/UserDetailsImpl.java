package com.lovely4k.backend.authentication.token;

import com.lovely4k.backend.common.sessionuser.MemberInfo;
import com.lovely4k.backend.member.Member;
import com.lovely4k.backend.member.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class UserDetailsImpl implements UserDetails, MemberInfo {

    private Member member;

    private UserDetailsImpl() {
    }

    public UserDetailsImpl(Member member) {
        this.member = member;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(Role.USER.getKey());
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(authority);
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return member.getNickname();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Member user() {
        return member;
    }

    @Override
    public String toString() {
        return "UserDetailsImpl{" +
            "member=" + member +
            '}';
    }

    @Override
    public Long getMemberId() {
        return member.getId();
    }

    @Override
    public Long getCoupleId() {
        return member.getCoupleId();
    }

    @Override
    public String getSex() {
        return member.getSex().name();
    }

    @Override
    public String getNickName() {
        return member.getNickname();
    }

    @Override
    public String getPicture() {
        return member.getImageUrl();
    }

}