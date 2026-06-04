$ErrorActionPreference = "Stop"
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$ROOT = $PSScriptRoot

$SERVER = "wej-user@192.168.5.136"
$PASS = "HONGren@kk868686"
$REMOTE = "/data/influencer-marketing"

# ============ Jar mapping ============
$BACKENDS = @(
  @{ Module = "backend-user"; Jar = "athluna-kms-backend-1.0.0-exec.jar" },
  @{ Module = "backend-influencer"; Jar = "athluna-influencer-1.0.4.jar" },
  @{ Module = "backend-shopify-integration"; Jar = "athluna-shopify-integration-1.0.4.jar" },
  @{ Module = "backend-webhook"; Jar = "athluna-webhook-1.0.0.jar" },
  @{ Module = "backend-ai-agent"; Jar = "athluna-ai-agent-1.0.0.jar" }
)

Write-Host ""
Write-Host "===========================================================" -ForegroundColor Cyan
Write-Host "   红人营销系统全量部署 (Frontend + All Backends)" -ForegroundColor Cyan
Write-Host "===========================================================" -ForegroundColor Cyan

# ========== 1. Build backends ==========
Write-Host ""
Write-Host "[1/6] Building all backend services..." -ForegroundColor Yellow
foreach ($svc in $BACKENDS) {
  $m = $svc.Module
  Write-Host "  -> Building $m ..." -ForegroundColor Gray
  Set-Location "$ROOT\$m"
  mvn clean package -DskipTests -q
  if ($LASTEXITCODE -ne 0) { Write-Host "  X $m build FAILED!" -ForegroundColor Red; exit 1 }
  Write-Host "  OK $m build success" -ForegroundColor Green
}
Set-Location $ROOT

# ========== 2. Build frontend ==========
Write-Host ""
Write-Host "[2/6] Building frontend..." -ForegroundColor Yellow
Set-Location "$ROOT\frontend"
npm run build
if ($LASTEXITCODE -ne 0) { Write-Host "  X Frontend build FAILED!" -ForegroundColor Red; exit 1 }
Write-Host "  OK Frontend build success" -ForegroundColor Green
Set-Location $ROOT

# ========== 3. Package ==========
Write-Host ""
Write-Host "[3/6] Packaging files..." -ForegroundColor Yellow
$STAGING = "$ROOT\staging"

# Clean old staging
if (Test-Path $STAGING) { Remove-Item -Recurse -Force $STAGING }

foreach ($svc in $BACKENDS) {
  $m = $svc.Module; $j = $svc.Jar
  $dir = "$STAGING\$m"
  New-Item -ItemType Directory -Force -Path $dir | Out-Null
  Copy-Item -Path "$ROOT\$m\target\$j" -Destination "$dir\app.jar" -Force
  Write-Host "  -> $m\app.jar" -ForegroundColor Gray
}

New-Item -ItemType Directory -Force -Path "$STAGING\frontend" | Out-Null 
Copy-Item -Path "$ROOT\frontend\dist" -Destination "$STAGING\frontend" -Recurse -Force
Copy-Item -Path "$ROOT\frontend\default.conf" -Destination "$STAGING\frontend\default.conf" -Force
Copy-Item -Path "$ROOT\docker-compose.prod.yml" -Destination "$STAGING\docker-compose.prod.yml" -Force
Write-Host "  -> frontend\dist & frontend\default.conf" -ForegroundColor Gray

Set-Location $STAGING
tar -cf "$ROOT\deploy_pkg.tar" *
Set-Location $ROOT
Remove-Item -Recurse -Force $STAGING
Write-Host "  OK deploy_pkg.tar generated" -ForegroundColor Green

# ========== 4. Upload ==========
Write-Host ""
Write-Host "[4/6] Uploading to server..." -ForegroundColor Yellow
Write-Host "  -> If prompted for password, enter: $PASS" -ForegroundColor Magenta
scp deploy_pkg.tar "${SERVER}:/tmp/deploy_pkg.tar"

# ========== 5. Remote deploy ==========
Write-Host ""
Write-Host "[5/6] Deploying on server..." -ForegroundColor Yellow
Write-Host "  -> If prompted again, enter: $PASS" -ForegroundColor Magenta

$remote_cmd = @"
set -e
echo '$PASS' | sudo -S rm -rf $REMOTE/frontend/dist
echo '$PASS' | sudo -S tar -xf /tmp/deploy_pkg.tar -C $REMOTE/
cd $REMOTE
echo '$PASS' | sudo -S docker compose -f docker-compose.prod.yml stop
echo '$PASS' | sudo -S docker compose -f docker-compose.prod.yml build
echo '$PASS' | sudo -S docker compose -f docker-compose.prod.yml up -d
echo '$PASS' | sudo -S rm /tmp/deploy_pkg.tar
echo '--- Container Status ---'
echo '$PASS' | sudo -S docker compose -f docker-compose.prod.yml ps
"@

ssh -t $SERVER $remote_cmd

# ========== 6. Cleanup ==========
Write-Host ""
Write-Host "[6/6] Cleaning up local temp files..." -ForegroundColor Yellow
Remove-Item -Force deploy_pkg.tar -ErrorAction SilentlyContinue
Write-Host "  OK Cleaned" -ForegroundColor Green

Write-Host ""
Write-Host "===========================================================" -ForegroundColor Green
Write-Host "   Full deploy complete! All services are online!" -ForegroundColor Green
Write-Host "===========================================================" -ForegroundColor Green
