$ErrorActionPreference = "Stop"
$ProgressPreference = "SilentlyContinue"
$ROOT = Split-Path $PSScriptRoot -Parent

$DB_PASS = if ($env:DB_PASSWORD) { $env:DB_PASSWORD } else { "Yaoxingxuanlv-39" }
$DB_NAME = "hongrenfuwu"
$CONTAINER = "influencer-mysql-dev"

function Import-SqlUtf8([string]$FilePath) {
  if (-not (Test-Path $FilePath)) {
    throw "SQL 文件不存在: $FilePath"
  }
  $name = [System.IO.Path]::GetFileName($FilePath)
  $containerPath = "/tmp/$name"
  docker cp $FilePath "${CONTAINER}:${containerPath}" | Out-Null
  docker exec $CONTAINER sh -c "mysql --default-character-set=utf8mb4 -uroot -p'$DB_PASS' $DB_NAME < '$containerPath'"
  if ($LASTEXITCODE -ne 0) { throw "导入失败: $FilePath" }
}

Write-Host "等待 MySQL 就绪..." -ForegroundColor Yellow
$ready = $false
for ($i = 0; $i -lt 60; $i++) {
  cmd /c "docker exec $CONTAINER mysqladmin ping -h 127.0.0.1 -uroot -p$DB_PASS --silent >nul 2>&1"
  if ($LASTEXITCODE -eq 0) { $ready = $true; break }
  Start-Sleep -Seconds 3
}
if (-not $ready) {
  Write-Host "MySQL 未就绪，请先执行: docker compose up -d" -ForegroundColor Red
  exit 1
}

$structureSql = Get-ChildItem (Join-Path $ROOT "database_scripts") -Filter "hongrenfuwu*.sql" |
  Where-Object { $_.Length -lt 5MB } |
  Sort-Object Length |
  Select-Object -First 1 -ExpandProperty FullName

Write-Host "[1/6] 导入主库结构..." -ForegroundColor Cyan
Import-SqlUtf8 $structureSql

Write-Host "[2/6] 导入易仓表..." -ForegroundColor Cyan
Import-SqlUtf8 (Join-Path $ROOT "database_scripts\eccang_tables.sql")

Write-Host "[3/6] 重建易仓商品表..." -ForegroundColor Cyan
Import-SqlUtf8 (Join-Path $ROOT "database_scripts\recreate_eccang_product_tables.sql")

Write-Host "[4/6] 清理废弃表..." -ForegroundColor Cyan
Import-SqlUtf8 (Join-Path $ROOT "database_scripts\drop_shopify_order_tables.sql")

Write-Host "[5/7] 修复易仓表中文备注..." -ForegroundColor Cyan
Import-SqlUtf8 (Join-Path $ROOT "database_scripts\fix_eccang_comments.sql")

Write-Host "[6/8] 清理遗留冗余表..." -ForegroundColor Cyan
Import-SqlUtf8 (Join-Path $ROOT "database_scripts\drop_legacy_tables.sql")

Write-Host "[7/8] 修复易仓店铺名称..." -ForegroundColor Cyan
Import-SqlUtf8 (Join-Path $ROOT "database_scripts\fix_eccang_store_names.sql")

Write-Host "[8/8] 完成" -ForegroundColor Cyan
Write-Host "数据库初始化完成: $DB_NAME" -ForegroundColor Green
