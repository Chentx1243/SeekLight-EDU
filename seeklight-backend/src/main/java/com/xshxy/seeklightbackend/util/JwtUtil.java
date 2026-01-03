package com.xshxy.seeklightbackend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    // 密钥，建议 32+ 字节，生产环境请放配置文件
    private static final String SECRET = "your-very-secret-key-please-change-to-long-one";
    private static final long EXPIRATION = 3600_000; // 1 小时

    /**
     * 生成 JWT Token
     */
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)              // 主体
                .claim("role", role)               // 角色
                .setIssuedAt(new Date())           // 签发时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION)) // 过期时间
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析 JWT
     */
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
