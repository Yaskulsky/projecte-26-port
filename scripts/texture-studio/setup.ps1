$ErrorActionPreference = "Stop"
$here = Split-Path -Parent $MyInvocation.MyCommand.Path
$venv = Join-Path $here ".venv"

if (-not (Test-Path $venv)) {
    Write-Host "Creating venv..."
    py -3 -m venv $venv
    & (Join-Path $venv "Scripts\pip.exe") install -r (Join-Path $here "requirements.txt")
    Write-Host "Setup done."
} else {
    Write-Host "venv OK"
}
