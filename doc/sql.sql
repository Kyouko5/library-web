create database library;
use library;
create table book
(
    id           bigint auto_increment
        primary key,
    title        varchar(255)                       not null,
    author       varchar(255)                       not null,
    publisher    varchar(255)                       null,
    isbn         varchar(20)                        null,
    created_at   datetime default CURRENT_TIMESTAMP null,
    updated_at   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    price        decimal(10, 2)                     null comment '价格',
    publish_time date                               null comment '出版时间',
    stock        int                                null comment ' 库存',
    version      int      default 1                 null comment '版本号',
    constraint uk_isbn
        unique (isbn)
)
    collate = utf8mb4_unicode_ci;

create table borrow_record
(
    id          bigint auto_increment
        primary key,
    user_id     bigint                             not null,
    book_id     bigint                             not null,
    borrow_date datetime default CURRENT_TIMESTAMP null,
    return_date datetime                           null,
    status      varchar(20)                        not null,
    username    varchar(100)                       null comment '用户名',
    isbn        varchar(255)                       null comment ' 图书 isbn 号',
    renew_times int      default 0                 null comment '续借次数',
    created_at  datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updated_at  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
)
    collate = utf8mb4_unicode_ci;

create index book_id
    on borrow_record (book_id);

create index idx_userid_bookid
    on borrow_record (user_id, book_id);

create table user
(
    id         bigint auto_increment
        primary key,
    username   varchar(50)                        not null,
    password   varchar(100)                       not null,
    role       varchar(20)                        not null,
    email      varchar(100)                       null,
    phone      varchar(20)                        null,
    address    varchar(255)                       null,
    created_at datetime default CURRENT_TIMESTAMP null,
    updated_at datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    nick_name  varchar(100)                       null comment '昵称',
    constraint uk_username
        unique (username)
)
    collate = utf8mb4_unicode_ci;


INSERT INTO library.book (id, title, author, publisher, isbn, created_at, updated_at, price, publish_time, stock, version) VALUES (7, '未知的旅程aa', '周九', '冒险出版社', '978-7-16-148419-3', '2024-06-29 04:01:57', '2024-07-05 02:34:46', 12.30, '2024-07-15', 9, 11);
INSERT INTO library.book (id, title, author, publisher, isbn, created_at, updated_at, price, publish_time, stock, version) VALUES (8, '数学的世界', '吴十', '科学出版社', '978-1-59327-503-4', '2024-06-29 04:01:57', '2024-07-05 02:34:46', 59.00, '2024-07-15', 9, 7);
INSERT INTO library.book (id, title, author, publisher, isbn, created_at, updated_at, price, publish_time, stock, version) VALUES (9, '精通 Python', '李十一', '技术书籍出版社', '978-0-262-04119-5', '2024-06-29 04:01:57', '2024-07-06 12:37:27', 12.00, '2024-07-24', 11, 6);
INSERT INTO library.book (id, title, author, publisher, isbn, created_at, updated_at, price, publish_time, stock, version) VALUES (10, '园艺技巧', '王十二', '家居园艺出版社', '978-1-56881-142-3', '2024-06-29 04:01:57', '2024-07-06 12:37:21', 30.00, '2024-07-15', 12, 4);
INSERT INTO library.book (id, title, author, publisher, isbn, created_at, updated_at, price, publish_time, stock, version) VALUES (11, '人工智能的未来', '赵十三', '技术书籍出版社', '978-0-262-03488-1', '2024-06-29 04:01:57', '2024-07-05 02:34:46', 12.00, '2024-07-15', 10, 4);
INSERT INTO library.book (id, title, author, publisher, isbn, created_at, updated_at, price, publish_time, stock, version) VALUES (12, '深海之谜', '孙十四', '自然出版社', '978-0-12-374856-1', '2024-06-29 04:01:57', '2024-07-05 02:34:46', 12.00, '2024-07-15', 10, 0);
INSERT INTO library.book (id, title, author, publisher, isbn, created_at, updated_at, price, publish_time, stock, version) VALUES (13, '现代艺术家', '陈十五', '艺术书籍出版社', '978-0-679-60147-6', '2024-06-29 04:01:57', '2024-07-05 02:34:46', 13.00, '2024-07-15', 1, 1);
INSERT INTO library.book (id, title, author, publisher, isbn, created_at, updated_at, price, publish_time, stock, version) VALUES (14, '初学者瑜伽', '张十六', '健康与保健出版社', '978-0-446-67542-7', '2024-06-29 04:01:57', '2024-07-05 02:34:46', 14.00, '2024-07-15', 1, 1);
INSERT INTO library.book (id, title, author, publisher, isbn, created_at, updated_at, price, publish_time, stock, version) VALUES (15, '远东的故事', '李十七', '文学出版社', '978-1-56881-272-7', '2024-06-29 04:01:57', '2024-07-05 02:34:46', 15.00, '2024-07-15', 5, 1);
INSERT INTO library.book (id, title, author, publisher, isbn, created_at, updated_at, price, publish_time, stock, version) VALUES (16, '量子力学', '周十八', '科学出版社', '978-1-59327-584-3', '2024-06-29 04:01:57', '2024-07-05 02:34:46', 16.00, '2024-07-15', 5, 1);
INSERT INTO library.book (id, title, author, publisher, isbn, created_at, updated_at, price, publish_time, stock, version) VALUES (17, '钓鱼指南', '吴十九', '户外生活出版社', '978-0-262-03388-4', '2024-06-29 04:01:57', '2024-07-05 02:34:46', 15.00, '2024-07-15', 2, 1);
INSERT INTO library.book (id, title, author, publisher, isbn, created_at, updated_at, price, publish_time, stock, version) VALUES (19, '探索宇宙', '赵二一', '科学出版社', '978-0-446-67543-4', '2024-06-29 04:01:57', '2024-07-05 02:34:46', 15.00, '2024-07-15', 6, 1);
INSERT INTO library.book (id, title, author, publisher, isbn, created_at, updated_at, price, publish_time, stock, version) VALUES (20, '孙子兵法', '孙二二', '历史出版社', '978-1-59327-502-7', '2024-06-29 04:01:57', '2024-07-05 02:34:46', 13.00, '2024-07-15', 5, 1);
INSERT INTO library.book (id, title, author, publisher, isbn, created_at, updated_at, price, publish_time, stock, version) VALUES (21, ' 测试图书上架', ' 箫云', '历史出版社', '11112333322', null, '2024-07-05 02:34:55', 12.00, '2024-07-15', 7, 1);
INSERT INTO library.book (id, title, author, publisher, isbn, created_at, updated_at, price, publish_time, stock, version) VALUES (22, '少时诵诗书', '三生三世', '历史出版社', '123232131231', null, '2024-07-05 02:34:55', 20.00, '2024-07-15', 8, 1);
INSERT INTO library.book (id, title, author, publisher, isbn, created_at, updated_at, price, publish_time, stock, version) VALUES (24, ' 测试图书', ' 万行', '人民出版社', 'isbn-test', '2024-07-06 20:21:30', '2024-07-06 12:46:23', 12.00, '2024-07-04', 11, 1);


INSERT INTO library.user (id, username, password, role, email, phone, address, created_at, updated_at, nick_name) VALUES (9, 'admin', '$2a$10$50Acm9OcWu6mONYcTOYbW.QZAd.HVyv1H.b9Gi1CVuKM8U/XLxWP.', '1', '32323@163.com', '18878923024', '浙江省杭州市西湖区呵呵', '2024-07-01 21:26:19', '2024-07-06 20:21:54', '张文是');
INSERT INTO library.user (id, username, password, role, email, phone, address, created_at, updated_at, nick_name) VALUES (10, 'user', '$2a$10$X1ZlFAhDl10JtALa4srLWuqJGPs//IbdHa0Yv/1qGs3epJHtFvw0y', '2', 'ssjjjd@163.com', '18827839837', '太子河', '2024-07-01 21:26:22', '2024-07-02 10:41:00', '李文');


INSERT INTO library.borrow_record (id, user_id, book_id, borrow_date, return_date, status, username, isbn, renew_times, created_at, updated_at) VALUES (15, 10, 7, '2024-07-01 13:19:18', '2024-07-02 13:25:05', '2', 'user', '978-7-16-148419-3', 0, '2024-07-02 05:19:18', '2024-07-02 07:02:55');
INSERT INTO library.borrow_record (id, user_id, book_id, borrow_date, return_date, status, username, isbn, renew_times, created_at, updated_at) VALUES (16, 10, 8, '2024-06-29 13:19:22', '2024-07-02 13:20:12', '2', 'user', '978-1-59327-503-4', 0, '2024-07-02 05:19:22', '2024-07-02 07:02:55');
INSERT INTO library.borrow_record (id, user_id, book_id, borrow_date, return_date, status, username, isbn, renew_times, created_at, updated_at) VALUES (17, 10, 9, '2024-03-02 13:19:24', '2024-07-06 20:37:28', '2', 'user', '978-0-262-04119-5', 0, '2024-07-02 05:19:24', '2024-07-06 20:37:28');
INSERT INTO library.borrow_record (id, user_id, book_id, borrow_date, return_date, status, username, isbn, renew_times, created_at, updated_at) VALUES (18, 10, 10, '2024-03-02 13:38:50', '2024-07-06 20:37:21', '2', 'user', '978-1-56881-142-3', 0, '2024-07-02 13:38:50', '2024-07-06 20:37:21');
INSERT INTO library.borrow_record (id, user_id, book_id, borrow_date, return_date, status, username, isbn, renew_times, created_at, updated_at) VALUES (19, 10, 24, '2024-07-06 20:46:23', null, '1', 'user', 'isbn-test', 0, '2024-07-06 20:46:23', '2024-07-06 20:46:23');
