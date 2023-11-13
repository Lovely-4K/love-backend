package com.lovely4k.backend.authentication;

import com.lovely4k.backend.common.sessionuser.MemberInfo;
import com.lovely4k.backend.member.Member;
import com.lovely4k.backend.member.Sex;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.util.Assert;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

@EqualsAndHashCode
@ToString
@Getter
public class MyOAuth2Member implements OAuth2User, Serializable, MemberInfo {

    @Serial
    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    private final Set<GrantedAuthority> authorities;

    private final String nameAttributeKey;

    private final Long memberId;
    private final Long coupleId;
    private final Sex sex;
    private final String nickName;
    private final String picture;

    /**
     * Constructs a {@code DefaultOAuth2User} using the provided parameters.
     * @param authorities the authorities granted to the user
     * @param member the info about the user
     * @param nameAttributeKey the key used to access the user's &quot;name&quot; from
     * {@link #getAttributes()}
     */
    public MyOAuth2Member(Collection<? extends GrantedAuthority> authorities,
                          String nameAttributeKey, Member member) {
        Assert.notNull(member, "member cannot be empty");
        Assert.hasText(nameAttributeKey, "nameAttributeKey cannot be empty");
        this.memberId = member.getId();
        this.coupleId = member.getCoupleId();
        this.sex = member.getSex();
        this.nickName = member.getNickname();
        this.picture = member.getImageUrl();
        this.authorities = (authorities != null)
            ? Collections.unmodifiableSet(new LinkedHashSet<>(this.sortAuthorities(authorities)))
            : Collections.unmodifiableSet(new LinkedHashSet<>(AuthorityUtils.NO_AUTHORITIES));
        this.nameAttributeKey = nameAttributeKey;
    }

    @Override
    public String getSex() {
        return sex.name();
    }

    @Override
    public String getName() {
        return this.nameAttributeKey;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Collections.emptyMap();
    }

    private Set<GrantedAuthority> sortAuthorities(Collection<? extends GrantedAuthority> authorities) {
        SortedSet<GrantedAuthority> sortedAuthorities = new TreeSet<>(
            Comparator.comparing(GrantedAuthority::getAuthority));
        sortedAuthorities.addAll(authorities);
        return sortedAuthorities;
    }

}