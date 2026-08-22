package com.library.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据初始化器
 * 幂等灌种子数据: 仅在对应表为空时插入
 *
 * @author library
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("开始执行数据初始化(幂等)...");
        initUsers();
        initCategories();
        initBooks();
        initReaders();
        initBorrows();
        initComments();
        log.info("数据初始化完成");
    }

    /**
     * 图书评论: 约 15 条, 覆盖热门图书, status 含待审/通过/驳回
     */
    private void initComments() {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM book_comment", Long.class);
        if (count != null && count > 0) {
            log.info("book_comment 表已有 {} 条数据, 跳过初始化", count);
            return;
        }
        // {bookId, userId, rating, content, status}
        Object[][] comments = {
                // 三体(24/25/26)
                {24, 3, 5, "中国科幻的巅峰之作，刘慈欣的想象力令人叹为观止。", 1},
                {24, 2, 5, "黑暗森林法则让我重新审视宇宙，强烈推荐。", 1},
                {25, 3, 4, "第二部比第一部更宏大，罗辑这个角色塑造得很好。", 1},
                {26, 1, 5, "死神永生的结局让人意难平，但确实是神作。", 0},
                // 红楼梦(1)
                {1, 3, 5, "中国古典文学的瑰宝，每次读都有新感悟。", 1},
                {1, 2, 4, "人物刻画细腻，但部分章节略显冗长。", 1},
                // 深入理解计算机系统(8)
                {8, 3, 5, "CSAPP，程序员必读，从硬件到软件讲得通透。", 1},
                {8, 2, 4, "内容很硬核，需要一定基础，但值得啃。", 1},
                // 算法导论(9)
                {9, 3, 4, "算法领域的权威教材，但有点偏理论。", 1},
                // 活着(5)
                {5, 3, 5, "余华的文字直击人心，福贵的一生让人落泪。", 1},
                {5, 2, 5, "读完久久不能平静，生命的韧性令人敬畏。", 0},
                // 百年孤独(7)
                {7, 3, 4, "魔幻现实主义代表作，人名太长容易混淆。", 1},
                // 时间简史(25 - 用 25 号 三体II)
                {25, 1, 3, "霍金的科普经典，但部分物理概念仍然难懂。", 2},
                // 人类简史(19)
                {19, 3, 5, "从认知革命到科学革命，视角宏大，强烈推荐。", 1},
                // 明朝那些事儿(18)
                {18, 2, 5, "把历史写得像小说，一口气读完七本。", 0}
        };
        for (Object[] c : comments) {
            jdbcTemplate.update(
                    "INSERT INTO book_comment(book_id, user_id, rating, content, status, create_time, update_time) " +
                            "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
                    c);
        }
        log.info("已初始化 {} 条图书评论", comments.length);
    }

    /**
     * 系统用户: admin/admin123, librarian/lib123, reader/reader123
     */
    private void initUsers() {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM `user`", Long.class);
        if (count != null && count > 0) {
            log.info("user 表已有 {} 条数据, 跳过初始化", count);
            return;
        }
        insertUser("admin", "admin123", "管理员", "admin", "admin@library.com", "13800000000");
        insertUser("librarian", "lib123", "图书管理员", "librarian", "lib@library.com", "13800000001");
        insertUser("reader", "reader123", "读者用户", "reader", "reader@library.com", "13800000002");
        log.info("已初始化 3 个系统用户");
    }

    private void insertUser(String username, String rawPassword, String nickname, String role, String email, String phone) {
        String encoded = passwordEncoder.encode(rawPassword);
        jdbcTemplate.update(
                "INSERT INTO `user`(username, password, nickname, role, avatar, email, phone, status, create_time, update_time) " +
                        "VALUES (?, ?, ?, ?, '', ?, ?, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
                username, encoded, nickname, role, email, phone);
    }

    /**
     * 分类: 文学/计算机/历史/艺术/科学
     */
    private void initCategories() {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM category", Long.class);
        if (count != null && count > 0) {
            log.info("category 表已有 {} 条数据, 跳过初始化", count);
            return;
        }
        String[] names = {"文学", "计算机", "历史", "艺术", "科学"};
        for (int i = 0; i < names.length; i++) {
            jdbcTemplate.update(
                    "INSERT INTO category(name, parent_id, sort, create_time, update_time) VALUES (?, 0, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
                    names[i], i + 1);
        }
        log.info("已初始化 5 个分类");
    }

    /**
     * 图书: 约 30 本, 覆盖三体/红楼梦/深入理解计算机系统等
     */
    private void initBooks() {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM book", Long.class);
        if (count != null && count > 0) {
            log.info("book 表已有 {} 条数据, 跳过初始化", count);
            return;
        }
        // categoryId -> (title, author, publisher, publishDate, price, stock, total, location, description)
        List<Object[]> books = new ArrayList<>();
        // 文学(1)
        books.add(new Object[]{"红楼梦", "曹雪芹", "9787020002207", 1, "人民文学出版社", "1791-01-01", 59.70, 8, 10, "A-01-01", "中国古典四大名著之一"});
        books.add(new Object[]{"西游记", "吴承恩", "9787020002208", 1, "人民文学出版社", "1592-01-01", 49.50, 6, 8, "A-01-02", "中国古典四大名著之一"});
        books.add(new Object[]{"水浒传", "施耐庵", "9787020002209", 1, "人民文学出版社", "1589-01-01", 45.00, 5, 6, "A-01-03", "中国古典四大名著之一"});
        books.add(new Object[]{"三国演义", "罗贯中", "9787020002210", 1, "人民文学出版社", "1522-01-01", 50.00, 7, 9, "A-01-04", "中国古典四大名著之一"});
        books.add(new Object[]{"活着", "余华", "9787506365437", 1, "作家出版社", "1993-01-01", 28.00, 10, 12, "A-01-05", "当代文学经典"});
        books.add(new Object[]{"平凡的世界", "路遥", "9787020042527", 1, "人民文学出版社", "1986-01-01", 79.80, 4, 5, "A-01-06", "中国当代文学经典"});
        books.add(new Object[]{"百年孤独", "加西亚·马尔克斯", "9787544251179", 1, "南海出版公司", "1967-05-30", 39.50, 3, 4, "A-01-07", "魔幻现实主义代表作"});
        // 计算机(2)
        books.add(new Object[]{"深入理解计算机系统", "Randal Bryant", "9787111544937", 2, "机械工业出版社", "2016-11-01", 139.00, 5, 6, "B-02-01", "CSAPP, 计算机系统经典教材"});
        books.add(new Object[]{"算法导论", "Thomas Cormen", "9787111407010", 2, "机械工业出版社", "2012-12-01", 128.00, 4, 5, "B-02-02", "算法领域权威著作"});
        books.add(new Object[]{"代码大全", "Steve McConnell", "9787121028867", 2, "电子工业出版社", "2006-01-01", 98.00, 3, 4, "B-02-03", "软件构建实践指南"});
        books.add(new Object[]{"设计模式", "GoF", "9787111075752", 2, "机械工业出版社", "2000-09-01", 35.00, 2, 3, "B-02-04", "面向对象设计经典"});
        books.add(new Object[]{"Java编程思想", "Bruce Eckel", "9787111213826", 2, "机械工业出版社", "2007-06-01", 108.00, 6, 8, "B-02-05", "Java 进阶必读"});
        books.add(new Object[]{"Effective Java", "Joshua Bloch", "9787111255833", 2, "机械工业出版社", "2009-01-01", 52.00, 5, 7, "B-02-06", "Java 最佳实践"});
        books.add(new Object[]{"重构: 改善既有代码的设计", "Martin Fowler", "9787115168134", 2, "人民邮电出版社", "2010-01-01", 69.00, 4, 5, "B-02-07", "代码重构圣经"});
        books.add(new Object[]{"Clean Code", "Robert Martin", "9787115216878", 2, "人民邮电出版社", "2010-01-01", 59.00, 8, 10, "B-02-08", "代码整洁之道"});
        books.add(new Object[]{"Spring Boot实战", "Craig Walls", "9787115441039", 2, "人民邮电出版社", "2016-09-01", 59.00, 6, 7, "B-02-09", "Spring Boot 入门书"});
        // 历史(3)
        books.add(new Object[]{"史记", "司马迁", "9787101003048", 3, "中华书局", "2013-01-01", 198.00, 3, 4, "C-03-01", "中国第一部纪传体通史"});
        books.add(new Object[]{"资治通鉴", "司马光", "9787101001020", 3, "中华书局", "1084-01-01", 398.00, 2, 3, "C-03-02", "编年体史书"});
        books.add(new Object[]{"明朝那些事儿", "当年明月", "9787550200356", 3, "北京联合出版公司", "2006-09-01", 358.00, 10, 12, "C-03-03", "通俗历史读物"});
        books.add(new Object[]{"人类简史", "尤瓦尔·赫拉利", "9787508647357", 3, "中信出版社", "2014-11-01", 68.00, 9, 10, "C-03-04", "人类发展史"});
        books.add(new Object[]{"万历十五年", "黄仁宇", "9787101055709", 3, "中华书局", "1982-05-01", 28.00, 5, 6, "C-03-05", "大历史观下的明朝断面"});
        // 艺术(4)
        books.add(new Object[]{"艺术的故事", "贡布里希", "9787549550869", 4, "广西美术出版社", "1950-01-01", 280.00, 3, 4, "D-04-01", "艺术史经典入门"});
        books.add(new Object[]{"小顾聊绘画", "顾爷", "9787508645797", 4, "中信出版社", "2014-04-01", 49.80, 6, 8, "D-04-02", "西方绘画轻松入门"});
        books.add(new Object[]{"美的历程", "李泽厚", "9787108018755", 4, "三联书店", "1981-03-01", 36.00, 4, 5, "D-04-03", "中国美学经典"});
        books.add(new Object[]{"设计中的设计", "原研哉", "9787563380432", 4, "广西师范大学出版社", "2006-11-01", 49.00, 5, 6, "D-04-04", "无印良品艺术总监的设计观"});
        // 科学(5)
        books.add(new Object[]{"三体", "刘慈欣", "9787536692930", 5, "重庆出版社", "2008-01-01", 23.00, 12, 15, "E-05-01", "中国科幻里程碑"});
        books.add(new Object[]{"三体II: 黑暗森林", "刘慈欣", "9787536693937", 5, "重庆出版社", "2008-01-01", 32.00, 10, 12, "E-05-02", "三体系列第二部"});
        books.add(new Object[]{"三体III: 死神永生", "刘慈欣", "9787536693999", 5, "重庆出版社", "2010-01-01", 38.00, 8, 10, "E-05-03", "三体系列终章"});
        books.add(new Object[]{"时间简史", "霍金", "9787535732309", 5, "湖南科技出版社", "1992-01-01", 45.00, 7, 9, "E-05-04", "宇宙学科普经典"});
        books.add(new Object[]{"从一到无穷大", "伽莫夫", "9787030053493", 5, "科学出版社", "2002-01-01", 29.00, 4, 5, "E-05-05", "科普经典"});
        books.add(new Object[]{"自私的基因", "理查德·道金斯", "9787508647487", 5, "中信出版社", "1976-01-01", 56.00, 5, 6, "E-05-06", "进化论视角下的科普"});
        books.add(new Object[]{"上帝掷骰子吗", "曹天元", "9787553809200", 5, "北京联合出版公司", "2006-01-01", 49.80, 6, 8, "E-05-07", "量子物理史话"});

        int bookId = 0;
        for (Object[] b : books) {
            bookId++;
            String coverPath = "/covers/" + bookId + ".svg";
            jdbcTemplate.update(
                    "INSERT INTO book(title, author, isbn, category_id, publisher, publish_date, price, stock, total, cover, location, description, status, create_time, update_time) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
                    b[0], b[1], b[2], b[3], b[4], b[5], b[6], b[7], b[8], coverPath, b[9], b[10]);
        }
        log.info("已初始化 {} 本图书(含封面)", books.size());
    }

    /**
     * 读者: 约 10 个
     */
    private void initReaders() {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM reader", Long.class);
        if (count != null && count > 0) {
            log.info("reader 表已有 {} 条数据, 跳过初始化", count);
            return;
        }
        Object[][] readers = {
                {"张三", 0, "13900000001", "zhangsan@example.com", "110101199001011234", "北京市海淀区中关村大街1号", "2024-01-15", 1},
                {"李四", 1, "13900000002", "lisi@example.com", "110102199203032345", "北京市朝阳区建国路88号", "2024-02-20", 1},
                {"王五", 0, "13900000003", "wangwu@example.com", "110103199305053456", "北京市西城区西直门外大街1号", "2024-03-10", 1},
                {"赵六", 1, "13900000004", "zhaoliu@example.com", "110104199407074567", "上海市浦东新区世纪大道100号", "2024-04-05", 1},
                {"钱七", 0, "13900000005", "qianqi@example.com", "310105199508085678", "上海市徐汇区漕宝路200号", "2024-05-12", 1},
                {"孙八", 1, "13900000006", "sunba@example.com", "310106199609096789", "广州市天河区体育西路1号", "2024-06-18", 1},
                {"周九", 0, "13900000007", "zhoujiu@example.com", "440107199710107890", "深圳市南山区科技园南区", "2024-07-22", 1},
                {"吴十", 1, "13900000008", "wushi@example.com", "440108199811118901", "深圳市福田区华强北街道", "2024-08-30", 1},
                {"郑十一", 0, "13900000009", "zhengshiyi@example.com", "510109199912129012", "成都市武侯区人民南路四段", "2024-09-14", 0},
                {"王十二", 1, "13900000010", "wangshier@example.com", "510110200001131023", "杭州市西湖区文三路478号", "2024-10-08", 1}
        };
        for (Object[] r : readers) {
            jdbcTemplate.update(
                    "INSERT INTO reader(name, gender, phone, email, id_card, address, register_date, status, create_time, update_time) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
                    r);
        }
        log.info("已初始化 {} 个读者", readers.length);
    }

    /**
     * 借阅记录: 约 20 条, 含已归还/借出中/逾期各若干
     */
    private void initBorrows() {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM borrow", Long.class);
        if (count != null && count > 0) {
            log.info("borrow 表已有 {} 条数据, 跳过初始化", count);
            return;
        }
        LocalDate today = LocalDate.now();
        // 逾期记录: 借阅日 + 偿还期限都早于今天, return_date 为空
        List<Object[]> overdue = new ArrayList<>();
        overdue.add(borrow(1, 1, today.minusDays(60), today.minusDays(40), null, 3));
        overdue.add(borrow(2, 2, today.minusDays(55), today.minusDays(35), null, 3));
        overdue.add(borrow(3, 3, today.minusDays(48), today.minusDays(28), null, 3));
        overdue.add(borrow(5, 4, today.minusDays(50), today.minusDays(30), null, 3));
        // 已归还
        List<Object[]> returned = new ArrayList<>();
        returned.add(borrow(1, 5, today.minusDays(90), today.minusDays(60), today.minusDays(55), 2));
        returned.add(borrow(2, 6, today.minusDays(85), today.minusDays(55), today.minusDays(50), 2));
        returned.add(borrow(3, 7, today.minusDays(80), today.minusDays(50), today.minusDays(45), 2));
        returned.add(borrow(4, 8, today.minusDays(75), today.minusDays(45), today.minusDays(40), 2));
        returned.add(borrow(8, 9, today.minusDays(70), today.minusDays(40), today.minusDays(35), 2));
        returned.add(borrow(9, 10, today.minusDays(65), today.minusDays(35), today.minusDays(30), 2));
        returned.add(borrow(15, 1, today.minusDays(60), today.minusDays(30), today.minusDays(25), 2));
        returned.add(borrow(16, 2, today.minusDays(58), today.minusDays(28), today.minusDays(23), 2));
        // 借出中
        List<Object[]> borrowing = new ArrayList<>();
        borrowing.add(borrow(11, 3, today.minusDays(10), today.plusDays(20), null, 1));
        borrowing.add(borrow(12, 4, today.minusDays(5), today.plusDays(25), null, 1));
        borrowing.add(borrow(13, 5, today.minusDays(2), today.plusDays(28), null, 1));
        borrowing.add(borrow(17, 6, today.minusDays(1), today.plusDays(29), null, 1));
        borrowing.add(borrow(22, 7, today.minusDays(3), today.plusDays(27), null, 1));
        borrowing.add(borrow(26, 8, today, today.plusDays(30), null, 1));
        borrowing.add(borrow(28, 9, today.minusDays(7), today.plusDays(23), null, 1));
        borrowing.add(borrow(30, 10, today.minusDays(15), today.plusDays(15), null, 1));

        for (Object[] b : overdue) jdbcTemplate.update(insertBorrowSql(), b);
        for (Object[] b : returned) jdbcTemplate.update(insertBorrowSql(), b);
        for (Object[] b : borrowing) jdbcTemplate.update(insertBorrowSql(), b);
        // 对应扣减借出中/逾期图书的库存
        for (Object[] b : overdue) {
            jdbcTemplate.update("UPDATE book SET stock = GREATEST(stock - 1, 0) WHERE id = ?", ((Number) b[0]).longValue());
        }
        for (Object[] b : borrowing) {
            jdbcTemplate.update("UPDATE book SET stock = GREATEST(stock - 1, 0) WHERE id = ?", ((Number) b[0]).longValue());
        }
        log.info("已初始化 {} 条借阅记录(含逾期/已归还/借出中)", overdue.size() + returned.size() + borrowing.size());
    }

    private String insertBorrowSql() {
        return "INSERT INTO borrow(book_id, reader_id, borrow_date, due_date, return_date, status, create_time, update_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
    }

    /**
     * 组装借阅记录数组
     */
    private Object[] borrow(int bookId, int readerId, LocalDate borrowDate, LocalDate dueDate, LocalDate returnDate, int status) {
        return new Object[]{bookId, readerId, borrowDate, dueDate, returnDate, status};
    }
}
