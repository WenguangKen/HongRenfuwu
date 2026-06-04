$ErrorActionPreference = "Stop"
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$ROOT = $PSScriptRoot

$SERVER = "wej-user@192.168.5.136"
$PASS = "HONGren@kk868686"
$REMOTE = "/data/influencer-marketing"
$MODULE = "backend-influencer"
$JAR = "athluna-influencer-1.0.4.jar"

Write-Host ""
Write-Host "===========================================================" -ForegroundColor Cyan
Write-Host "   红人营销系统 Influencer 后端部署" -ForegroundColor Cyan
Write-Host "===========================================================" -ForegroundColor Cyan

# ========== 1. Build backend-influencer ==========
Write-Host ""
Write-Host "[1/4] Building $MODULE ..." -ForegroundColor Yellow
Set-Location "$ROOT\$MODULE"
mvn clean package -DskipTests -q
if ($LASTEXITCODE -ne 0) { Write-Host "  X $MODULE build FAILED!" -ForegroundColor Red; exit 1 }
Write-Host "  OK $MODULE build success" -ForegroundColor Green
Set-Location $ROOT

# ========== 2. Package ==========
Write-Host ""
Write-Host "[2/4] Packaging $MODULE ..." -ForegroundColor Yellow
$STAGING = "$ROOT\staging"

if (Test-Path $STAGING) { Remove-Item -Recurse -Force $STAGING }

$dir = "$STAGING\$MODULE"
New-Item -ItemType Directory -Force -Path $dir | Out-Null
Copy-Item -Path "$ROOT\$MODULE\target\$JAR" -Destination "$dir\app.jar" -Force
Copy-Item -Path "$ROOT\docker-compose.prod.yml" -Destination "$STAGING\docker-compose.prod.yml" -Force
Write-Host "  -> $MODULE\app.jar" -ForegroundColor Gray

Set-Location $STAGING
tar -cf "$ROOT\deploy_influencer.tar" *
Set-Location $ROOT
Start-Sleep -Milliseconds 500
cmd /c "rd /s /q `"$STAGING`"" 2>$null
Write-Host "  OK deploy_influencer.tar generated" -ForegroundColor Green

# ========== 3. Upload & Deploy ==========
Write-Host ""
Write-Host "[3/4] Uploading & deploying on server..." -ForegroundColor Yellow
Write-Host "  -> If prompted for password, enter: $PASS" -ForegroundColor Magenta
scp deploy_influencer.tar "${SERVER}:/tmp/deploy_influencer.tar"

Write-Host "  -> Deploying..." -ForegroundColor Yellow
Write-Host "  -> If prompted again, enter: $PASS" -ForegroundColor Magenta

$remote_cmd = @"
set -e
echo '$PASS' | sudo -S tar -xf /tmp/deploy_influencer.tar -C $REMOTE/
cd $REMOTE
echo '$PASS' | sudo -S docker compose -f docker-compose.prod.yml stop $MODULE
echo '$PASS' | sudo -S docker compose -f docker-compose.prod.yml build $MODULE
echo '$PASS' | sudo -S docker compose -f docker-compose.prod.yml up -d $MODULE
echo '$PASS' | sudo -S rm /tmp/deploy_influencer.tar
echo '--- Backend-Influencer Container Status ---'
echo '$PASS' | sudo -S docker compose -f docker-compose.prod.yml ps $MODULE
"@

ssh -t $SERVER $remote_cmd

# ========== 4. Cleanup ==========
Write-Host ""
Write-Host "[4/4] Cleaning up..." -ForegroundColor Yellow
Remove-Item -Force deploy_influencer.tar -ErrorAction SilentlyContinue
Write-Host "  OK Cleaned" -ForegroundColor Green

Write-Host ""
Write-Host "===========================================================" -ForegroundColor Green
Write-Host "   Backend-Influencer deploy complete!" -ForegroundColor Green
Write-Host "===========================================================" -ForegroundColor Green
