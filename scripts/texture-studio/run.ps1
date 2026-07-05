$ErrorActionPreference = "Stop"
$here = Split-Path -Parent $MyInvocation.MyCommand.Path
$venvPy = Join-Path $here ".venv\Scripts\python.exe"

if (-not (Test-Path $venvPy)) {
    & (Join-Path $here "setup.ps1")
}

$repo = Split-Path -Parent (Split-Path -Parent $here)
Push-Location $repo
try {
    & $venvPy (Join-Path $here "pe_studio.py") @args
    exit $LASTEXITCODE
} finally {
    Pop-Location
}
