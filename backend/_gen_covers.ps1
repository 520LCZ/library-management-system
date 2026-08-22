$ErrorActionPreference = 'Continue'
$backendDir = "c:\Users\lv227\Desktop\前端计划\library-management-system\backend"
$coversDir = "$backendDir\covers"
New-Item -ItemType Directory -Force -Path $coversDir | Out-Null

$books = @(
  @{id=1;  en='Dream of the Red Chamber';        style='classical Chinese palace interior, red and gold, ornate, elegant, traditional painting style'},
  @{id=2;  en='Journey to the West';             style='classic Chinese mythology, monkey king with golden staff, colorful, traditional Chinese illustration'},
  @{id=3;  en='Water Margin';                    style='ancient Chinese heroes, sword wielding outlaws, dramatic ink wash painting style, dynamic'},
  @{id=4;  en='Romance of the Three Kingdoms';   style='ancient Chinese warriors in battle, heroic, dramatic, traditional Chinese painting style'},
  @{id=5;  en='To Live';                         style='rural Chinese life, elderly farmer, emotional, minimalist oil painting, muted earth tones'},
  @{id=6;  en='Ordinary World';                  style='Chinese youth in rural countryside, warm sunset, hopeful, realistic oil painting'},
  @{id=7;  en='One Hundred Years of Solitude';   style='Magical realism, Latin American town, yellow butterflies, mystical atmosphere'},
  @{id=8;  en='Computer Systems';                style='clean modern tech book cover, circuit board pattern, blue gradient, minimalist'},
  @{id=9;  en='Introduction to Algorithms';      style='abstract algorithm visualization, geometric shapes, blue and purple gradient, tech book'},
  @{id=10; en='Code Complete';                   style='software engineering book cover, blue gradient, clean modern typography style'},
  @{id=11; en='Design Patterns';                 style='geometric patterns, interconnected shapes, purple and blue, tech book cover'},
  @{id=12; en='Thinking in Java';               style='Java programming book cover, coffee cup, code background, warm orange accent'},
  @{id=13; en='Effective Java';                 style='clean developer book cover, blue and white, code snippet pattern, minimal design'},
  @{id=14; en='Refactoring';                    style='blueprints and building renovation theme, clean tech book cover'},
  @{id=15; en='Clean Code';                      style='clean minimalist developer book cover, black and white, elegant typography'},
  @{id=16; en='Spring Boot in Action';          style='Spring boot plant illustration, green gradient, Java programming book cover'},
  @{id=17; en='Records of the Grand Historian';  style='ancient Chinese bamboo scroll, imperial court, historical ink painting'},
  @{id=18; en='Comprehensive Mirror in Aid of Governance'; style='ancient Chinese court scene, historical scroll painting, elegant ink style'},
  @{id=19; en='Ming Those Things';               style='Chinese imperial court drama, humorous cartoon style, Ming dynasty aesthetics'},
  @{id=20; en='Sapiens';                         style='human evolution, ancient to modern, journey of mankind, warm earth tones'},
  @{id=21; en='The Death of the Ming Dynasty';   style='Ming dynasty politics, historical portrait, elegant classical Chinese style'},
  @{id=22; en='The Story of Art';                style='collage of famous paintings, colorful art history book cover, sophisticated'},
  @{id=23; en='Gu Er Talks About Painting';      style='charming cartoon painter at easel, humorous, colorful, friendly illustration'},
  @{id=24; en='The Path of Beauty';              style='Chinese art and nature aesthetic, mountains and river, traditional ink painting'},
  @{id=25; en='Designing Design';                style='Japanese minimalist design, white space, elegant typography, subtle texture'},
  @{id=26; en='The Three-Body Problem';         style='cosmic science fiction, three suns rising, alien civilization, dark space'},
  @{id=27; en='The Dark Forest';                style='dark cosmic forest, alien spaceships, epic science fiction, atmospheric'},
  @{id=28; en='Death is Eternal';               style='cosmic apocalypse, dimensional universe, science fiction epic, dramatic'},
  @{id=29; en='A Brief History of Time';        style='cosmic time and space, galaxy, black hole, physics book cover, dark blue'},
  @{id=30; en='One Two Three Infinity';         style='mathematical infinity symbols, colorful numbers, popular science book cover'},
  @{id=31; en='The Selfish Gene';               style='DNA helix, evolutionary biology, modern science book cover, green and blue'},
  @{id=32; en='Does God Play Dice?';            style='quantum physics, dice in cosmic space, uncertainty principle, surreal physics book'}
)

$base = "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image"
$headers = @{ "Accept" = "image/jpeg" }

$results = @()
$ok = 0
$fail = 0
foreach ($b in $books) {
  $id = $b.id
  $prompt = $b.style + ", professional book cover design, 3:4 aspect ratio, high quality"
  $encoded = [System.Uri]::EscapeDataString($prompt)
  $url = "$base`?prompt=$encoded&image_size=portrait_4_3"
  $outFile = "$coversDir\$id.jpg"
  try {
    $resp = Invoke-WebRequest -Uri $url -OutFile $outFile -TimeoutSec 60 -UseBasicParsing
    $size = (Get-Item $outFile).Length
    if ($size -gt 5000) {
      Write-Output "OK    id=$id size=$size"
      $results += @{id=$id; cover="/covers/$id.jpg"; status='ok'}
      $ok++
    } else {
      Write-Output "SMALL id=$id size=$size"
      $results += @{id=$id; cover=''; status='small'}
      $fail++
    }
  } catch {
    Write-Output "FAIL  id=$id err=$($_.Exception.Message)"
    $results += @{id=$id; cover=''; status='fail'}
    $fail++
  }
}
Write-Output ""
Write-Output "SUMMARY: ok=$ok fail=$fail"
$results | ConvertTo-Json -Depth 3 | Out-File -FilePath "$backendDir\cover_results.json" -Encoding UTF8
Write-Output "saved cover_results.json"
