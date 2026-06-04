$ErrorActionPreference = "Stop"
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$ROOT = $PSScriptRoot

$SERVER = "wej-user@192.168.5.136"
$PASS = "HONGren@kk868686"
$REMOTE = "/data/influencer-marketing"

Write-Host ""
Write-Host "===========================================================" -ForegroundColor Cyan
Write-Host "   红人营销系统前端部署" -ForegroundColor Cyan
Write-Host "===========================================================" -ForegroundColor Cyan

# ========== 1. Build frontend ==========
Write-Host ""
Write-Host "[1/4] Building frontend..." -ForegroundColor Yellow
Set-Location "$ROOT\frontend"
npm run build
if ($LASTEXITCODE -ne 0) { Write-Host "  X Frontend build FAILED!" -ForegroundColor Red; exit 1 }
Write-Host "  OK Frontend build success" -ForegroundColor Green
Set-Location $ROOT

# ========== 2. Package ==========
Write-Host ""
Write-Host "[2/4] Packaging frontend files..." -ForegroundColor Yellow
$STAGING = "$ROOT\staging"

if (Test-Path $STAGING) { Remove-Item -Recurse -Force $STAGING }

New-Item -ItemType Directory -Force -Path "$STAGING\frontend" | Out-Null
Copy-Item -Path "$ROOT\frontend\dist" -Destination "$STAGING\frontend" -Recurse -Force
Copy-Item -Path "$ROOT\frontend\default.conf" -Destination "$STAGING\frontend\default.conf" -Force
Copy-Item -Path "$ROOT\docker-compose.prod.yml" -Destination "$STAGING\docker-compose.prod.yml" -Force
Write-Host "  -> frontend\dist & frontend\default.conf" -ForegroundColor Gray

Set-Location $STAGING
tar -cf "$ROOT\deploy_frontend.tar" *
Set-Location $ROOT
Start-Sleep -Milliseconds 500
cmd /c "rd /s /q `"$STAGING`"" 2>$null
Write-Host "  OK deploy_frontend.tar generated" -ForegroundColor Green

# ========== 3. Upload & Deploy ==========
Write-Host ""
Write-Host "[3/4] Uploading & deploying on server..." -ForegroundColor Yellow
Write-Host "  -> If prompted for password, enter: $PASS" -ForegroundColor Magenta
scp deploy_frontend.tar "${SERVER}:/tmp/deploy_frontend.tar"

Write-Host "  -> Deploying..." -ForegroundColor Yellow
Write-Host "  -> If prompted again, enter: $PASS" -ForegroundColor Magenta

$remote_cmd = @"
set -e
echo '$PASS' | sudo -S rm -rf $REMOTE/frontend/dist
echo '$PASS' | sudo -S tar -xf /tmp/deploy_frontend.tar -C $REMOTE/
cd $REMOTE
echo '$PASS' | sudo -S docker compose -f docker-compose.prod.yml stop frontend
echo '$PASS' | sudo -S docker compose -f docker-compose.prod.yml build frontend
echo '$PASS' | sudo -S docker compose -f docker-compose.prod.yml up -d frontend
echo '$PASS' | sudo -S rm /tmp/deploy_frontend.tar
echo '--- Frontend Container Status ---'
echo '$PASS' | sudo -S docker compose -f docker-compose.prod.yml ps frontend
"@

ssh -t $SERVER $remote_cmd

# ========== 4. Cleanup ==========
Write-Host ""
Write-Host "[4/4] Cleaning up..." -ForegroundColor Yellow
Remove-Item -Force deploy_frontend.tar -ErrorAction SilentlyContinue
Write-Host "  OK Cleaned" -ForegroundColor Green

Write-Host ""
Write-Host "===========================================================" -ForegroundColor Green
Write-Host "   Frontend deploy complete!" -ForegroundColor Green
Write-Host "===========================================================" -ForegroundColor Green
