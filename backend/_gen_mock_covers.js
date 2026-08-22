const fs = require('fs');
const path = require('path');

const backendDir = 'c:\\Users\\lv227\\Desktop\\前端计划\\library-management-system\\backend';
const frontendDir = 'c:\\Users\\lv227\\Desktop\\前端计划\\library-management-system\\frontend';

const coversDir = path.join(backendDir, 'covers');
const publicCoversDir = path.join(frontendDir, 'public', 'covers');
fs.mkdirSync(publicCoversDir, { recursive: true });

// mock.js BOOK_TITLES 顺序对应的封面配色
const mockCovers = [
  { title: '三体',            tag: '科幻',   colors: ['#0a0e27', '#1a237e', '#ff6f00'] },
  { title: '三体II\n黑暗森林', tag: '科幻',   colors: ['#0d1b2a', '#1b263b', '#d4af37'] },
  { title: '三体III\n死神永生', tag: '科幻',   colors: ['#1a0033', '#3d0066', '#ff0066'] },
  { title: '红楼梦',          tag: '古典',   colors: ['#8B0000', '#DC143C', '#FFD700'] },
  { title: '水浒传',          tag: '古典',   colors: ['#2C1810', '#5C3317', '#B8860B'] },
  { title: '西游记',          tag: '古典',   colors: ['#B8860B', '#CD853F', '#FF8C00'] },
  { title: '三国演义',         tag: '古典',   colors: ['#000080', '#191970', '#C0C0C0'] },
  { title: '明朝那些事儿',     tag: '历史',   colors: ['#4a1c1c', '#8b4513', '#deb887'] },
  { title: '人类简史',         tag: '历史',   colors: ['#3d2817', '#8b6914', '#d4a574'] },
  { title: '深入理解\n计算机系统', tag: '计算机', colors: ['#1a237e', '#283593', '#64b5f6'] },
  { title: '代码大全',         tag: '计算机', colors: ['#263238', '#37474f', '#78909c'] },
  { title: 'Clean Code',      tag: '计算机', colors: ['#263238', '#424242', '#ffeb3b'] },
  { title: '算法导论',         tag: '计算机', colors: ['#1b5e20', '#2e7d32', '#81c784'] },
  { title: 'Vue.js\n设计与实现', tag: '计算机', colors: ['#4fc08d', '#42b883', '#35495e'] },
  { title: 'JavaScript\n高级程序设计', tag: '计算机', colors: ['#f7df1e', '#ffd600', '#323330'] },
  { title: '时间简史',         tag: '科学',   colors: ['#0d47a1', '#1565c0', '#64b5f6'] },
  { title: '果壳中的宇宙',     tag: '科学',   colors: ['#1a237e', '#283593', '#90caf9'] },
  { title: '上帝掷骰子吗',     tag: '科学',   colors: ['#4a148c', '#6a1b9a', '#ce93d8'] },
  { title: '自私的基因',       tag: '科学',   colors: ['#1b5e20', '#388e3c', '#a5d6a7'] },
  { title: '从一到无穷大',     tag: '科学',   colors: ['#006064', '#00838f', '#4dd0e1'] },
  { title: '活着',            tag: '文学',   colors: ['#880e4f', '#ad1457', '#f8bbd0'] },
  { title: '百年孤独',         tag: '文学',   colors: ['#4e342e', '#6d4c41', '#d7ccc8'] },
  { title: '白夜行',          tag: '文学',   colors: ['#263238', '#455a64', '#b0bec5'] },
  { title: '嫌疑人X\n的献身',   tag: '文学',   colors: ['#3e2723', '#5d4037', '#bcaaa4'] },
  { title: '围城',            tag: '文学',   colors: ['#3e2723', '#5d4037', '#d7b89c'] },
  { title: '万历十五年',       tag: '历史',   colors: ['#3e2723', '#6d4c41', '#a1887f'] },
  { title: '艺术的故事',       tag: '艺术',   colors: ['#bf360c', '#d84315', '#ffab91'] },
  { title: '美的历程',         tag: '艺术',   colors: ['#4527a0', '#5e35b1', '#d1c4e9'] },
  { title: '西方哲学史',       tag: '艺术',   colors: ['#3e2723', '#6d4c41', '#bcaaa4'] },
  { title: '苏菲的世界',       tag: '艺术',   colors: ['#004d40', '#00695c', '#80cbc4'] },
  { title: '枪炮、病菌\n与钢铁', tag: '历史', colors: ['#33691e', '#558b2f', '#c5e1a5'] },
  { title: '万历十五年\n(版本二)', tag: '历史', colors: ['#3e2723', '#6d4c41', '#a1887f'] }
];

function generateSVG(b, idx) {
  const id = idx + 1;
  const [c1, c2, c3] = b.colors;
  const lines = b.title.split('\n');
  const titleFontSize = lines.length > 1 ? 34 : 44;

  let titleText = '';
  lines.forEach((line, i) => {
    const y = 175 + i * 58;
    titleText += `<text x="50" y="${y}" font-family="'Noto Serif SC', 'SimSun', serif" font-size="${titleFontSize}" font-weight="bold" fill="#ffffff" opacity="0.95">${line}</text>`;
  });

  return `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 420" width="300" height="420">
  <defs>
    <linearGradient id="bg${id}" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" style="stop-color:${c1};stop-opacity:1" />
      <stop offset="50%" style="stop-color:${c2};stop-opacity:1" />
      <stop offset="100%" style="stop-color:${c1};stop-opacity:1" />
    </linearGradient>
    <radialGradient id="glow${id}" cx="50%" cy="30%" r="70%">
      <stop offset="0%" style="stop-color:${c3};stop-opacity:0.3" />
      <stop offset="100%" style="stop-color:${c3};stop-opacity:0" />
    </radialGradient>
    <filter id="shadow${id}" x="-20%" y="-20%" width="140%" height="140%">
      <feDropShadow dx="2" dy="4" stdDeviation="6" flood-opacity="0.4"/>
    </filter>
  </defs>
  <rect width="300" height="420" fill="url(#bg${id})"/>
  <rect width="300" height="420" fill="url(#glow${id})"/>
  <rect x="15" y="15" width="270" height="390" fill="none" stroke="${c3}" stroke-width="1" opacity="0.4"/>
  <rect x="40" y="60" width="220" height="3" fill="${c3}" opacity="0.7"/>
  <rect x="40" y="82" width="56" height="22" fill="${c3}" opacity="0.9"/>
  <text x="68" y="98" font-family="'PingFang SC', sans-serif" font-size="12" fill="${c1}" text-anchor="middle" font-weight="bold">${b.tag}</text>
  <g filter="url(#shadow${id})">
    ${titleText}
  </g>
  <rect x="50" y="${175 + lines.length * 58 + 12}" width="200" height="1" fill="${c3}" opacity="0.5"/>
  <rect x="40" y="370" width="220" height="2" fill="${c3}" opacity="0.6"/>
  <circle cx="150" cy="385" r="4" fill="${c3}" opacity="0.8"/>
  <path d="M40 40 L60 40 L40 60 Z" fill="${c3}" opacity="0.5"/>
  <path d="M260 40 L240 40 L260 60 Z" fill="${c3}" opacity="0.5"/>
  <path d="M40 380 L60 380 L40 360 Z" fill="${c3}" opacity="0.5"/>
  <path d="M260 380 L240 380 L260 360 Z" fill="${c3}" opacity="0.5"/>
</svg>`;
}

mockCovers.forEach((b, idx) => {
  const svg = generateSVG(b, idx);
  // 后端 covers 目录（用于 Spring Boot 静态资源）
  fs.writeFileSync(path.join(coversDir, `${idx + 1}.svg`), svg, 'utf-8');
  // 前端 public/covers 目录（用于 mock 模式下直接访问）
  fs.writeFileSync(path.join(publicCoversDir, `${idx + 1}.svg`), svg, 'utf-8');
  process.stdout.write('.');
});

// 生成 mock.js 封面路径映射代码
const coverMapCode = mockCovers.map((_, idx) => {
  return `  '/covers/${idx + 1}.svg'`;
}).join(',\n');

const mockPatchCode = `
// 封面路径映射: 按 BOOK_TITLES 索引分配
const BOOK_COVERS = [
${coverMapCode}
];
`;

console.log('');
console.log('完成: ' + mockCovers.length + ' 本 mock 封面 SVG 已生成');
console.log('  后端: ' + coversDir);
console.log('  前端: ' + publicCoversDir);
console.log('');
console.log('=== 以下代码需添加到 mock.js initDB 中 ===');
console.log('在 cover: 处改为: cover: BOOK_COVERS[seq - 1] || \'\'');
