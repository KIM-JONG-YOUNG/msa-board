package com.jong.msa.board.platform.gateway.filter;

import com.jong.msa.board.common.constants.HeaderNames;
import com.jong.msa.board.common.enums.Group;
import com.jong.msa.board.platform.gateway.exception.GatewayServiceException;
import com.jong.msa.board.platform.gateway.filter.TokenAuthFilterFactory.Config;
import com.jong.msa.board.platform.gateway.service.TokenManageService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenAuthFilterFactory extends AbstractGatewayFilterFactory<Config> {

    public static final Config IS_AUTH = new Config(List.of(Group.ADMIN, Group.USER));
    public static final Config IS_ADMIN = new Config(List.of(Group.ADMIN));
    public static final Config IS_USER = new Config(List.of(Group.USER));

    private final TokenManageService tokenManageService;

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String accessToken = exchange.getRequest().getHeaders().getFirst(HeaderNames.ACCESS_TOKEN);
            return tokenManageService.extractAccessTokenDetail(accessToken)
                .flatMap(tokenDetail -> {
                    if (!config.group().contains(tokenDetail.group())) {
                        return Mono.error(GatewayServiceException.notAccessibleUrl());
                    }
                    return chain.filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder
                            .withAuthentication(UsernamePasswordAuthenticationToken
                                .authenticated(tokenDetail.id(), null, List
                                    .of(new SimpleGrantedAuthority("ROLE_" + tokenDetail.group())))));
                });
        };
    }

    public record Config(List<Group> group) {}

}
