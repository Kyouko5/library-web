package com.kyouko.libraryweb.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

/**
 * JWT令牌工具类
 * 
 * 负责JWT令牌的生成、解析和验证操作。
 * JWT(JSON Web Token)用于用户身份认证和授权。
 * 
 * 主要功能：
 * - 生成JWT令牌：用户登录成功后生成令牌
 * - 解析JWT令牌：从令牌中提取用户信息和声明
 * - 验证JWT令牌：检查令牌的有效性和是否过期
 * 
 * 令牌结构：
 * - Header: 包含算法类型和令牌类型
 * - Payload: 包含用户信息和过期时间等声明
 * - Signature: 使用密钥对Header和Payload的签名
 * 
 * 安全特性：
 * - 使用HMAC SHA256算法进行签名
 * - 令牌有效期为7天
 * - 密钥用于签名验证，确保令牌未被篡改
 * 
 * @author kyouko
 * @version 1.0
 */
@Component  // 标识为Spring组件，可被依赖注入
public class JwtUtil {

    /**
     * JWT签名密钥
     * 用于生成和验证JWT令牌的签名
     * 
     * 注意：在生产环境中，此密钥应该：
     * 1. 存储在配置文件中，而不是硬编码
     * 2. 使用更强的随机密钥
     * 3. 定期轮换密钥以提高安全性
     */
    private String secret = "sjjakjsdjksjdksajdskajdakjsdjasdjk889827usjjdj";

    /**
     * 从JWT令牌中提取用户名
     * 
     * 用户名存储在JWT的subject字段中。
     * 这是最常用的方法，用于从请求中的令牌获取当前用户身份。
     * 
     * @param token JWT令牌字符串
     * @return 用户名字符串
     * @throws JwtException 当令牌无效或解析失败时抛出异常
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * 从JWT令牌中提取过期时间
     * 
     * 用于检查令牌是否已过期。
     * 
     * @param token JWT令牌字符串
     * @return 令牌过期时间
     * @throws JwtException 当令牌无效或解析失败时抛出异常
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * 生成JWT令牌
     * 
     * 根据用户名生成包含用户信息的JWT令牌。
     * 令牌包含以下信息：
     * - subject: 用户名
     * - issuedAt: 签发时间
     * - expiration: 过期时间（当前时间+7天）
     * 
     * @param username 用户名，将作为令牌的subject
     * @return 生成的JWT令牌字符串
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)  // 设置主题为用户名
                .setIssuedAt(new Date(System.currentTimeMillis()))  // 设置签发时间
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7))  // 设置过期时间为7天
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)  // 使用HMAC SHA256算法签名
                .compact();  // 生成最终的令牌字符串
    }

    /**
     * 验证JWT令牌的有效性
     * 
     * 检查令牌是否有效，包括：
     * 1. 令牌中的用户名是否与提供的用户名匹配
     * 2. 令牌是否已过期
     * 
     * @param token JWT令牌字符串
     * @param username 要验证的用户名
     * @return true表示令牌有效，false表示令牌无效
     */
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        // 验证用户名匹配且令牌未过期
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    /**
     * 从JWT令牌中提取指定的声明(Claim)
     * 
     * 通用方法，可以提取JWT payload中的任何声明。
     * 使用函数式接口来指定要提取的具体声明。
     * 
     * @param token JWT令牌字符串
     * @param claimsResolver 声明解析函数，指定要提取的声明类型
     * @param <T> 声明值的类型
     * @return 提取的声明值
     * @throws JwtException 当令牌无效或解析失败时抛出异常
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 从JWT令牌中提取所有声明
     * 
     * 解析JWT令牌并提取payload中的所有声明。
     * 这是所有其他提取方法的基础。
     * 
     * @param token JWT令牌字符串
     * @return Claims对象，包含所有声明
     * @throws JwtException 当令牌无效、签名错误或解析失败时抛出异常
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())  // 设置验证签名的密钥
                .build()
                .parseClaimsJws(token)  // 解析并验证令牌
                .getBody();  // 获取payload中的声明
    }

    /**
     * 获取JWT签名密钥
     * 
     * 将字符串密钥转换为HMAC算法所需的Key对象。
     * 使用Base64解码确保密钥的正确格式。
     * 
     * @return 用于签名和验证的Key对象
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 检查JWT令牌是否已过期
     * 
     * 比较令牌的过期时间与当前时间。
     * 
     * @param token JWT令牌字符串
     * @return true表示令牌已过期，false表示令牌仍然有效
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

}
