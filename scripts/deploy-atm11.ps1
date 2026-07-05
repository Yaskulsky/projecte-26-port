# Build ProjectEE, verify JAR, deploy numbered build to ATM11 (Minecraft must be closed)
param(
    [switch]$SkipBuild,
    [switch]$Force
)

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
$destDir = "C:\CurseForge\Instances\All the Mods 11 - ATM11\mods"
$buildNumFile = Join-Path $root ".build-number"
$mcProcess = "javaw", "minecraft", "Minecraft"

Set-Location $root

function Get-ModVersion {
    $props = Get-Content (Join-Path $root "gradle.properties") -Raw
    if ($props -match 'projecte_version=(.+)') { return $Matches[1].Trim() }
    throw "projecte_version not found in gradle.properties"
}

function Get-BuildNumber {
    $n = 0
    if (Test-Path $buildNumFile) {
        $raw = (Get-Content $buildNumFile -Raw).Trim()
        if ($raw -match '^\d+$') { $n = [int]$raw }
    }
    $n++
    Set-Content -Path $buildNumFile -Value $n -NoNewline
    return $n
}

$version = Get-ModVersion
$buildNum = Get-BuildNumber
$buildTag = "b{0:D3}" -f $buildNum
$baseJar = Join-Path $root "build\libs\projectee-$version.jar"
$numberedJar = "projectee-$version-$buildTag.jar"
$dest = Join-Path $destDir $numberedJar

$running = Get-Process -ErrorAction SilentlyContinue | Where-Object {
    $mcProcess -contains $_.ProcessName
}
if ($running -and -not $Force) {
    Write-Host "ERROR: Minecraft/Java appears running. Close game first or use -Force." -ForegroundColor Red
    $running | ForEach-Object { Write-Host "  PID $($_.Id): $($_.ProcessName)" }
    Write-Host "Hot deploy corrupts JAR (ZipException, missing icons, GUI crash)."
    # Roll back build number increment on abort
    if ($buildNum -gt 1) { Set-Content -Path $buildNumFile -Value ($buildNum - 1) -NoNewline }
    exit 1
}

if (-not $SkipBuild) {
    Write-Host "=== Building ProjectEE $version ($buildTag) ===" -ForegroundColor Cyan
    & .\gradlew.bat build -x test
    if ($LASTEXITCODE -ne 0) {
        if ($buildNum -gt 1) { Set-Content -Path $buildNumFile -Value ($buildNum - 1) -NoNewline }
        exit $LASTEXITCODE
    }
}

if (-not (Test-Path $baseJar)) {
    Write-Host "JAR not found: $baseJar" -ForegroundColor Red
    exit 1
}

Write-Host "=== Verifying JAR ===" -ForegroundColor Cyan
& (Join-Path $root "scripts\port-check.ps1") -VerifyJar -Quiet
if ($LASTEXITCODE -ne 0) {
    Write-Host "JAR verify failed - not deploying." -ForegroundColor Red
    exit 1
}

if (-not (Test-Path $destDir)) {
    Write-Host "ATM mods folder missing: $destDir" -ForegroundColor Red
    exit 1
}

Write-Host "=== Deploying $numberedJar ===" -ForegroundColor Cyan
# Remove older ProjectE/ProjectEE jars to avoid duplicate mod loads
Get-ChildItem $destDir -Filter "projecte*.jar" -ErrorAction SilentlyContinue | Remove-Item -Force
Get-ChildItem $destDir -Filter "projectee*.jar" -ErrorAction SilentlyContinue | Remove-Item -Force
Copy-Item -Force $baseJar $dest
Write-Host "Copied to $dest" -ForegroundColor Green
Write-Host "ProjectEE v$version build $buildTag ready - start ATM11" -ForegroundColor Cyan
