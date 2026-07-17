# Rebrand Equivalence -> Equivox (modId: equivox)
# Run from repo root: .\scripts\rebrand-equivox.ps1
$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
Set-Location $root

function Move-Tree($from, $to) {
    if (-not (Test-Path $from)) {
        Write-Host "SKIP missing: $from" -ForegroundColor Yellow
        return
    }
    $parent = Split-Path $to -Parent
    if (-not (Test-Path $parent)) {
        New-Item -ItemType Directory -Force -Path $parent | Out-Null
    }
    if (Test-Path $to) {
        throw "Target already exists: $to"
    }
    Move-Item -LiteralPath $from -Destination $to
    Write-Host "MOVED $from -> $to" -ForegroundColor Green
}

function Remove-EmptyParents($path) {
    $dir = $path
    while ($dir -and (Test-Path $dir)) {
        $items = Get-ChildItem -LiteralPath $dir -Force -ErrorAction SilentlyContinue
        if ($items -and $items.Count -gt 0) { break }
        $parent = Split-Path $dir -Parent
        Remove-Item -LiteralPath $dir -Force -ErrorAction SilentlyContinue
        $dir = $parent
    }
}

Write-Host "=== Phase 1: move packages / assets ===" -ForegroundColor Cyan

$moves = @(
    @{ From = "src\api\java\com\yaskulsky\equivalence"; To = "src\api\java\com\yaskulsky\equivox" },
    @{ From = "src\main\java\com\yaskulsky\equivalence"; To = "src\main\java\com\yaskulsky\equivox" },
    @{ From = "src\datagen\java\com\yaskulsky\equivalence"; To = "src\datagen\java\com\yaskulsky\equivox" },
    @{ From = "src\test\java\com\yaskulsky\equivalence"; To = "src\test\java\com\yaskulsky\equivox" },
    @{ From = "buildSrc\src\main\groovy\com\yaskulsky\equivalence"; To = "buildSrc\src\main\groovy\com\yaskulsky\equivox" },
    @{ From = "src\main\resources\assets\equivalence"; To = "src\main\resources\assets\equivox" },
    @{ From = "src\main\resources\data\equivalence"; To = "src\main\resources\data\equivox" },
    @{ From = "src\datagen\generated\assets\equivalence"; To = "src\datagen\generated\assets\equivox" },
    @{ From = "src\datagen\generated\data\equivalence"; To = "src\datagen\generated\data\equivox" }
)

foreach ($m in $moves) {
    Move-Tree $m.From $m.To
    Remove-EmptyParents (Split-Path $m.From -Parent)
}

# META-INF services filenames
$svcDir = "src\main\resources\META-INF\services"
if (Test-Path $svcDir) {
    Get-ChildItem $svcDir -File | ForEach-Object {
        if ($_.Name -like "com.yaskulsky.equivalence.*") {
            $newName = $_.Name.Replace("com.yaskulsky.equivalence.", "com.yaskulsky.equivox.")
            Rename-Item -LiteralPath $_.FullName -NewName $newName
            Write-Host "RENAMED service $($_.Name) -> $newName" -ForegroundColor Green
        }
    }
}

Write-Host "=== Phase 2: rename Equivalence* source files ===" -ForegroundColor Cyan
$fileRenames = @{
    "EquivalenceAPI.java"            = "EquivoxAPI.java"
    "EquivalenceRegistries.java"     = "EquivoxRegistries.java"
    "EquivalenceConfig.java"         = "EquivoxConfig.java"
    "EquivalenceDataGenerator.java"  = "EquivoxDataGenerator.java"
    "EquivalenceEmiDefaults.java"    = "EquivoxEmiDefaults.java"
    "EquivalenceAliases.java"        = "EquivoxAliases.java"
    "EquivalenceAliasMapping.java"   = "EquivoxAliasMapping.java"
    "EquivalenceTNT.java"            = "EquivoxTNT.java"
    "EquivalenceProxyTest.java"      = "EquivoxProxyTest.java"
    "EquivalenceClassInit.java"      = "EquivoxClassInit.java"
    "equivalence.json"               = "equivox.json"  # curios entities
}

Get-ChildItem -Recurse -File | Where-Object {
    $_.FullName -notmatch '\\(build|\.gradle|\.git)\\' -and $fileRenames.ContainsKey($_.Name)
} | ForEach-Object {
    $newName = $fileRenames[$_.Name]
    Rename-Item -LiteralPath $_.FullName -NewName $newName
    Write-Host "RENAMED file $($_.Name) -> $newName"
}

Write-Host "=== Phase 3: bulk text replace ===" -ForegroundColor Cyan

$excludeDirRegex = '\\(build|\.gradle|\.git|textures|previews|energizedpower_recipes_extracted|net)\\'
$textExt = @("*.java","*.groovy","*.gradle","*.kts","*.toml","*.json","*.json5","*.mcmeta","*.cfg","*.txt","*.md","*.ps1","*.py","*.properties","*.xml","*.yml","*.yaml","*.lang","*.snbt")

# Ordered replacements (longest / most specific first)
$replacements = @(
    @{ Old = "EquivalenceAliasMapping"; New = "EquivoxAliasMapping" },
    @{ Old = "EquivalenceAliases"; New = "EquivoxAliases" },
    @{ Old = "EquivalenceClassInit"; New = "EquivoxClassInit" },
    @{ Old = "EquivalenceDataGenerator"; New = "EquivoxDataGenerator" },
    @{ Old = "EquivalenceEmiDefaults"; New = "EquivoxEmiDefaults" },
    @{ Old = "EquivalenceProxyTest"; New = "EquivoxProxyTest" },
    @{ Old = "EquivalenceRegistries"; New = "EquivoxRegistries" },
    @{ Old = "EquivalenceConfig"; New = "EquivoxConfig" },
    @{ Old = "EquivalenceTNT"; New = "EquivoxTNT" },
    @{ Old = "EquivalenceAPI"; New = "EquivoxAPI" },
    @{ Old = "EQUIVALENCE_MODID"; New = "EQUIVOX_MODID" },
    @{ Old = "com.yaskulsky.equivalence"; New = "com.yaskulsky.equivox" },
    @{ Old = "com/yaskulsky/equivalence"; New = "com/yaskulsky/equivox" },
    @{ Old = "assets/equivalence"; New = "assets/equivox" },
    @{ Old = "assets\equivalence"; New = "assets\equivox" },
    @{ Old = "data/equivalence"; New = "data/equivox" },
    @{ Old = "data\equivalence"; New = "data\equivox" },
    @{ Old = "dependencies.equivalence"; New = "dependencies.equivox" },
    @{ Old = "equivalence_datagen"; New = "equivox_datagen" },
    @{ Old = "equivalence_version"; New = "equivox_version" },
    @{ Old = "equivalence.skip_top"; New = "equivox.skip_top" },
    @{ Old = "equivalence:plugin"; New = "equivox:plugin" },
    @{ Old = "config/Equivalence"; New = "config/Equivox" },
    @{ Old = "config\Equivalence"; New = "config\Equivox" },
    @{ Old = "Automatic-Module-Name""   : ""equivalence"""; New = "Automatic-Module-Name""   : ""equivox""" }
)

function Apply-ContentReplacements([string]$content) {
    foreach ($r in $replacements) {
        $content = $content.Replace($r.Old, $r.New)
    }

    # Protect intentional historical / credit / legacy tokens before display-name replace
    $content = $content.Replace("Bbublick (ProjectE Retexture)", "Bbublick (@@KEEP_PE_RETEXTURE@@)")
    $content = $content.Replace("ProjectE Retexture", "@@KEEP_PE_RETEXTURE@@")
    $content = $content.Replace("sinkillerj/ProjectE", "@@KEEP_UPSTREAM_REPO@@")
    $content = $content.Replace("mc-mods/projecte", "@@KEEP_CF_PROJECTE@@")
    # Protect legacy mod id string that must stay "projecte"
    $content = $content.Replace('LEGACY_MODID = "projecte"', 'LEGACY_MODID = "@@KEEP_PROJECTE_MODID@@"')
    $content = $content.Replace("LEGACY_MODID = 'projecte'", "LEGACY_MODID = '@@KEEP_PROJECTE_MODID@@'")
    # Protect projecte:* alias source mentions in comments / docs that should remain historical
    $content = $content.Replace("projecte:*", "@@KEEP_PROJECTE_STAR@@")
    $content = $content.Replace('"projecte"', '"@@KEEP_PROJECTE_MODID@@"')
    $content = $content.Replace("'projecte'", "'@@KEEP_PROJECTE_MODID@@'")

    # Temporary protect for equivalence legacy alias we will re-introduce
    # (bulk replace will turn current namespace into equivox; we restore LEGACY separately)

    $content = $content.Replace("Equivalence", "Equivox")
    $content = [regex]::Replace($content, 'modId\s*=\s*"equivalence"', 'modId="equivox"')
    $content = [regex]::Replace($content, "modId\s*=\s*'equivalence'", "modId='equivox'")
    $content = [regex]::Replace($content, '\bEQUIVOX_MODID\s*=\s*"equivalence"', 'EQUIVOX_MODID = "equivox"')
    $content = [regex]::Replace($content, "EQUIVOX_MODID\s*=\s*'equivalence'", "EQUIVOX_MODID = 'equivox'")
    $content = [regex]::Replace($content, '(?m)^(\s*)equivalence\s*\{', '${1}equivox {')
    $content = $content.Replace("mods.equivalence", "mods.equivox")
    $content = $content.Replace("neoForge.mods.equivalence", "neoForge.mods.equivox")
    $content = $content.Replace("'--mod', 'equivalence'", "'--mod', 'equivox'")
    $content = $content.Replace('"--mod", "equivalence"', '"--mod", "equivox"')
    $content = $content.Replace("archivesName = `"equivalence`"", "archivesName = `"equivox`"")
    $content = $content.Replace("archivesName = 'equivalence'", "archivesName = 'equivox'")
    $content = $content.Replace("equivalence-", "equivox-")
    # Resource namespace prefix
    $content = $content.Replace("equivalence:", "equivox:")
    $content = $content.Replace("[equivalence]", "[equivox]")
    $content = [regex]::Replace($content, '"equivalence"', '"equivox"')
    $content = [regex]::Replace($content, "'equivalence'", "'equivox'")
    # Remaining lowercase word (paths, props already handled; catch stragglers carefully)
    $content = [regex]::Replace($content, '\bequivalence\b', 'equivox')

    # Restore protected tokens
    $content = $content.Replace("@@KEEP_PE_RETEXTURE@@", "ProjectE Retexture")
    $content = $content.Replace("@@KEEP_UPSTREAM_REPO@@", "sinkillerj/ProjectE")
    $content = $content.Replace("@@KEEP_CF_PROJECTE@@", "mc-mods/projecte")
    $content = $content.Replace("@@KEEP_PROJECTE_MODID@@", "projecte")
    $content = $content.Replace("@@KEEP_PROJECTE_STAR@@", "projecte:*")
    return $content
}

$skipNames = @(
    "rebrand-equivalence.ps1",
    "rebrand-equivox.ps1",
    "Changelog.txt",
    "ChangelogMC110.txt",
    "ChangelogMC112.txt",
    "ChangelogMC18.txt",
    "ChangelogMC19.txt",
    "LICENSE"
)

$files = Get-ChildItem -Recurse -File -Include $textExt | Where-Object {
    $_.FullName -notmatch $excludeDirRegex -and
    $skipNames -notcontains $_.Name
}

$changed = 0
foreach ($f in $files) {
    $original = [System.IO.File]::ReadAllText($f.FullName)
    $updated = Apply-ContentReplacements $original
    if ($updated -ne $original) {
        [System.IO.File]::WriteAllText($f.FullName, $updated)
        $changed++
    }
}
Write-Host "Updated $changed text files" -ForegroundColor Green

Write-Host "=== Phase 4: rewrite mods.toml identity ===" -ForegroundColor Cyan
$tomlPath = "src\main\resources\META-INF\neoforge.mods.toml"
$tomlBody = @"
modLoader="javafml"
loaderVersion="`${loader_version}"
issueTrackerURL="https://github.com/Yaskulsky/projecte-26-port/issues"
license="MIT"

[[mods]]
  modId="equivox"
  version="`${version}"
  displayName="Equivox"
  displayURL="https://github.com/Yaskulsky/projecte-26-port"
  logoFile="logo.png"
  authors="Yaskulsky"
  credits="Based on MIT-licensed code originally by SinKillerJ, MaPePeR, williewillus, Lilylicious, pupnewfster et al. EE2 creator - x3n0ph0b3. Item/block retextures - Bbublick (ProjectE Retexture). Philosopher's Stone - Retro Exchange. Maintainer - Yaskulsky."
  description='''Equivox for Minecraft 26.1.2 / NeoForge. Forked from the MIT-licensed ProjectE codebase (sinkillerj/ProjectE). Not affiliated with or endorsed by the ProjectE authors.'''

[[dependencies.equivox]]
  modId="minecraft"
  type="required"
  versionRange="`${mc_version}"
  side="BOTH"
[[dependencies.equivox]]
  modId="neoforge"
  type="required"
  versionRange="`${neo_version}"
  side="BOTH"

[[dependencies.equivox]]
modId="mekanism"
type="incompatible"
reason="Requires an update to support changes to recipe mappers"
versionRange="[,10.7.8]"
side="BOTH"
"@
[System.IO.File]::WriteAllText((Join-Path $root $tomlPath), $tomlBody)

Write-Host "=== Done (bulk). Next: LegacyIds multi-alias + version bump + commands ===" -ForegroundColor Cyan
