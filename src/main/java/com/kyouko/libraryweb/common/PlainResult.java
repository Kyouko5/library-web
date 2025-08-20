package com.kyouko.libraryweb.common;

import lombok.Data;

/**
 * 统一响应结果包装类
 * 
 * 用于封装所有API接口的响应结果，提供统一的响应格式。
 * 确保前端能够以一致的方式处理所有API响应。
 * 
 * 响应格式说明：
 * - success: 表示请求是否成功处理
 * - code: HTTP状态码或业务状态码
 * - msg: 响应消息，成功时为"success"，失败时为具体错误信息
 * - data: 响应数据，可以是任何类型的对象
 * 
 * 使用泛型支持不同类型的响应数据。
 * 
 * 典型的响应示例：
 * 成功响应：
 * {
 *   "success": true,
 *   "code": 200,
 *   "msg": "success", 
 *   "data": { ... }
 * }
 * 
 * 失败响应：
 * {
 *   "success": false,
 *   "code": 400,
 *   "msg": "用户名已存在",
 *   "data": null
 * }
 * 
 * @param <T> 响应数据的类型
 * @author kyouko
 * @version 1.0
 */
@Data  // Lombok注解，自动生成getter、setter、equals、hashCode、toString方法
public class PlainResult<T> {
    
    /**
     * 响应数据
     * 可以是任何类型的对象，如用户信息、图书列表、分页数据等
     * 当请求失败时，此字段通常为null
     */
    private T data;
    
    /**
     * 状态码
     * 通常使用HTTP状态码：
     * - 200: 请求成功
     * - 400: 客户端请求错误（如参数验证失败）
     * - 401: 未授权（如JWT令牌无效）
     * - 403: 禁止访问（如权限不足）
     * - 500: 服务器内部错误
     */
    private int code;
    
    /**
     * 响应消息
     * 成功时通常为"success"
     * 失败时为具体的错误描述，便于前端显示给用户
     */
    private String msg;
    
    /**
     * 请求是否成功
     * true表示请求成功处理，false表示请求处理失败
     * 前端可以根据此字段判断是否需要显示错误信息
     */
    private boolean success;

    /**
     * 默认构造函数
     * 创建一个空的响应对象
     */
    public PlainResult() {}

    /**
     * 创建成功响应的静态工厂方法
     * 
     * 用于快速创建表示成功的响应对象。
     * 自动设置success为true，code为200，msg为"success"。
     * 
     * @param data 要返回的响应数据
     * @param <T> 响应数据的类型
     * @return 包含成功状态和数据的PlainResult对象
     * 
     * 使用示例：
     * return PlainResult.success(userList);
     * return PlainResult.success("操作成功");
     */
    public static <T> PlainResult<T> success(T data) {
        PlainResult<T> plainResult = new PlainResult<>();
        plainResult.setSuccess(true);
        plainResult.setData(data);
        plainResult.setCode(200);
        plainResult.setMsg("success");
        return plainResult;
    }

    /**
     * 创建失败响应的静态工厂方法
     * 
     * 用于快速创建表示失败的响应对象。
     * 自动设置success为false，data为null。
     * 
     * @param code 错误状态码
     * @param msg 错误消息描述
     * @param <T> 响应数据的类型（虽然失败时data为null）
     * @return 包含失败状态和错误信息的PlainResult对象
     * 
     * 使用示例：
     * return PlainResult.fail(400, "用户名已存在");
     * return PlainResult.fail(401, "JWT令牌无效");
     * return PlainResult.fail(403, "权限不足");
     */
    public static <T> PlainResult<T> fail(int code, String msg) {
        PlainResult<T> plainResult = new PlainResult<>();
        plainResult.setSuccess(false);
        plainResult.setCode(code);
        plainResult.setMsg(msg);
        return plainResult;
    }

}
