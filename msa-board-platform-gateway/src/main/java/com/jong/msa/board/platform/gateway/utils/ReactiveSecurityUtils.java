package com.jong.msa.board.platform.gateway.utils;

import com.jong.msa.board.common.enums.Group;
import java.util.List;
import java.util.UUID;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import reactor.util.context.Context;

public final class ReactiveSecurityUtils {

    public static Context withAuthentication(UUID id, Group group) {
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + group);
        Authentication authentication = new UsernamePasswordAuthenticationToken(id, null, List.of(authority));
        return ReactiveSecurityContextHolder.withAuthentication(authentication);
    }

}
