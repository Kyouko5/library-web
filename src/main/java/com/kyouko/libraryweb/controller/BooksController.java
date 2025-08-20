package com.kyouko.libraryweb.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kyouko.libraryweb.common.PlainResult;
import com.kyouko.libraryweb.dto.BookDto;
import com.kyouko.libraryweb.service.BookService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 图书管理控制器
 * 
 * 负责处理图书相关的CRUD操作请求。
 * 提供RESTful API接口供前端进行图书管理。
 * 
 * 主要功能：
 * - 添加新图书：录入图书信息到系统
 * - 更新图书信息：修改已有图书的详细信息
 * - 删除图书：单个删除或批量删除图书
 * - 查询图书：分页查询，支持按标题和ISBN搜索
 * 
 * 权限要求：
 * - 通常需要管理员权限才能进行增删改操作
 * - 查询操作可能对所有登录用户开放
 * 
 * @author kyouko
 * @version 1.0
 */
@RestController  // 标识为REST控制器，自动将返回值序列化为JSON
@RequestMapping("/api")  // 统一的API路径前缀
public class BooksController {

    /**
     * 图书服务层依赖注入
     * 处理图书相关的业务逻辑
     */
    @Resource
    private BookService bookService;

    /**
     * 添加新图书接口
     * 
     * 接收图书信息并添加到系统中。
     * 通常需要管理员权限才能执行此操作。
     * 
     * @param bookDto 图书数据传输对象，包含标题、作者、ISBN、库存等信息
     * @return PlainResult<String> 统一响应格式，成功时返回成功消息
     * 
     * API路径: POST /api/books
     * 请求体示例:
     * {
     *   "title": "Java编程思想",
     *   "author": "Bruce Eckel",
     *   "publisher": "机械工业出版社",
     *   "isbn": "9787111213826",
     *   "stock": 10,
     *   "price": 108.00
     * }
     */
    @PostMapping("/books")
    public PlainResult<String> addBook(@RequestBody BookDto bookDto) {
        // 调用图书服务添加新图书
        bookService.addBook(bookDto);
        return PlainResult.success("success");
    }

    /**
     * 更新图书信息接口
     * 
     * 根据图书ID更新对应图书的详细信息。
     * 支持部分字段更新。
     * 
     * @param id 要更新的图书ID
     * @param bookDto 包含更新信息的图书数据传输对象
     * @return PlainResult<String> 统一响应格式，成功时返回成功消息
     * 
     * API路径: PUT /api/books/{id}
     * 路径示例: PUT /api/books/1
     */
    @PutMapping("/books/{id}")
    public PlainResult<String> updateBook(@PathVariable Long id, @RequestBody BookDto bookDto) {
        // 调用图书服务更新图书信息
        bookService.updateBook(id, bookDto);
        return PlainResult.success("success");
    }

    /**
     * 删除单个图书接口
     * 
     * 根据图书ID删除指定图书。
     * 删除前会检查是否有相关的借阅记录。
     * 
     * @param id 要删除的图书ID
     * @return PlainResult<String> 统一响应格式，成功时返回成功消息
     * 
     * API路径: DELETE /api/books/{id}
     * 路径示例: DELETE /api/books/1
     */
    @DeleteMapping("/books/{id}")
    public PlainResult<String> deleteBook(@PathVariable Long id) {
        // 调用图书服务删除指定图书
        bookService.deleteBook(id);
        return PlainResult.success("success");
    }

    /**
     * 批量删除图书接口
     * 
     * 根据图书ID列表批量删除多个图书。
     * 适用于前端表格的批量删除操作。
     * 
     * @param ids 要删除的图书ID列表
     * @return PlainResult<String> 统一响应格式，成功时返回成功消息
     * 
     * API路径: POST /api/books/deleteBatch
     * 请求体示例: [1, 2, 3, 4, 5]
     */
    @PostMapping("/books/deleteBatch")
    public PlainResult<String> deleteBatch(@RequestBody List<Long> ids) {
        // 调用图书服务批量删除图书
        bookService.batchDelete(ids);
        return PlainResult.success("success");
    }

    /**
     * 分页查询图书接口
     * 
     * 支持分页查询所有图书，可选的搜索条件包括标题和ISBN。
     * 返回分页结果，包含总数、当前页数据等信息。
     * 
     * @param pageNum 页码，从1开始（可选，默认为1）
     * @param pageSize 每页大小（可选，默认为10）
     * @param title 图书标题搜索关键词（可选，支持模糊搜索）
     * @param isbn ISBN搜索关键词（可选，支持精确搜索）
     * @return PlainResult<IPage<BookDto>> 包含分页图书数据的统一响应格式
     * 
     * API路径: GET /api/books
     * 请求示例:
     * - GET /api/books  查询所有图书（使用默认分页）
     * - GET /api/books?pageNum=1&pageSize=20  指定分页参数
     * - GET /api/books?title=Java  按标题搜索
     * - GET /api/books?isbn=9787111213826  按ISBN搜索
     * - GET /api/books?pageNum=1&pageSize=10&title=编程&isbn=978  组合搜索
     * 
     * 响应体示例:
     * {
     *   "code": 200,
     *   "message": "success",
     *   "data": {
     *     "records": [图书列表],
     *     "total": 100,
     *     "current": 1,
     *     "size": 10
     *   }
     * }
     */
    @GetMapping("/books")
    public PlainResult<IPage<BookDto>> getAllBooks(@RequestParam(required = false) Integer pageNum,
                                                   @RequestParam(required = false) Integer pageSize,
                                                   @RequestParam(required = false) String title,
                                                   @RequestParam(required = false) String isbn) {
        // 调用图书服务进行分页查询
        IPage<BookDto> books = bookService.getAllBooks(pageNum, pageSize, title, isbn);
        return PlainResult.success(books);
    }
}
