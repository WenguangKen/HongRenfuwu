$ErrorActionPreference = "Stop"
$ROOT = Split-Path $PSScriptRoot -Parent

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  红人营销系统 - 本地开发启动" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 1. 启动基础设施
Write-Host "[1] 启动 MySQL + Redis ..." -ForegroundColor Yellow
Set-Location $ROOT
docker compose up -d
if ($LASTEXITCODE -ne 0) {
  Write-Host "Docker 启动失败，请先打开 Docker Desktop" -ForegroundColor Red
  exit 1
}

# 2. 初始化数据库（首次）
$initFlag = Join-Path $ROOT ".db-initialized"
if (-not (Test-Path $initFlag)) {
  Write-Host "[2] 首次运行，初始化数据库 ..." -ForegroundColor Yellow
  & "$ROOT\scripts\init-database.ps1"
  if ($LASTEXITCODE -ne 0) { exit 1 }
  New-Item -ItemType File -Path $initFlag -Force | Out-Null
} else {
  Write-Host "[2] 数据库已初始化，跳过" -ForegroundColor Gray
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "  基础设施已就绪，请开 4 个终端分别启动:" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "  终端1 - 用户服务 (8080):" -ForegroundColor White
Write-Host "    cd backend-user" -ForegroundColor Gray
Write-Host "    mvn spring-boot:run" -ForegroundColor Gray
Write-Host ""
Write-Host "  终端2 - 红人服务 (8082):" -ForegroundColor White
Write-Host "    cd backend-influencer" -ForegroundColor Gray
Write-Host "    mvn spring-boot:run" -ForegroundColor Gray
Write-Host ""
Write-Host "  终端3 - 易仓服务 (8081):" -ForegroundColor White
Write-Host "    cd backend-eccang-integration" -ForegroundColor Gray
Write-Host "    mvn spring-boot:run -Dspring-boot.run.profiles=dev" -ForegroundColor Gray
Write-Host ""
Write-Host "  终端4 - 前端 (5173):" -ForegroundColor White
Write-Host "    cd frontend" -ForegroundColor Gray
Write-Host "    npm install" -ForegroundColor Gray
Write-Host "    npm run dev" -ForegroundColor Gray
Write-Host ""
Write-Host "  访问: http://localhost:5173" -ForegroundColor Cyan
Write-Host "  数据库: localhost:3306 / hongrenfuwu / root" -ForegroundColor Cyan
Write-Host ""
