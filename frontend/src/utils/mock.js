// Mock 数据层: 生产部署(公网 GH Pages)下, 前后端不在同一域, 也无公开后端,
// 故全部接口走内存态模拟, 保证 UI 演示与交互完整; 本地 dev 模式不会启用。
// 数据结构严格对齐 CONTRACT.md

// ========== 常量: 预置种子 ==========
const ROLE_LABELS = [
  { value: 'admin', label: '系统管理员' },
  { value: 'librarian', label: '图书管理员' },
  { value: 'reader', label: '读者' }
]

const CATEGORY_SEED = [
  { id: 1, name: '文学', parentId: 0, sort: 1 },
  { id: 2, name: '计算机', parentId: 0, sort: 2 },
  { id: 3, name: '历史', parentId: 0, sort: 3 },
  { id: 4, name: '艺术', parentId: 0, sort: 4 },
  { id: 5, name: '科学', parentId: 0, sort: 5 }
]

const BOOK_TITLES = [
  ['三体', '刘慈欣', 5, '重庆出版社', 2008, 68, 8, 10, 'A-01-01', '一部伟大的科幻小说'],
  ['三体II:黑暗森林', '刘慈欣', 5, '重庆出版社', 2008, 58, 6, 8, 'A-01-02', '宇宙社会学'],
  ['三体III:死神永生', '刘慈欣', 5, '重庆出版社', 2010, 66, 7, 9, 'A-01-03', '回归运动'],
  ['红楼梦', '曹雪芹', 1, '人民文学出版社', 1996, 59, 9, 12, 'B-02-01', '古典名著'],
  ['水浒传', '施耐庵', 1, '人民文学出版社', 1997, 48, 5, 7, 'B-02-02', '梁山好汉'],
  ['西游记', '吴承恩', 1, '人民文学出版社', 1980, 45, 4, 6, 'B-02-03', '师徒四人西天取经'],
  ['三国演义', '罗贯中', 3, '人民文学出版社', 1973, 55, 8, 10, 'C-03-01', '三国纷争'],
  ['明朝那些事儿', '当年明月', 3, '浙江人民出版社', 2009, 199, 5, 8, 'C-03-02', '通俗说明史'],
  ['人类简史', '尤瓦尔·赫拉利', 3, '中信出版社', 2014, 68, 6, 8, 'C-03-03', '认知革命到科学革命'],
  ['深入理解计算机系统', 'Randal E. Bryant', 2, '机械工业出版社', 2016, 139, 4, 6, 'D-04-01', 'CSAPP 经典'],
  ['代码大全', 'Steve McConnell', 2, '电子工业出版社', 2006, 128, 3, 5, 'D-04-02', '软件构造百科'],
  ['Clean Code', 'Robert C. Martin', 2, '人民邮电出版社', 2010, 59, 5, 7, 'D-04-03', '整洁代码之道'],
  ['算法导论', 'Thomas H. Cormen', 2, '机械工业出版社', 2013, 128, 3, 5, 'D-04-04', 'CLRS'],
  ['Vue.js设计与实现', '霍春阳', 2, '人民邮电出版社', 2022, 89, 6, 8, 'D-04-05', 'HcySunYang 著作'],
  ['JavaScript高级程序设计', 'Nicholas C. Zakas', 2, '人民邮电出版社', 2020, 129, 7, 9, 'D-04-06', '红宝书第四版'],
  ['时间简史', '史蒂芬·霍金', 5, '湖南科学技术出版社', 2010, 45, 5, 7, 'E-05-01', '宇宙学入门'],
  ['果壳中的宇宙', '史蒂芬·霍金', 5, '湖南科学技术出版社', 2002, 42, 4, 6, 'E-05-02', '时空续篇'],
  ['上帝掷骰子吗', '曹天元', 5, '北京联合出版公司', 2019, 56, 6, 8, 'E-05-03', '量子物理史话'],
  ['自私的基因', '理查德·道金斯', 5, '中信出版社', 2012, 68, 5, 7, 'E-05-04', '演化生物学经典'],
  ['从一到无穷大', '乔治·伽莫夫', 5, '商务印书馆', 2019, 58, 4, 6, 'E-05-05', '科学科普'],
  ['活着', '余华', 1, '作家出版社', 2012, 39, 7, 9, 'B-02-04', '福贵的一生'],
  ['百年孤独', '加西亚·马尔克斯', 1, '南海出版公司', 2011, 55, 6, 8, 'B-02-05', '魔幻现实主义'],
  ['白夜行', '东野圭吾', 1, '南海出版公司', 2013, 42, 5, 7, 'B-02-06', '推理经典'],
  ['嫌疑人X的献身', '东野圭吾', 1, '南海出版公司', 2008, 35, 4, 6, 'B-02-07', '推理'],
  ['围城', '钱钟书', 1, '人民文学出版社', 1991, 29, 5, 7, 'B-02-08', '知识分子'],
  ['万历十五年', '黄仁宇', 3, '中华书局', 2014, 39, 4, 6, 'C-03-04', '大历史观'],
  ['艺术的故事', '贡布里希', 4, '广西美术出版社', 2015, 280, 3, 5, 'F-06-01', '艺术史经典'],
  ['美的历程', '李泽厚', 4, '生活·读书·新知三联书店', 2009, 45, 4, 6, 'F-06-02', '中国美学'],
  ['西方哲学史', '罗素', 4, '商务印书馆', 1976, 68, 3, 5, 'F-06-03', '哲学通史'],
  ['苏菲的世界', '乔斯坦·贾德', 4, '作家出版社', 2017, 48, 5, 7, 'F-06-04', '哲学启蒙'],
  ['枪炮、病菌与钢铁', '贾雷德·戴蒙德', 3, '上海译文出版社', 2016, 72, 4, 6, 'C-03-05', '人类社会命运'],
  ['万历十五年', '黄仁宇', 3, '三联书店', 1982, 32, 2, 4, 'C-03-06', '版本二(示例)']
]

const READER_NAMES = [
  ['张三', 0, '13900000001', 'zhangsan@demo.com', '110101199001010001', '北京市朝阳区'],
  ['李四', 1, '13900000002', 'lisi@demo.com', '110101199101010002', '上海市浦东新区'],
  ['王五', 0, '13900000003', 'wangwu@demo.com', '110101199201010003', '广州市天河区'],
  ['赵六', 1, '13900000004', 'zhaoliu@demo.com', '110101199301010004', '深圳市南山区'],
  ['钱七', 0, '13900000005', 'qianqi@demo.com', '110101199401010005', '杭州市西湖区'],
  ['孙八', 1, '13900000006', 'sunba@demo.com', '110101199501010006', '成都市武侯区'],
  ['周九', 0, '13900000007', 'zhoujiu@demo.com', '110101199601010007', '武汉市洪山区'],
  ['吴十', 1, '13900000008', 'wushi@demo.com', '110101199701010008', '西安市雁塔区'],
  ['郑十一', 0, '13900000009', 'zheng11@demo.com', '110101199801010009', '南京市鼓楼区'],
  ['王十二', 1, '13900000010', 'wang12@demo.com', '110101199901010010', '苏州市工业园区']
]

const USER_SEED = [
  { id: 1, username: 'admin', password: 'admin123', nickname: '系统管理员', role: 'admin', email: 'admin@library.com', phone: '13800000000', status: 1 },
  { id: 2, username: 'librarian', password: 'lib123', nickname: '图书管理员小李', role: 'librarian', email: 'lib@library.com', phone: '13800000001', status: 1 },
  { id: 3, username: 'reader', password: 'reader123', nickname: '普通读者小明', role: 'reader', email: 'reader@library.com', phone: '13800000002', status: 1 }
]

// ========== 内存态 ==========
const DB = {
  initDone: false,
  users: [],
  categories: [],
  books: [],
  readers: [],
  borrows: [],
  comments: [],
  idSeq: { book: 0, reader: 0, borrow: 0, category: 0, user: 0, comment: 0 }
}

const fmt = (d) => d.toISOString().slice(0, 10)
const now = () => new Date().toISOString()
const daysAgo = (n) => { const d = new Date(); d.setDate(d.getDate() - n); return fmt(d) }
const daysLater = (n) => { const d = new Date(); d.setDate(d.getDate() + n); return fmt(d) }

const BOOK_COVERS = [
  '/covers/1.svg',  '/covers/2.svg',  '/covers/3.svg',  '/covers/4.svg',
  '/covers/5.svg',  '/covers/6.svg',  '/covers/7.svg',  '/covers/8.svg',
  '/covers/9.svg',  '/covers/10.svg', '/covers/11.svg', '/covers/12.svg',
  '/covers/13.svg', '/covers/14.svg', '/covers/15.svg', '/covers/16.svg',
  '/covers/17.svg', '/covers/18.svg', '/covers/19.svg', '/covers/20.svg',
  '/covers/21.svg', '/covers/22.svg', '/covers/23.svg', '/covers/24.svg',
  '/covers/25.svg', '/covers/26.svg', '/covers/27.svg', '/covers/28.svg',
  '/covers/29.svg', '/covers/30.svg', '/covers/31.svg', '/covers/32.svg'
]

function initDB() {
  if (DB.initDone) return
  DB.categories = CATEGORY_SEED.map((c) => ({ ...c, createTime: now() }))
  DB.idSeq.category = 5

  let seq = 0
  DB.books = BOOK_TITLES.map(([title, author, categoryId, publisher, pubYr, price, stock, total, location, desc]) => {
    seq++
    return {
      id: seq,
      title, author, isbn: '978' + String(1000000000000 + seq * 7).slice(-10),
      categoryId, publisher,
      publishDate: `${pubYr}-01-01`,
      price, stock, total, cover: BOOK_COVERS[seq - 1] || '', location,
      description: desc, status: 1,
      createTime: now(), updateTime: now()
    }
  })
  DB.idSeq.book = seq

  seq = 0
  DB.readers = READER_NAMES.map(([name, gender, phone, email, idCard, address]) => {
    seq++
    return {
      id: seq, name, gender, phone, email, idCard, address,
      registerDate: daysAgo(365 - seq * 20),
      status: 1,
      createTime: now()
    }
  })
  DB.idSeq.reader = seq

  DB.users = USER_SEED.map(u => ({ ...u, createTime: now() }))
  DB.idSeq.user = 3

  seq = 0
  const borrowSeed = [
    [1, 1, 18, 5, 1],   // 借出中
    [2, 2, 25, 0, 2],   // 已归还
    [3, 3, 14, -2, 3],  // 逾期
    [4, 4, 30, 0, 1],   // 借出中
    [5, 5, 60, -1, 3],  // 逾期
    [6, 6, 15, 0, 2],   // 已归还
    [7, 7, 20, -3, 3],  // 逾期
    [8, 8, 30, 0, 1],   // 借出中
    [9, 9, 7, 0, 2],    // 已归还
    [10, 10, 14, -5, 3],// 逾期
    [1, 2, 21, 0, 1],   // 借出中
    [2, 3, 28, 0, 1],   // 借出中
    [3, 4, 10, 0, 2],   // 已归还
    [4, 5, 30, 0, 1],   // 借出中
    [5, 6, 45, 0, 2],   // 已归还
    [6, 7, 12, 0, 2],   // 已归还
    [7, 8, 30, 0, 1],   // 借出中
    [8, 9, 21, 0, 2],   // 已归还
    [9, 10, 60, 0, 1],  // 借出中
    [10, 1, 30, 0, 1],  // 借出中
    [11, 2, 14, 0, 1],  // 借出中(第21条,库存扣减过一条)
  ]
  borrowSeed.forEach(([bookId, readerId, days, returnOffset, status]) => {
    seq++
    const borrowDate = daysAgo(Math.max(1, days - (returnOffset < 0 ? -returnOffset : returnOffset)))
    const dDate = new Date(borrowDate); dDate.setDate(dDate.getDate() + days)
    const dueDate = fmt(dDate)
    let returnDate = null
    if (status === 2) returnDate = returnOffset === 0 ? daysAgo(Math.max(0, daysAgo(0) - 0) || 2) : borrowDate
    if (status === 2) {
      // 已归还的, 用 borrowDate + days - 2 作为归还日
      const d = new Date(borrowDate); d.setDate(d.getDate() + Math.max(1, days - 2))
      returnDate = fmt(d)
    }
    // 同步库存: 借出中/逾期 的书 stock--
    if (status !== 2) {
      const b = DB.books.find(x => x.id === bookId)
      if (b) b.stock = Math.max(0, b.stock - 1)
    }
    DB.borrows.push({
      id: seq, bookId, readerId, borrowDate, dueDate, returnDate, status,
      createTime: now(), updateTime: now()
    })
  })
  DB.idSeq.borrow = seq

  // 评论种子: 15 条, 覆盖热门图书, status 含 0/1/2
  seq = 0
  const commentSeed = [
    [1, 3, 5, '三体太精彩了，刘慈欣的想象力令人叹为观止。', 1],
    [1, 2, 5, '黑暗森林法则让我重新审视宇宙，强烈推荐。', 1],
    [2, 3, 4, '第二部比第一部更宏大，罗辑这个角色塑造得很好。', 1],
    [3, 1, 5, '死神永生的结局让人意难平，但确实是神作。', 0],
    [4, 3, 5, '红楼梦是中国古典文学的瑰宝，每次读都有新感悟。', 1],
    [4, 2, 4, '人物刻画细腻，但部分章节略显冗长。', 1],
    [10, 3, 5, 'CSAPP，程序员必读，从硬件到软件讲得通透。', 1],
    [10, 2, 4, '内容很硬核，需要一定基础，但值得啃。', 1],
    [13, 3, 4, '算法领域的权威教材，但有点偏理论。', 1],
    [21, 3, 5, '余华的文字直击人心，福贵的一生让人落泪。', 1],
    [21, 2, 5, '读完久久不能平静，生命的韧性令人敬畏。', 0],
    [22, 3, 4, '魔幻现实主义代表作，人名太长容易混淆。', 1],
    [16, 1, 3, '霍金的科普经典，但部分物理概念仍然难懂。', 2],
    [9, 3, 5, '从认知革命到科学革命，视角宏大，强烈推荐。', 1],
    [8, 2, 5, '把历史写得像小说，一口气读完七本。', 0]
  ]
  commentSeed.forEach(([bookId, userId, rating, content, status]) => {
    seq++
    DB.comments.push({
      id: seq, bookId, userId, rating, content, status,
      createTime: now(), updateTime: now()
    })
  })
  DB.idSeq.comment = seq
  DB.initDone = true
}

// ========== 辅助 ==========
function categoryNameById(id) {
  const c = DB.categories.find(x => x.id === id)
  return c ? c.name : ''
}
function decorateBook(b) {
  return { ...b, categoryName: categoryNameById(b.categoryId) }
}
function decorateBorrow(br) {
  const b = DB.books.find(x => x.id === br.bookId)
  const r = DB.readers.find(x => x.id === br.readerId)
  return { ...br, bookTitle: b ? b.title : '', readerName: r ? r.name : '' }
}
function decorateComment(cm) {
  const b = DB.books.find(x => x.id === cm.bookId)
  const u = DB.users.find(x => x.id === cm.userId)
  return { ...cm, bookTitle: b ? b.title : '', username: u ? u.username : '' }
}
function paginate(arr, page, size) {
  const total = arr.length
  const records = arr.slice((page - 1) * size, page * size)
  return { records, total, page, size }
}
function ok(data = null, message = 'success') {
  return { code: 200, message, data }
}
function fail(code, message) {
  return { code, message, data: null }
}

// ========== 路由处理 ==========
function match(pattern, url) {
  // pattern 形如 GET /book/{id} 或 POST /borrow; url 是 pathname (不含 /api 前缀)
  const [pm, pu] = [pattern.split(' '), url.split('?')[0].split('/')]
  if (pm[0] !== url.split(' ')[0]) return null
  const path = (url.split(' ')[1] || url).split('?')[0]
  const urlParts = path.split('/').filter(Boolean)
  const patParts = pm[1].split('/').filter(Boolean)
  if (patParts.length !== urlParts.length) return null
  const params = {}
  for (let i = 0; i < patParts.length; i++) {
    if (patParts[i].startsWith('{') && patParts[i].endsWith('}')) {
      const key = patParts[i].slice(1, -1)
      params[key] = isNaN(+urlParts[i]) ? urlParts[i] : +urlParts[i]
    } else if (patParts[i] !== urlParts[i]) {
      return null
    }
  }
  return params
}

function qs(url, key, def = '') {
  const i = url.indexOf('?')
  if (i < 0) return def
  const sp = new URLSearchParams(url.slice(i + 1))
  const v = sp.get(key)
  return v === null || v === undefined || v === '' ? def : v
}

// 纯函数: 接收 method+path(不含/api前缀)+body+token, 返回 {status, json}
export function handleMock(method, rawPath, body = null, token = '') {
  initDB()
  const path = rawPath.split('?')[0]
  const url = `${method} ${rawPath}`

  // === 认证 ===
  if (match(`${method} /auth/login`, url) !== null) {
    const u = DB.users.find(x => x.username === body.username && x.password === body.password)
    if (!u) return { status: 200, json: fail(400, '用户名或密码错误') }
    if (u.status !== 1) return { status: 200, json: fail(400, '账号已停用') }
    const token = 'mock-token-' + u.username + '-' + Date.now()
    const userInfo = { id: u.id, username: u.username, nickname: u.nickname, role: u.role, avatar: '', email: u.email, phone: u.phone, status: u.status }
    return { status: 200, json: ok({ token, userInfo }) }
  }
  if (match(`${method} /auth/info`, url) !== null) {
    const username = (token || '').includes('admin') ? 'admin'
      : (token.includes('librarian') ? 'librarian' : (token.includes('reader') ? 'reader' : 'admin'))
    const u = DB.users.find(x => x.username === username) || DB.users[0]
    const userInfo = { id: u.id, username: u.username, nickname: u.nickname, role: u.role, avatar: '', email: u.email, phone: u.phone, status: u.status }
    return { status: 200, json: ok(userInfo) }
  }

  // === 角色列表 ===
  if (match(`${method} /role/list`, url) !== null) return { status: 200, json: ok(ROLE_LABELS) }

  // === 驾驶舱 ===
  if (match(`${method} /dashboard/stats`, url) !== null) {
    const byCat = {}
    DB.books.forEach(b => { byCat[b.categoryId] = (byCat[b.categoryId] || 0) + 1 })
    const categoryDist = DB.categories.map(c => ({ name: c.name, value: byCat[c.id] || 0 }))
    const borrowCountMap = {}
    DB.borrows.forEach(b => { borrowCountMap[b.bookId] = (borrowCountMap[b.bookId] || 0) + 1 })
    const topBooks = Object.entries(borrowCountMap)
      .map(([id, v]) => { const b = DB.books.find(x => x.id === +id); return { name: b ? b.title : '', value: v } })
      .sort((a, b) => b.value - a.value).slice(0, 5)
    const borrowTrend = Array.from({ length: 12 }, (_, i) => ({ month: String(i + 1).padStart(2, '0'), count: Math.floor(Math.random() * 8) + 2 }))
    borrowTrend[7] = { month: '08', count: 10 }
    borrowTrend[5] = { month: '06', count: 8 }
    const readerGrowth = Array.from({ length: 12 }, (_, i) => ({ month: String(i + 1).padStart(2, '0'), count: i < 10 ? 1 : Math.floor(Math.random() * 2) }))
    const kpi = {
      bookCount: DB.books.length,
      readerCount: DB.readers.length,
      borrowingCount: DB.borrows.filter(x => x.status === 1).length,
      overdueCount: DB.borrows.filter(x => x.status === 3).length
    }
    return { status: 200, json: ok({ kpi, borrowTrend, categoryDist, topBooks, readerGrowth }) }
  }

  // === 图书推荐 ===
  if (match(`${method} /recommend/list`, url) !== null) {
    // 热门借阅 TOP 10: 按 borrow 表 book_id 分组 count 倒序
    const hotMap = {}
    DB.borrows.forEach(b => { hotMap[b.bookId] = (hotMap[b.bookId] || 0) + 1 })
    const hot = Object.entries(hotMap)
      .map(([id, cnt]) => {
        const b = DB.books.find(x => x.id === +id)
        if (!b) return null
        return { id: b.id, title: b.title, author: b.author, categoryId: b.categoryId, categoryName: categoryNameById(b.categoryId), cover: b.cover, price: b.price, score: cnt }
      })
      .filter(Boolean)
      .sort((a, b) => b.score - a.score)
      .slice(0, 10)
    // 高分评论 TOP 10: book_comment status=1 按 avg(rating) 倒序
    const ratingMap = {}
    DB.comments.filter(c => c.status === 1).forEach(c => {
      if (!ratingMap[c.bookId]) ratingMap[c.bookId] = { sum: 0, count: 0 }
      ratingMap[c.bookId].sum += c.rating
      ratingMap[c.bookId].count += 1
    })
    const rating = Object.entries(ratingMap)
      .map(([id, v]) => {
        const b = DB.books.find(x => x.id === +id)
        if (!b) return null
        return { id: b.id, title: b.title, author: b.author, categoryId: b.categoryId, categoryName: categoryNameById(b.categoryId), cover: b.cover, price: b.price, score: v.sum / v.count }
      })
      .filter(Boolean)
      .sort((a, b) => b.score - a.score)
      .slice(0, 10)
    return { status: 200, json: ok({ hot, rating }) }
  }

  // === 图书评论 ===
  if (match(`${method} /comment/page`, url) !== null) {
    const page = +qs(rawPath, 'page', 1) || 1
    const size = +qs(rawPath, 'size', 10) || 10
    const kw = qs(rawPath, 'keyword', '').trim()
    const status = qs(rawPath, 'status', '')
    let list = DB.comments.slice().sort((a, b) => b.id - a.id).map(decorateComment)
    if (kw) list = list.filter(c => (c.bookTitle + c.content).includes(kw))
    if (status !== '') list = list.filter(c => String(c.status) === String(status))
    return { status: 200, json: ok(paginate(list, page, size)) }
  }
  if (method === 'POST' && match('POST /comment', url) !== null) {
    DB.idSeq.comment++
    const nc = { id: DB.idSeq.comment, bookId: body.bookId, userId: body.userId || 3, rating: body.rating || 5, content: body.content || '', status: 0, createTime: now(), updateTime: now() }
    DB.comments.push(nc)
    return { status: 200, json: ok(nc) }
  }
  const pCommentStatus = match(`${method} /comment/{id}/status`, url)
  if (pCommentStatus !== null) {
    const c = DB.comments.find(x => x.id === +pCommentStatus.id)
    if (!c) return { status: 200, json: fail(404, '评论不存在') }
    const ns = +qs(rawPath, 'status', 1)
    c.status = ns
    c.updateTime = now()
    return { status: 200, json: ok(decorateComment(c)) }
  }
  const pCommentDelete = match(`DELETE /comment/{id}`, url)
  if (pCommentDelete !== null) {
    const idx = DB.comments.findIndex(x => x.id === +pCommentDelete.id)
    if (idx < 0) return { status: 200, json: fail(404, '评论不存在') }
    DB.comments.splice(idx, 1)
    return { status: 200, json: ok() }
  }

  // === 图书 ===
  if (match(`${method} /book/page`, url) !== null) {
    const page = +qs(rawPath, 'page', 1) || 1
    const size = +qs(rawPath, 'size', 10) || 10
    const kw = qs(rawPath, 'keyword', '').trim()
    const cid = qs(rawPath, 'categoryId', '')
    const status = qs(rawPath, 'status', '')
    let list = DB.books.slice().sort((a, b) => b.id - a.id).map(decorateBook)
    if (kw) list = list.filter(b => (b.title + b.author + b.isbn).includes(kw))
    if (cid) list = list.filter(b => String(b.categoryId) === String(cid))
    if (status !== '') list = list.filter(b => String(b.status) === String(status))
    return { status: 200, json: ok(paginate(list, page, size)) }
  }
  const pBookDetail = match(`${method} /book/{id}`, url)
  if (pBookDetail !== null) {
    const b = DB.books.find(x => x.id === +pBookDetail.id)
    if (!b) return { status: 200, json: fail(404, '图书不存在') }
    const history = DB.borrows.filter(x => x.bookId === b.id).sort((a, b) => b.id - a.id).map(decorateBorrow)
    return { status: 200, json: ok({ ...decorateBook(b), borrowHistory: history }) }
  }
  if (method === 'POST' && match('POST /book', url) !== null) {
    DB.idSeq.book++
    const nb = { id: DB.idSeq.book, status: 1, stock: body.stock || 0, total: body.total || 0, ...body, createTime: now(), updateTime: now() }
    DB.books.push(nb)
    return { status: 200, json: ok(nb) }
  }
  if (method === 'PUT' && match('PUT /book', url) !== null) {
    const b = DB.books.find(x => x.id === +body.id)
    if (!b) return { status: 200, json: fail(404, '图书不存在') }
    Object.assign(b, body, { updateTime: now() })
    return { status: 200, json: ok(b) }
  }
  const pBookDel = match(`${method} /book/{id}`, url)
  if (method === 'DELETE' && pBookDel !== null) {
    DB.books = DB.books.filter(x => x.id !== +pBookDel.id)
    return { status: 200, json: ok(null) }
  }
  const pBookStatus = match(`${method} /book/{id}/status`, url)
  if (method === 'PUT' && pBookStatus !== null) {
    const b = DB.books.find(x => x.id === +pBookStatus.id)
    if (!b) return { status: 200, json: fail(404, '图书不存在') }
    b.status = +qs(rawPath, 'status', 1) || 0
    b.updateTime = now()
    return { status: 200, json: ok(null) }
  }

  // === 分类 ===
  if (match(`${method} /category/tree`, url) !== null) {
    const list = DB.categories.slice().sort((a, b) => a.sort - b.sort)
    return { status: 200, json: ok(list) }
  }
  if (match(`${method} /category/list`, url) !== null) {
    return { status: 200, json: ok(DB.categories.slice().sort((a, b) => a.sort - b.sort)) }
  }
  if (method === 'POST' && match('POST /category', url) !== null) {
    DB.idSeq.category++
    const nc = { id: DB.idSeq.category, parentId: 0, sort: 10, ...body, createTime: now() }
    DB.categories.push(nc)
    return { status: 200, json: ok(nc) }
  }
  if (method === 'PUT' && match('PUT /category', url) !== null) {
    const c = DB.categories.find(x => x.id === +body.id)
    if (!c) return { status: 200, json: fail(404, '分类不存在') }
    Object.assign(c, body); return { status: 200, json: ok(c) }
  }
  const pCatDel = match(`${method} /category/{id}`, url)
  if (method === 'DELETE' && pCatDel !== null) {
    DB.categories = DB.categories.filter(x => x.id !== +pCatDel.id)
    return { status: 200, json: ok(null) }
  }

  // === 读者 ===
  if (match(`${method} /reader/page`, url) !== null) {
    const page = +qs(rawPath, 'page', 1) || 1
    const size = +qs(rawPath, 'size', 10) || 10
    const kw = qs(rawPath, 'keyword', '').trim()
    let list = DB.readers.slice().sort((a, b) => b.id - a.id)
    if (kw) list = list.filter(r => (r.name + r.phone + r.email + r.idCard).includes(kw))
    return { status: 200, json: ok(paginate(list, page, size)) }
  }
  const pReaderDetail = match(`${method} /reader/{id}`, url)
  if (pReaderDetail !== null) {
    const r = DB.readers.find(x => x.id === +pReaderDetail.id)
    if (!r) return { status: 200, json: fail(404, '读者不存在') }
    const borrowHistory = DB.borrows.filter(x => x.readerId === r.id).sort((a, b) => b.id - a.id).map(decorateBorrow)
    return { status: 200, json: ok({ ...r, borrowHistory }) }
  }
  if (method === 'POST' && match('POST /reader', url) !== null) {
    DB.idSeq.reader++
    const nr = { id: DB.idSeq.reader, gender: 0, status: 1, registerDate: fmt(new Date()), ...body, createTime: now() }
    DB.readers.push(nr)
    return { status: 200, json: ok(nr) }
  }
  if (method === 'PUT' && match('PUT /reader', url) !== null) {
    const r = DB.readers.find(x => x.id === +body.id)
    if (!r) return { status: 200, json: fail(404, '读者不存在') }
    Object.assign(r, body); return { status: 200, json: ok(r) }
  }
  const pReaderDel = match(`${method} /reader/{id}`, url)
  if (method === 'DELETE' && pReaderDel !== null) {
    DB.readers = DB.readers.filter(x => x.id !== +pReaderDel.id)
    return { status: 200, json: ok(null) }
  }

  // === 借阅 ===
  if (match(`${method} /borrow/page`, url) !== null) {
    const page = +qs(rawPath, 'page', 1) || 1
    const size = +qs(rawPath, 'size', 10) || 10
    const st = qs(rawPath, 'status', '')
    let list = DB.borrows.slice().sort((a, b) => b.id - a.id).map(decorateBorrow)
    if (st !== '') list = list.filter(x => String(x.status) === String(st))
    return { status: 200, json: ok(paginate(list, page, size)) }
  }
  if (match(`${method} /borrow/overdue`, url) !== null) {
    return { status: 200, json: ok(DB.borrows.filter(x => x.status === 3).slice(-20).reverse().map(decorateBorrow)) }
  }
  if (method === 'POST' && match('POST /borrow', url) !== null) {
    const { bookId, readerId, days } = body
    const b = DB.books.find(x => x.id === +bookId)
    if (!b) return { status: 200, json: fail(400, '图书不存在') }
    if (b.stock <= 0) return { status: 200, json: fail(400, '该书库存不足') }
    if (b.status !== 1) return { status: 200, json: fail(400, '该书已下架') }
    const r = DB.readers.find(x => x.id === +readerId)
    if (!r) return { status: 200, json: fail(400, '读者不存在') }
    DB.idSeq.borrow++
    const bd = daysAgo(0)
    const d = new Date(); d.setDate(d.getDate() + (days || 30)); const dd = fmt(d)
    b.stock--; b.updateTime = now()
    const nb = { id: DB.idSeq.borrow, bookId: +bookId, readerId: +readerId, borrowDate: bd, dueDate: dd, returnDate: null, status: 1, createTime: now(), updateTime: now() }
    DB.borrows.push(nb)
    return { status: 200, json: ok(decorateBorrow(nb)) }
  }
  const pBorrowReturn = match(`${method} /borrow/{id}/return`, url)
  if (method === 'PUT' && pBorrowReturn !== null) {
    const br = DB.borrows.find(x => x.id === +pBorrowReturn.id)
    if (!br) return { status: 200, json: fail(404, '借阅记录不存在') }
    if (br.status === 2) return { status: 200, json: fail(400, '该借阅已归还') }
    br.status = 2; br.returnDate = fmt(new Date()); br.updateTime = now()
    const b = DB.books.find(x => x.id === br.bookId)
    if (b) { b.stock = Math.min(b.total, b.stock + 1); b.updateTime = now() }
    return { status: 200, json: ok(decorateBorrow(br)) }
  }

  // === 统计 ===
  if (match(`${method} /stats/borrow-by-category`, url) !== null) {
    const m = {}
    DB.borrows.forEach(br => {
      const b = DB.books.find(x => x.id === br.bookId)
      if (!b) return
      const c = DB.categories.find(x => x.id === b.categoryId)
      const n = c ? c.name : '其他'
      m[n] = (m[n] || 0) + 1
    })
    const arr = Object.entries(m).map(([name, value]) => ({ name, value }))
    return { status: 200, json: ok(arr) }
  }
  if (match(`${method} /stats/borrow-by-month`, url) !== null) {
    const arr = Array.from({ length: 12 }, (_, i) => ({ month: String(i + 1).padStart(2, '0'), count: Math.floor(Math.random() * 10) + 3 }))
    arr[7] = { month: '08', count: 12 }
    return { status: 200, json: ok(arr) }
  }
  if (match(`${method} /stats/active-readers`, url) !== null) {
    const m = {}
    DB.borrows.forEach(br => { m[br.readerId] = (m[br.readerId] || 0) + 1 })
    const arr = Object.entries(m).map(([rid, v]) => {
      const r = DB.readers.find(x => x.id === +rid)
      return { name: r ? r.name : `读者${rid}`, value: v }
    }).sort((a, b) => b.value - a.value).slice(0, 10)
    return { status: 200, json: ok(arr) }
  }
  if (match(`${method} /stats/inventory-summary`, url) !== null) {
    const totalBooks = DB.books.length
    const totalStock = DB.books.reduce((s, b) => s + b.stock, 0)
    const totalBorrowed = DB.borrows.filter(x => x.status !== 2).length
    const byCatMap = {}
    DB.books.forEach(b => { byCatMap[b.categoryId] = (byCatMap[b.categoryId] || 0) + b.total })
    const byCategory = DB.categories.map(c => ({ name: c.name, value: byCatMap[c.id] || 0 }))
    return { status: 200, json: ok({ totalBooks, totalStock, totalBorrowed, byCategory }) }
  }

  // === 用户管理 ===
  if (match(`${method} /user/page`, url) !== null) {
    const page = +qs(rawPath, 'page', 1) || 1
    const size = +qs(rawPath, 'size', 10) || 10
    const kw = qs(rawPath, 'keyword', '').trim()
    let list = DB.users.slice().sort((a, b) => a.id - b.id).map(u => {
      const { password, ...rest } = u; return rest
    })
    if (kw) list = list.filter(u => (u.username + u.nickname + u.email).includes(kw))
    return { status: 200, json: ok(paginate(list, page, size)) }
  }
  if (method === 'POST' && match('POST /user', url) !== null) {
    DB.idSeq.user++
    const nu = { id: DB.idSeq.user, password: '123456', status: 1, ...body, createTime: now() }
    DB.users.push(nu)
    const { password, ...rest } = nu
    return { status: 200, json: ok(rest) }
  }
  if (method === 'PUT' && match('PUT /user', url) !== null) {
    const u = DB.users.find(x => x.id === +body.id)
    if (!u) return { status: 200, json: fail(404, '用户不存在') }
    Object.assign(u, body)
    const { password, ...rest } = u
    return { status: 200, json: ok(rest) }
  }
  const pUserDel = match(`${method} /user/{id}`, url)
  if (method === 'DELETE' && pUserDel !== null) {
    DB.users = DB.users.filter(x => x.id !== +pUserDel.id)
    return { status: 200, json: ok(null) }
  }
  const pUserStatus = match(`${method} /user/{id}/status`, url)
  if (method === 'PUT' && pUserStatus !== null) {
    const u = DB.users.find(x => x.id === +pUserStatus.id)
    if (!u) return { status: 200, json: fail(404, '用户不存在') }
    u.status = +qs(rawPath, 'status', 1) || 0
    return { status: 200, json: ok(null) }
  }

  return { status: 200, json: fail(404, 'Mock: 未匹配接口 ' + url) }
}
