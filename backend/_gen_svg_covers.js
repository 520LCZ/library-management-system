const fs = require('fs');
const path = require('path');

const backendDir = 'c:\\Users\\lv227\\Desktop\\前端计划\\library-management-system\\backend';
const coversDir = path.join(backendDir, 'covers');
fs.mkdirSync(coversDir, { recursive: true });

// 顺序严格匹配 DataInitializer 中的书籍顺序（id=1~32）
const books = [
  { id: 1,  title: '红楼梦',           author: '曹雪芹',       tag: '古典',   colors: ['#8B0000', '#DC143C', '#FFD700'] },
  { id: 2,  title: '西游记',           author: '吴承恩',       tag: '古典',   colors: ['#B8860B', '#CD853F', '#FF8C00'] },
  { id: 3,  title: '水浒传',           author: '施耐庵',       tag: '古典',   colors: ['#2C1810', '#5C3317', '#B8860B'] },
  { id: 4,  title: '三国演义',          author: '罗贯中',       tag: '古典',   colors: ['#000080', '#191970', '#C0C0C0'] },
  { id: 5,  title: '活着',             author: '余华',         tag: '文学',   colors: ['#880e4f', '#ad1457', '#f8bbd0'] },
  { id: 6,  title: '平凡的世界',        author: '路遥',         tag: '文学',   colors: ['#e65100', '#ef6c00', '#ffcc80'] },
  { id: 7,  title: '百年孤独',          author: '马尔克斯',     tag: '文学',   colors: ['#4e342e', '#6d4c41', '#d7ccc8'] },
  { id: 8,  title: '深入理解\n计算机系统', author: 'Bryant',     tag: '计算机', colors: ['#1a237e', '#283593', '#64b5f6'] },
  { id: 9,  title: '算法导论',          author: 'Cormen',       tag: '计算机', colors: ['#1b5e20', '#2e7d32', '#81c784'] },
  { id: 10, title: '代码大全',          author: 'McConnell',    tag: '计算机', colors: ['#263238', '#37474f', '#78909c'] },
  { id: 11, title: '设计模式',          author: 'GoF',          tag: '计算机', colors: ['#4527a0', '#5e35b1', '#b39ddb'] },
  { id: 12, title: 'Java编程思想',      author: 'Eckel',        tag: '计算机', colors: ['#e65100', '#f57c00', '#ffcc80'] },
  { id: 13, title: 'Effective Java',    author: 'Bloch',        tag: '计算机', colors: ['#212121', '#424242', '#ffeb3b'] },
  { id: 14, title: '重构',              author: 'Fowler',       tag: '计算机', colors: ['#006064', '#00838f', '#4dd0e1'] },
  { id: 15, title: 'Clean Code',        author: 'Martin',       tag: '计算机', colors: ['#263238', '#424242', '#ffeb3b'] },
  { id: 16, title: 'Spring Boot\n实战', author: 'Walls',        tag: '计算机', colors: ['#1b5e20', '#2e7d32', '#81c784'] },
  { id: 17, title: '史记',              author: '司马迁',       tag: '历史',   colors: ['#3e2723', '#5d4037', '#bcaaa4'] },
  { id: 18, title: '资治通鉴',          author: '司马光',       tag: '历史',   colors: ['#3e2723', '#6d4c41', '#a1887f'] },
  { id: 19, title: '明朝那些事儿',      author: '当年明月',     tag: '历史',   colors: ['#4a1c1c', '#8b4513', '#deb887'] },
  { id: 20, title: '人类简史',          author: '赫拉利',       tag: '历史',   colors: ['#3d2817', '#8b6914', '#d4a574'] },
  { id: 21, title: '万历十五年',        author: '黄仁宇',       tag: '历史',   colors: ['#3e2723', '#6d4c41', '#a1887f'] },
  { id: 22, title: '艺术的故事',        author: '贡布里希',     tag: '艺术',   colors: ['#bf360c', '#d84315', '#ffab91'] },
  { id: 23, title: '小顾聊绘画',        author: '顾爷',         tag: '艺术',   colors: ['#4527a0', '#5e35b1', '#b39ddb'] },
  { id: 24, title: '美的历程',          author: '李泽厚',       tag: '艺术',   colors: ['#4527a0', '#5e35b1', '#d1c4e9'] },
  { id: 25, title: '设计中的设计',      author: '原研哉',       tag: '艺术',   colors: ['#eceff1', '#90a4ae', '#37474f'] },
  { id: 26, title: '三体',              author: '刘慈欣',       tag: '科幻',   colors: ['#0a0e27', '#1a237e', '#ff6f00'] },
  { id: 27, title: '三体II\n黑暗森林',  author: '刘慈欣',       tag: '科幻',   colors: ['#0d1b2a', '#1b263b', '#d4af37'] },
  { id: 28, title: '三体III\n死神永生', author: '刘慈欣',       tag: '科幻',   colors: ['#1a0033', '#3d0066', '#ff0066'] },
  { id: 29, title: '时间简史',          author: '霍金',         tag: '科学',   colors: ['#0d47a1', '#1565c0', '#64b5f6'] },
  { id: 30, title: '从一到无穷大',      author: '伽莫夫',       tag: '科学',   colors: ['#006064', '#00838f', '#4dd0e1'] },
  { id: 31, title: '自私的基因',        author: '道金斯',       tag: '科学',   colors: ['#1b5e20', '#388e3c', '#a5d6a7'] },
  { id: 32, title: '上帝掷骰子吗',      author: '曹天元',       tag: '科学',   colors: ['#4a148c', '#6a1b9a', '#ce93d8'] }
];

function generateSVG(b) {
  const [c1, c2, c3] = b.colors;
  const lines = b.title.split('\n');
  const titleFontSize = lines.length > 1 ? 36 : 46;

  let titleText = '';
  lines.forEach((line, i) => {
    const y = 180 + i * 60;
    titleText += `<text x="50" y="${y}" font-family="'Noto Serif SC', 'SimSun', serif" font-size="${titleFontSize}" font-weight="bold" fill="#ffffff" opacity="0.95">${line}</text>`;
  });

  const authorY = 180 + lines.length * 60 + 25;

  return `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 420" width="300" height="420">
  <defs>
    <linearGradient id="bg${b.id}" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" style="stop-color:${c1};stop-opacity:1" />
      <stop offset="50%" style="stop-color:${c2};stop-opacity:1" />
      <stop offset="100%" style="stop-color:${c1};stop-opacity:1" />
    </linearGradient>
    <radialGradient id="glow${b.id}" cx="50%" cy="30%" r="70%">
      <stop offset="0%" style="stop-color:${c3};stop-opacity:0.3" />
      <stop offset="100%" style="stop-color:${c3};stop-opacity:0" />
    </radialGradient>
    <filter id="shadow${b.id}" x="-20%" y="-20%" width="140%" height="140%">
      <feDropShadow dx="2" dy="4" stdDeviation="6" flood-opacity="0.4"/>
    </filter>
  </defs>

  <rect width="300" height="420" fill="url(#bg${b.id})"/>
  <rect width="300" height="420" fill="url(#glow${b.id})"/>

  <rect x="15" y="15" width="270" height="390" fill="none" stroke="${c3}" stroke-width="1" opacity="0.4"/>
  <rect x="22" y="22" width="256" height="376" fill="none" stroke="${c3}" stroke-width="0.5" opacity="0.25"/>

  <rect x="40" y="60" width="220" height="3" fill="${c3}" opacity="0.7"/>
  <rect x="40" y="68" width="150" height="1" fill="${c3}" opacity="0.4"/>

  <rect x="40" y="82" width="56" height="22" fill="${c3}" opacity="0.9"/>
  <text x="68" y="98" font-family="'PingFang SC', sans-serif" font-size="12" fill="${c1}" text-anchor="middle" font-weight="bold">${b.tag}</text>

  <g filter="url(#shadow${b.id})">
    ${titleText}
  </g>

  <rect x="50" y="${180 + lines.length * 60 + 12}" width="200" height="1" fill="${c3}" opacity="0.5"/>
  <rect x="50" y="${180 + lines.length * 60 + 16}" width="120" height="1" fill="${c3}" opacity="0.3"/>

  <text x="50" y="${authorY}" font-family="'PingFang SC', sans-serif" font-size="18" fill="${c3}" opacity="0.85">${b.author}</text>

  <rect x="40" y="370" width="220" height="2" fill="${c3}" opacity="0.6"/>
  <circle cx="150" cy="385" r="4" fill="${c3}" opacity="0.8"/>
  <circle cx="140" cy="385" r="2" fill="${c3}" opacity="0.5"/>
  <circle cx="160" cy="385" r="2" fill="${c3}" opacity="0.5"/>

  <path d="M40 40 L60 40 L40 60 Z" fill="${c3}" opacity="0.5"/>
  <path d="M260 40 L240 40 L260 60 Z" fill="${c3}" opacity="0.5"/>
  <path d="M40 380 L60 380 L40 360 Z" fill="${c3}" opacity="0.5"/>
  <path d="M260 380 L240 380 L260 360 Z" fill="${c3}" opacity="0.5"/>
</svg>`;
}

let sqlLines = ['-- 批量更新图书封面路径', 'USE library_db;', ''];
let ok = 0;
books.forEach(b => {
  const svg = generateSVG(b);
  const svgPath = path.join(coversDir, `${b.id}.svg`);
  fs.writeFileSync(svgPath, svg, 'utf-8');
  sqlLines.push(`UPDATE book SET cover = '/covers/${b.id}.svg' WHERE id = ${b.id};`);
  ok++;
  process.stdout.write('.');
});

sqlLines.push('');
sqlLines.push('-- 完成: ' + ok + ' 本图书封面已更新');

const sqlPath = path.join(backendDir, 'update_covers.sql');
fs.writeFileSync(sqlPath, sqlLines.join('\n'), 'utf-8');

console.log('');
console.log('完成: ' + ok + ' 本图书封面 SVG 已生成');
console.log('SQL 脚本: ' + sqlPath);
