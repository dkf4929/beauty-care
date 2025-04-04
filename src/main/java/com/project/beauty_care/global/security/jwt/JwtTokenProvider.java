package com.project.beauty_care.global.security.jwt;

import com.project.beauty_care.domain.role.dto.RoleResponse;
import com.project.beauty_care.domain.role.service.RoleService;
import com.project.beauty_care.global.enums.Errors;
import com.project.beauty_care.global.exception.NoAuthorityMember;
import com.project.beauty_care.global.exception.TokenExpiredException;
import com.project.beauty_care.global.security.dto.AppUser;
import com.project.beauty_care.global.security.dto.LoginResponse;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class JwtTokenProvider {
    private static final String AUTHORITIES_KEY = "authorities";
    public static final String TOKEN_TYPE = "Bearer";
//    private static final long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String TOKEN_SCHEME = "Bearer ";
    @Value("${token.access}")
    private long accessTokenValidTime;

    @Autowired
    private RoleService roleService;

    private final Key key;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public LoginResponse generateToken(Authentication authentication,
                                       long now) {
        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + accessTokenValidTime);

        // 인증 dto
        AppUser appUser = (AppUser) authentication.getPrincipal();

        // 인증 객체로 부터 권한을 가져온다.
        String authority = appUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElseThrow(() -> new NoAuthorityMember(Errors.NO_AUTHORITY_MEMBER));

        // 유효한 권한인지 확인
        checkAuthority(authority);

        // accessToken 생성
        String accessToken = Jwts.builder()
                .subject(appUser.getLoginId())
                .setClaims(
                        createClaims(appUser.getMemberId(),
                                appUser.getLoginId(),
                                appUser.getName(),
                                authority,
                                appUser.getRole().getUrlPatterns()
                        )
                )
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        return LoginResponse.builder()
                .grantType(TOKEN_TYPE)
                .accessToken(accessToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .build();
    }

    public Authentication getAuthentication(String accessToken) {
        // 토큰 복호화
        Claims claims = parseClaims(accessToken);

        // 권한 없음 -> Ex
        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        RoleResponse role = roleService.findRoleByAuthority(Claim.AUTHORITIES.getClaimValueString(claims));

        // UserDetails 객체를 만들어서 Authentication 리턴
        UserDetails principal = AppUser.builder()
                .memberId(Claim.ID.getClaimValueLong(claims))
                .loginId(Claim.LOGIN_ID.getClaimValueString(claims))
                .name(Claim.NAME.getClaimValueString(claims))
                .role(role)
                .build();

        return new UsernamePasswordAuthenticationToken(
                principal,
                "",
                List.of(new SimpleGrantedAuthority(role.getRoleName())));
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(key).build().parseClaimsJws(token);
            return Boolean.TRUE;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
            throw e;
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
            throw new TokenExpiredException(Errors.TOKEN_EXPIRED);
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
            throw e;
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
            throw e;
        }
    }

    // authorizationHeader 에서, 토큰을 추출한다.
    // "BEARER " -> "BEARER"
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_SCHEME)) {
            return bearerToken.split(" ")[1].trim();
        }
        return null;
    }

    // 토큰 파싱
    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parser().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    // 클레임 생성
    private Map<String, Object> createClaims(Long memberId,
                                             String loginId,
                                             String name,
                                             String authorities,
                                             List<String> authUrlPatterns) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(Claim.ID.getValue(), memberId);
        claims.put(Claim.LOGIN_ID.getValue(), loginId);
        claims.put(Claim.NAME.getValue(), name);
        claims.put(Claim.AUTHORITIES.getValue(), authorities);
        claims.put(Claim.AUTHORITY_PATTERN.getValue(), authUrlPatterns);
        return claims;
    }


    @Getter
    enum Claim {
        ID("member_id", Long.class),
        LOGIN_ID("login_id", String.class),
        NAME("name", String.class),
        AUTHORITIES(AUTHORITIES_KEY, String.class),
        AUTHORITY_PATTERN("authority_pattern", List.class),;

        private String value;
        private Class type;

        Claim(String value, Class type) {
            this.value = value;
            this.type = type;
        }

        private String getClaimValueString(Claims claims) {
            return claims.get(this.getValue(), String.class);
        }
        private Long getClaimValueLong(Claims claims) {
            return claims.get(this.getValue(), Long.class);
        }
        private Boolean getClaimValueBoolean(Claims claims) { return claims.get(this.getValue(), Boolean.class); }

    }

    private void checkAuthority(String authority) {
        roleService.checkAuthority(authority);
    }
}
