package com.kyouko.libraryweb.security;

import com.kyouko.libraryweb.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 基于 JWT 的认证过滤器。
 *
 * <p>职责：
 * - 从请求头 {@code Authorization} 中解析 Bearer Token；
 * - 使用 {@link JwtUtil} 提取用户名并校验 Token 的有效性；
 * - 在校验通过且当前上下文未认证时，将认证信息写入 {@link SecurityContextHolder}；
 * - 放行无需认证的接口（如登录/注册）。
 *
 * <p>调用链：本过滤器继承 {@link org.springframework.web.filter.OncePerRequestFilter}，
 * 对每个请求最多执行一次，并在最后继续调用后续过滤器链。
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    /**
     * 用户详情服务：根据用户名加载 {@link UserDetails}。
     */
    private UserDetailsService userDetailsService;

    /**
     * JWT 工具：负责解析用户名、校验 Token 是否有效等。
     */
    private JwtUtil jwtUtil;

    /**
     * 通过构造函数注入依赖。
     *
     * @param userDetailsService 加载用户详情的服务
     * @param jwtUtil            JWT 工具类
     */
    public JwtRequestFilter(UserDetailsService userDetailsService, JwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    /**
     * 过滤逻辑：
     * 1. 放行登录/注册等无需认证的接口；
     * 2. 尝试从请求头中解析 Bearer Token 并提取用户名；
     * 3. 若当前上下文未认证且用户名存在，则校验 Token 并构建认证信息；
     * 4. 始终继续过滤器链。
     *
     * @param request  当前 HTTP 请求
     * @param response 当前 HTTP 响应
     * @param chain    过滤器链
     * @throws ServletException 可能的 Servlet 异常
     * @throws IOException      IO 异常
     */
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // 1) 忽略无需认证的接口（登录/注册相关），直接放行
        if (request.getServletPath().startsWith("/api/auth")) {
            chain.doFilter(request, response);
            return;
        }

        // 2) 从请求头中读取 Authorization，并尝试解析 Bearer Token
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // 约定格式：Authorization: Bearer <JWT> 
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            username = jwtUtil.extractUsername(jwt);
        }

        // 3) 若尚未认证且成功解析出用户名，则加载用户并校验 Token
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {

                // 3.1) 构建认证对象并绑定到安全上下文
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        // 4) 无论是否认证，均继续后续过滤器链
        chain.doFilter(request, response);
    }
}

