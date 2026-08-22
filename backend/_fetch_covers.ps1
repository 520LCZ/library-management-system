$ErrorActionPreference = 'SilentlyContinue'
$backendDir = "c:\Users\lv227\Desktop\前端计划\library-management-system\backend"
$coversDir = "$backendDir\covers"
New-Item -ItemType Directory -Force -Path $coversDir | Out-Null

$books = @(
  @{id=1; isbn='9787020002207'},
  @{id=2; isbn='9787020002208'},
  @{id=3; isbn='9787020002209'},
  @{id=4; isbn='9787020002210'},
  @{id=5; isbn='9787506365437'},
  @{id=6; isbn='9787020042527'},
  @{id=7; isbn='9787544251179'},
  @{id=8; isbn='9787111544937'},
  @{id=9; isbn='9787111407010'},
  @{id=10; isbn='9787121028867'},
  @{id=11; isbn='9787111075752'},
  @{id=12; isbn='9787111213826'},
  @{id=13; isbn='9787111255833'},
  @{id=14; isbn='9787115168134'},
  @{id=15; isbn='9787115216878'},
  @{id=16; isbn='9787115441039'},
  @{id=17; isbn='9787101003048'},
  @{id=18; isbn='9787101001020'},
  @{id=19; isbn='9787550200356'},
  @{id=20; isbn='9787508647357'},
  @{id=21; isbn='9787101055709'},
  @{id=22; isbn='9787549550869'},
  @{id=23; isbn='9787508645797'},
  @{id=24; isbn='9787108018755'},
  @{id=25; isbn='9787563380432'},
  @{id=26; isbn='9787536692930'},
  @{id=27; isbn='9787536693937'},
  @{id=28; isbn='9787536693999'},
  @{id=29; isbn='9787535732309'},
  @{id=30; isbn='9787030053493'},
  @{id=31; isbn='9787508647487'},
  @{id=32; isbn='9787553809200'}
)

$results = @()
$ok = 0
$miss = 0
foreach ($b in $books) {
  $isbn = $b.isbn
  $id = $b.id
  $url = "https://covers.openlibrary.org/b/isbn/$isbn-L.jpg"
  $outFile = "$coversDir\$isbn.jpg"
  try {
    Invoke-WebRequest -Uri $url -OutFile $outFile -TimeoutSec 15 -UseBasicParsing
    $size = (Get-Item $outFile).Length
    if ($size -gt 1000) {
      Write-Output "OK    id=$id isbn=$isbn size=$size"
      $results += @{id=$id; cover=$url; mode='ol'}
      $ok++
    } else {
      Remove-Item $outFile -Force -ErrorAction SilentlyContinue
      Write-Output "SMALL id=$id isbn=$isbn size=$size (placeholder)"
      $results += @{id=$id; cover=''; mode='miss'}
      $miss++
    }
  } catch {
    Write-Output "FAIL  id=$id isbn=$isbn"
    $results += @{id=$id; cover=''; mode='fail'}
    $miss++
  }
}
Write-Output ""
Write-Output "SUMMARY: ok=$ok miss=$miss"
$results | ConvertTo-Json -Depth 3 | Out-File -FilePath "$backendDir\cover_results.json" -Encoding UTF8
Write-Output "saved cover_results.json"
