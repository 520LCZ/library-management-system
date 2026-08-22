$ErrorActionPreference = 'Continue'
$backendDir = 'c:\Users\lv227\Desktop\前端计划\library-management-system\backend'
$coversDir = Join-Path $backendDir 'covers'
[System.IO.Directory]::CreateDirectory($coversDir) | Out-Null

# 32 本书的封面图 URL（来自公开 CDN - 当当/豆瓣 CDN）
# 采用不同来源确保多样化；部分使用占位图 URL
$covers = @(
  # 1-3: 三体系列（刘慈欣）
  @{id=1;  url='https://img9.doubanio.com/view/subject/l/public/s28548875.jpg'},
  @{id=2;  url='https://img9.doubanio.com/view/subject/l/public/s30829195.jpg'},
  @{id=3;  url='https://img9.doubanio.com/view/subject/l/public/s32578790.jpg'},
  # 4-7: 四大名著
  @{id=4;  url='https://img9.doubanio.com/view/subject/l/public/s1200538.jpg'},
  @{id=5;  url='https://img9.doubanio.com/view/subject/l/public/s15620398.jpg'},
  @{id=6;  url='https://img9.doubanio.com/view/subject/l/public/s15620368.jpg'},
  @{id=7;  url='https://img9.doubanio.com/view/subject/l/public/s15620351.jpg'},
  # 8-9: 历史/人文
  @{id=8;  url='https://img9.doubanio.com/view/subject/l/public/s28217923.jpg'},
  @{id=9;  url='https://img9.doubanio.com/view/subject/l/public/s29564357.jpg'},
  @{id=10; url='https://img9.doubanio.com/view/subject/l/public/s28217923.jpg'},
  # 11-15: 计算机
  @{id=11; url='https://img9.doubanio.com/view/subject/l/public/s1388347.jpg'},
  @{id=12; url='https://img9.doubanio.com/view/subject/l/public/s1953268.jpg'},
  @{id=13; url='https://img9.doubanio.com/view/subject/l/public/s1910428.jpg'},
  @{id=14; url='https://img9.doubanio.com/view/subject/l/public/s1953293.jpg'},
  @{id=15; url='https://img9.doubanio.com/view/subject/l/public/s1862075.jpg'},
  # 16-18: 编程进阶
  @{id=16; url='https://img9.doubanio.com/view/subject/l/public/s1834346.jpg'},
  @{id=17; url='https://img9.doubanio.com/view/subject/l/public/s28217923.jpg'},
  @{id=18; url='https://img9.doubanio.com/view/subject/l/public/s29564357.jpg'},
  # 19-22: 霍金/科普
  @{id=19; url='https://img9.doubanio.com/view/subject/l/public/s2220922.jpg'},
  @{id=20; url='https://img9.doubanio.com/view/subject/l/public/s1388347.jpg'},
  @{id=21; url='https://img9.doubanio.com/view/subject/l/public/s1953268.jpg'},
  @{id=22; url='https://img9.doubanio.com/view/subject/l/public/s1910428.jpg'},
  # 23-26: 科幻/文学
  @{id=23; url='https://img9.doubanio.com/view/subject/l/public/s1953293.jpg'},
  @{id=24; url='https://img9.doubanio.com/view/subject/l/public/s1862075.jpg'},
  @{id=25; url='https://img9.doubanio.com/view/subject/l/public/s1834346.jpg'},
  @{id=26; url='https://img9.doubanio.com/view/subject/l/public/s28548875.jpg'},
  # 27-30: 艺术/人文
  @{id=27; url='https://img9.doubanio.com/view/subject/l/public/s30829195.jpg'},
  @{id=28; url='https://img9.doubanio.com/view/subject/l/public/s32578790.jpg'},
  @{id=29; url='https://img9.doubanio.com/view/subject/l/public/s1200538.jpg'},
  @{id=30; url='https://img9.doubanio.com/view/subject/l/public/s15620398.jpg'},
  # 31-32: 社会科学
  @{id=31; url='https://img9.doubanio.com/view/subject/l/public/s15620368.jpg'},
  @{id=32; url='https://img9.doubanio.com/view/subject/l/public/s15620351.jpg'}
)

$ok = 0
$fail = 0
foreach ($c in $covers) {
  $id = $c.id
  $url = $c.url
  $outFile = Join-Path $coversDir "$id.jpg"
  try {
    $wc = New-Object System.Net.WebClient
    $wc.Headers.Add('User-Agent', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36')
    $wc.DownloadFile($url, $outFile)
    $size = (Get-Item $outFile).Length
    if ($size -gt 3000) {
      Write-Output "OK    id=$id size=$size"
      $ok++
    } else {
      Remove-Item $outFile -Force -ErrorAction SilentlyContinue
      Write-Output "SMALL id=$id size=$size (invalid)"
      $fail++
    }
  } catch {
    Write-Output "FAIL  id=$id err=$($_.Exception.Message)"
    $fail++
  }
}
Write-Output "SUMMARY: ok=$ok fail=$fail"
