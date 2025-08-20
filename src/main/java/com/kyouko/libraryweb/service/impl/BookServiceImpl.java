package com.kyouko.libraryweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kyouko.libraryweb.domain.Book;
import com.kyouko.libraryweb.dto.BookDto;
import com.kyouko.libraryweb.mapper.BookMapper;
import com.kyouko.libraryweb.service.BookService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 图书服务实现类
 * 
 * 实现BookService接口，提供图书管理的具体业务逻辑。
 * 
 * 主要功能：
 * - 图书信息的CRUD操作（创建、查询、更新、删除）
 * - 图书库存管理（借出时减少库存，归还时增加库存）
 * - 分页查询和条件搜索
 * - 数据传输对象(DTO)与实体对象的转换
 * 
 * 技术特性：
 * - 使用MyBatis Plus进行数据库操作
 * - 支持事务管理，确保数据一致性
 * - 使用乐观锁机制防止库存并发修改问题
 * - 提供灵活的查询条件组合
 * 
 * 并发安全：
 * - 库存操作使用乐观锁，基于版本号进行并发控制
 * - 事务注解保证操作的原子性
 * 
 * @author kyouko
 * @version 1.0
 */
@Service  // 标识为Spring服务层组件
public class BookServiceImpl implements BookService {
    
    /**
     * 图书数据访问层依赖注入
     * 用于执行数据库相关操作
     */
    @Resource
    private BookMapper bookMapper;

    /**
     * 添加新图书
     * 
     * 将前端传入的图书DTO转换为实体对象并保存到数据库。
     * 自动设置创建时间和更新时间。
     * 
     * @param bookDto 图书数据传输对象，包含图书的所有信息
     * 
     * 业务流程：
     * 1. 将DTO转换为实体对象
     * 2. 设置时间戳（创建时间和更新时间）
     * 3. 调用Mapper保存到数据库
     * 
     * 注意事项：
     * - ISBN应该是唯一的，但当前实现未加唯一性校验
     * - 价格和库存数量应该大于0，建议添加参数验证
     */
    @Override
    public void addBook(BookDto bookDto) {
        // 将DTO转换为实体对象
        Book book = convertToBook(bookDto);
        
        // 设置创建和更新时间
        book.setCreatedAt(LocalDateTime.now());
        book.setUpdatedAt(LocalDateTime.now());
        
        // 保存到数据库
        bookMapper.insert(book);
    }

    /**
     * 更新图书信息
     * 
     * 根据图书ID更新现有图书的信息。
     * 只更新提供的字段，自动更新修改时间。
     * 
     * @param id 要更新的图书ID
     * @param bookDto 包含更新信息的图书DTO
     * 
     * 业务流程：
     * 1. 将DTO转换为实体对象
     * 2. 设置图书ID和更新时间
     * 3. 根据ID更新数据库记录
     * 
     * 注意事项：
     * - 不验证图书是否存在，如果ID不存在，更新操作不会报错但也不会生效
     * - 版本号会自动更新，支持乐观锁机制
     */
    @Override
    public void updateBook(Long id, BookDto bookDto) {
        // 转换为实体对象并设置ID
        Book book = convertToBook(bookDto);
        book.setId(id);
        book.setUpdatedAt(LocalDateTime.now());
        
        // 根据ID更新数据库记录
        bookMapper.updateById(book);
    }

    /**
     * 删除单个图书
     * 
     * 根据图书ID删除对应的图书记录。
     * 
     * @param id 要删除的图书ID
     * 
     * 注意事项：
     * - 删除前应该检查是否有相关的借阅记录
     * - 当前实现为物理删除，可考虑改为逻辑删除（软删除）
     * - 如果ID不存在，删除操作不会报错
     */
    @Override
    public void deleteBook(Long id) {
        bookMapper.deleteById(id);
    }

    /**
     * 批量删除图书
     * 
     * 根据图书ID列表批量删除多个图书记录。
     * 适用于前端表格的批量操作。
     * 
     * @param ids 要删除的图书ID列表
     * 
     * 性能优化：
     * - 使用MyBatis Plus的批量删除方法，比循环单个删除效率更高
     * - 在数据库层面执行批量删除，减少网络开销
     */
    @Override
    public void batchDelete(List<Long> ids) {
        bookMapper.deleteBatchIds(ids);
    }

    /**
     * 根据ID查询单个图书
     * 
     * 根据图书ID查询对应的图书详细信息。
     * 
     * @param id 图书ID
     * @return BookDto 图书DTO对象，如果不存在则返回null
     * 
     * 业务流程：
     * 1. 根据ID查询数据库
     * 2. 检查查询结果是否为空
     * 3. 将实体对象转换为DTO并返回
     */
    @Override
    public BookDto getBook(Long id) {
        Book book = bookMapper.selectById(id);
        if (book == null) {
            return null;
        }
        return toBookDto(book);
    }

    /**
     * 分页查询图书列表
     * 
     * 支持按图书名称和ISBN进行模糊搜索的分页查询。
     * 
     * @param pageNum 页码（从1开始），如果为null或小于1则默认为1
     * @param pageSize 每页大小，如果为null或小于1则默认为10
     * @param bookName 图书名称搜索关键词（可选，支持模糊搜索）
     * @param isbn ISBN搜索关键词（可选，支持模糊搜索）
     * @return IPage<BookDto> 分页结果，包含图书列表和分页信息
     * 
     * 查询特性：
     * - 支持多条件组合查询
     * - 按更新时间降序排列，最新更新的图书排在前面
     * - 使用Lambda表达式构建查询条件，类型安全
     * - 自动处理分页参数的边界情况
     * 
     * 性能优化：
     * - 使用MyBatis Plus的分页插件，在数据库层面进行分页
     * - 只查询当前页需要的数据，避免全表查询
     */
    @Override
    public IPage<BookDto> getAllBooks(Integer pageNum, Integer pageSize, String bookName, String isbn) {
        // 参数校验和默认值设置
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        
        // 构建查询条件
        LambdaQueryWrapper<Book> wrappers = Wrappers.lambdaQuery();
        
        // 按图书名称模糊搜索（如果提供了搜索关键词）
        if (StringUtils.hasText(bookName)) {
            wrappers.like(Book::getTitle, bookName);
        }
        
        // 按ISBN模糊搜索（如果提供了搜索关键词）
        if (StringUtils.hasText(isbn)) {
            wrappers.like(Book::getIsbn, isbn);
        }
        
        // 按更新时间降序排列
        wrappers.orderByDesc(Book::getUpdatedAt);
        
        // 执行分页查询
        Page<Book> bookPage = bookMapper.selectPage(new Page<>(pageNum, pageSize), wrappers);
        if (bookPage == null) {
            return null;
        }
        
        // 将实体分页结果转换为DTO分页结果
        return bookPage.convert(this::toBookDto);
    }

    /**
     * 减少图书库存
     * 
     * 用于图书借出时减少库存数量。
     * 使用乐观锁机制防止并发修改导致的库存不一致问题。
     * 
     * @param bookId 图书ID
     * @return boolean 操作是否成功，true表示库存减少成功，false表示失败
     * 
     * 业务规则：
     * - 只有库存大于0时才能减少库存
     * - 使用版本号实现乐观锁，防止并发问题
     * - 操作失败时返回false（如库存不足或并发冲突）
     * 
     * 并发安全：
     * - 使用事务注解保证操作的原子性
     * - 乐观锁机制：基于版本号的并发控制
     * - 如果版本号不匹配（其他事务已修改），操作会失败
     * 
     * 典型场景：
     * - 用户借阅图书时调用此方法
     * - 需要配合借阅记录的创建一起使用
     */
    @Override
    @Transactional  // 事务注解，确保操作的原子性
    public boolean decreaseStock(Long bookId) {
        // 先查询当前图书信息（包括版本号）
        Book book = bookMapper.selectById(bookId);
        
        // 检查图书是否存在且库存大于0
        if (book != null && book.getStock() > 0) {
            // 使用乐观锁减少库存（传入当前版本号）
            int rows = bookMapper.decreaseStock(bookId, book.getVersion());
            // rows > 0 表示更新成功，版本号匹配
            return rows > 0;
        }
        
        // 图书不存在或库存不足
        return false;
    }

    /**
     * 增加图书库存
     * 
     * 用于图书归还时增加库存数量。
     * 同样使用乐观锁机制保证并发安全。
     * 
     * @param bookId 图书ID
     * @return boolean 操作是否成功，true表示库存增加成功，false表示失败
     * 
     * 业务规则：
     * - 图书存在即可增加库存（没有库存上限限制）
     * - 使用版本号实现乐观锁
     * 
     * 典型场景：
     * - 用户归还图书时调用此方法
     * - 需要配合借阅记录状态的更新一起使用
     */
    @Override
    @Transactional  // 事务注解，确保操作的原子性
    public boolean increaseStock(Long bookId) {
        // 先查询当前图书信息（包括版本号）
        Book book = bookMapper.selectById(bookId);
        
        // 检查图书是否存在
        if (book != null) {
            // 使用乐观锁增加库存（传入当前版本号）
            int rows = bookMapper.increaseStock(bookId, book.getVersion());
            // rows > 0 表示更新成功，版本号匹配
            return rows > 0;
        }
        
        // 图书不存在
        return false;
    }

    /**
     * 根据ID集合批量查询图书
     * 
     * 根据图书ID列表批量查询图书信息。
     * 适用于需要获取多个图书详情的场景。
     * 
     * @param idList 图书ID集合
     * @return List<BookDto> 图书DTO列表，如果没有找到任何图书则返回空列表
     * 
     * 典型使用场景：
     * - 查询用户的借阅图书列表
     * - 生成报表时批量获取图书信息
     * - 购物车功能中批量获取商品信息
     * 
     * 性能优化：
     * - 使用MyBatis Plus的批量查询，比循环单个查询效率更高
     * - 使用Stream API进行对象转换，代码简洁且性能较好
     */
    @Override
    public List<BookDto> findAllByIdIn(Collection<Long> idList) {
        // 批量查询图书实体
        List<Book> books = bookMapper.selectBatchIds(idList);
        
        // 检查查询结果是否为空
        if (CollectionUtils.isEmpty(books)) {
            return Collections.emptyList();
        }
        
        // 使用Stream API将实体列表转换为DTO列表
        return books.stream().map(this::toBookDto).collect(Collectors.toList());
    }

    /**
     * 实体对象转DTO对象的私有方法
     * 
     * 将Book实体对象转换为BookDto数据传输对象。
     * 用于在服务层和控制层之间传递数据。
     * 
     * @param book 图书实体对象
     * @return BookDto 图书DTO对象
     * 
     * 转换说明：
     * - 只转换业务相关的字段
     * - 不包含数据库相关的元数据（如创建时间、更新时间、版本号）
     * - 保护内部数据结构，避免直接暴露实体对象
     */
    private BookDto toBookDto(Book book) {
        BookDto bookDto = new BookDto();
        bookDto.setId(book.getId());
        bookDto.setPrice(book.getPrice());
        bookDto.setStock(book.getStock());
        bookDto.setPublishTime(book.getPublishTime());
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setPublisher(book.getPublisher());
        bookDto.setIsbn(book.getIsbn());
        bookDto.setStock(book.getStock());  // 注意：这里重复设置了stock，可能是代码冗余
        return bookDto;
    }

    /**
     * DTO对象转实体对象的私有静态方法
     * 
     * 将BookDto数据传输对象转换为Book实体对象。
     * 用于在保存或更新数据到数据库时的对象转换。
     * 
     * @param bookDto 图书DTO对象
     * @return Book 图书实体对象
     * 
     * 转换说明：
     * - 只转换业务字段，不包含时间戳等系统字段
     * - 时间戳字段由业务方法单独设置
     * - 版本号由MyBatis Plus自动管理
     */
    private static Book convertToBook(BookDto bookDto) {
        Book book = new Book();
        book.setId(bookDto.getId());
        book.setTitle(bookDto.getTitle());
        book.setAuthor(bookDto.getAuthor());
        book.setPublisher(bookDto.getPublisher());
        book.setIsbn(bookDto.getIsbn());
        book.setStock(bookDto.getStock());
        book.setPublishTime(bookDto.getPublishTime());
        book.setPrice(bookDto.getPrice());
        return book;
    }
}
