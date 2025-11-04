package com.kyouko.libraryweb.security;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Spring Security 核心配置。
 *
 * <p>职责：
 * - 定义 {@link SecurityFilterChain}，基于 JWT 的无状态认证；
 * - 配置跨域（CORS），允许前端访问；
 * - 配置授权规则与过滤器顺序。
 *
 * <p>关键点：
 * - 由于使用 JWT，无需服务器端会话，设置为 {@link SessionCreationPolicy#STATELESS}；
 * - 关闭 CSRF（通常用于基于 Cookie 的会话防护）；
 * - 将自定义的 {@link JwtRequestFilter} 插入到 {@link UsernamePasswordAuthenticationFilter} 之前，
 *   确保在用户名密码认证尝试前完成基于 Token 的身份解析与上下文设置。
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Resource
    /**
     * JWT 认证过滤器：解析 Authorization Bearer Token，校验并写入 SecurityContext。
     */
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    /**
     * 定义安全过滤链。
     *
     * <p>配置说明：
     * - 关闭 CSRF：配合前后端分离与 JWT 无状态认证；
     * - 开启并使用自定义 CORS 策略；
     * - 授权规则：放行 /api/auth/**（登录/注册/刷新等），其余请求要求认证；
     * - 会话策略：STATELESS，避免创建或使用服务端会话；
     * - 过滤器顺序：在 UsernamePasswordAuthenticationFilter 之前添加 JwtRequestFilter，
     *   以便在后续安全链处理前完成用户身份设置。
     *
     * @param http HttpSecurity DSL
     * @return 构建完成的 SecurityFilterChain
     * @throws Exception 配置时可能抛出的异常
     */
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 关闭 CSRF：JWT 场景通常不依赖 Cookie 会话，不需要 CSRF 防护
                .csrf(AbstractHttpConfigurer::disable)
                // 开启 CORS 并使用下方声明的 CorsConfigurationSource Bean
                .cors()   // ✅ 开启跨域
                .and()
                .authorizeHttpRequests(auth -> auth
                        // 明确放行认证相关接口（登录/注册/刷新验证码等）
                        .requestMatchers("/api/auth/**").permitAll()
                        // 除上述外，其余请求需已认证用户访问
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        // 使用无状态会话策略：不创建、不使用 Http Session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        // 在用户名密码认证过滤器之前执行 JWT 过滤器，优先完成 Token 解析与上下文填充
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ✅ 定义 CORS 配置
    @Bean
    /**
     * 定义 CORS 策略。
     *
     * <p>默认策略（可按需收敛或配置化）：
     * - 允许来源：前端开发地址（vite 默认端口 5173）；
     * - 允许方法：GET/POST/PUT/DELETE/OPTIONS；
     * - 允许头：任意（生产可按需白名单化）；
     * - 允许凭证：true，便于携带 Cookie/Authorization 等敏感头。
     *
     * @return CORS 配置源
     */
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 前端地址（开发环境）。生产环境建议改为配置文件驱动或使用具体域名
        configuration.setAllowedOrigins(List.of("http://localhost:5173")); // 前端地址
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true); // 允许携带 Cookie / Authorization

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
