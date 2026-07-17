# Rebrand ProjectE/ProjectEE fork -> Equivalence (modId: equivalence)
# Run from repo root: .\scripts\rebrand-equivalence.ps1
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
    @{ From = "src\api\java\moze_intel\projecte"; To = "src\api\java\com\yaskulsky\equivalence" },
    @{ From = "src\main\java\moze_intel\projecte"; To = "src\main\java\com\yaskulsky\equivalence" },
    @{ From = "src\datagen\java\moze_intel\projecte"; To = "src\datagen\java\com\yaskulsky\equivalence" },
    @{ From = "src\test\java\moze_intel\projecte"; To = "src\test\java\com\yaskulsky\equivalence" },
    @{ From = "buildSrc\src\main\groovy\moze_intel\projecte"; To = "buildSrc\src\main\groovy\com\yaskulsky\equivalence" },
    @{ From = "src\main\resources\assets\projecte"; To = "src\main\resources\assets\equivalence" },
    @{ From = "src\main\resources\data\projecte"; To = "src\main\resources\data\equivalence" },
    @{ From = "src\datagen\generated\assets\projecte"; To = "src\datagen\generated\assets\equivalence" },
    @{ From = "src\datagen\generated\data\projecte"; To = "src\datagen\generated\data\equivalence" }
)

foreach ($m in $moves) {
    Move-Tree $m.From $m.To
    Remove-EmptyParents (Split-Path $m.From -Parent)
}

# META-INF services filenames
$svcDir = "src\main\resources\META-INF\services"
if (Test-Path $svcDir) {
    Get-ChildItem $svcDir -File | ForEach-Object {
        if ($_.Name -like "moze_intel.projecte.*") {
            $newName = $_.Name.Replace("moze_intel.projecte.", "com.yaskulsky.equivalence.")
            Rename-Item -LiteralPath $_.FullName -NewName $newName
            Write-Host "RENAMED service $($_.Name) -> $newName" -ForegroundColor Green
        }
    }
}

Write-Host "=== Phase 2: rename ProjectE* source files ===" -ForegroundColor Cyan
$fileRenames = @{
    "ProjectEAPI.java"            = "EquivalenceAPI.java"
    "ProjectERegistries.java"     = "EquivalenceRegistries.java"
    "ProjectEConfig.java"         = "EquivalenceConfig.java"
    "ProjectEDataGenerator.java"  = "EquivalenceDataGenerator.java"
    "ProjectEEmiDefaults.java"    = "EquivalenceEmiDefaults.java"
    "ProjectEAliases.java"        = "EquivalenceAliases.java"
    "ProjectEAliasMapping.java"   = "EquivalenceAliasMapping.java"
    "ProjectETNT.java"            = "EquivalenceTNT.java"
    "ProjectEProxyTest.java"      = "EquivalenceProxyTest.java"
    "ProjectEClassInit.java"      = "EquivalenceClassInit.java"
    "projecte.json"               = "equivalence.json"  # curios entities
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
    @{ Old = "ProjectEAliasMapping"; New = "EquivalenceAliasMapping" },
    @{ Old = "ProjectEAliases"; New = "EquivalenceAliases" },
    @{ Old = "ProjectEClassInit"; New = "EquivalenceClassInit" },
    @{ Old = "ProjectEDataGenerator"; New = "EquivalenceDataGenerator" },
    @{ Old = "ProjectEEmiDefaults"; New = "EquivalenceEmiDefaults" },
    @{ Old = "ProjectEProxyTest"; New = "EquivalenceProxyTest" },
    @{ Old = "ProjectERegistries"; New = "EquivalenceRegistries" },
    @{ Old = "ProjectEConfig"; New = "EquivalenceConfig" },
    @{ Old = "ProjectETNT"; New = "EquivalenceTNT" },
    @{ Old = "ProjectEAPI"; New = "EquivalenceAPI" },
    @{ Old = "PROJECTE_MODID"; New = "EQUIVALENCE_MODID" },
    @{ Old = "ProjectEE"; New = "Equivalence" },
    @{ Old = "moze_intel.projecte"; New = "com.yaskulsky.equivalence" },
    @{ Old = "moze_intel/projecte"; New = "com/yaskulsky/equivalence" },
    @{ Old = "assets/projecte"; New = "assets/equivalence" },
    @{ Old = "assets\projecte"; New = "assets\equivalence" },
    @{ Old = "data/projecte"; New = "data/equivalence" },
    @{ Old = "data\projecte"; New = "data\equivalence" },
    @{ Old = "dependencies.projecte"; New = "dependencies.equivalence" },
    @{ Old = "projecte_datagen"; New = "equivalence_datagen" },
    @{ Old = "projecte_version"; New = "equivalence_version" },
    @{ Old = "projecte.skip_top"; New = "equivalence.skip_top" },
    @{ Old = "projecte:plugin"; New = "equivalence:plugin" },
    @{ Old = "Automatic-Module-Name""   : ""projecte"""; New = "Automatic-Module-Name""   : ""equivalence""" }
)

# Additional careful replacements applied with regex on content
function Apply-ContentReplacements([string]$content) {
    foreach ($r in $replacements) {
        $content = $content.Replace($r.Old, $r.New)
    }
    # Display / leftover ProjectE product name -> Equivalence (docs + lang values + comments)
    # Keep attribution phrases that name the upstream texture pack / historical notes via protected tokens
    $content = $content.Replace("Bbublick (ProjectE Retexture)", "Bbublick (@@KEEP_PE_RETEXTURE@@)")
    $content = $content.Replace("ProjectE Retexture", "@@KEEP_PE_RETEXTURE@@")
    $content = $content.Replace("sinkillerj/ProjectE", "@@KEEP_UPSTREAM_REPO@@")
    $content = $content.Replace("mc-mods/projecte", "@@KEEP_CF_PROJECTE@@")

    $content = $content.Replace("ProjectE", "Equivalence")
    # modId string literals and neoForge DSL identifiers
    $content = [regex]::Replace($content, 'modId\s*=\s*"projecte"', 'modId="equivalence"')
    $content = [regex]::Replace($content, "modId\s*=\s*'projecte'", "modId='equivalence'")
    $content = [regex]::Replace($content, '\bEQUIVALENCE_MODID\s*=\s*"projecte"', 'EQUIVALENCE_MODID = "equivalence"')
    $content = [regex]::Replace($content, "EQUIVALENCE_MODID\s*=\s*'projecte'", "EQUIVALENCE_MODID = 'equivalence'")
    # neoForge mods { projecte { -> already handled if ProjectE replaced? DSL uses lowercase projecte
    $content = [regex]::Replace($content, '(?m)^(\s*)projecte\s*\{', '${1}equivalence {')
    $content = $content.Replace("mods.projecte", "mods.equivalence")
    $content = $content.Replace("neoForge.mods.projecte", "neoForge.mods.equivalence")
    $content = $content.Replace("'--mod', 'projecte'", "'--mod', 'equivalence'")
    $content = $content.Replace('"--mod", "projecte"', '"--mod", "equivalence"')
    $content = $content.Replace("archivesName = `"projectee`"", "archivesName = `"equivalence`"")
    $content = $content.Replace("archivesName = 'projectee'", "archivesName = 'equivalence'")
    $content = $content.Replace("projectee-", "equivalence-")
    $content = $content.Replace("projectee", "equivalence")
    # Resource namespace prefix (item/block ids, tags, etc.)
    $content = $content.Replace("projecte:", "equivalence:")
    $content = $content.Replace("[projecte]", "[equivalence]")
    # Namespace string "projecte" used as mod id / resource namespace (common patterns)
    $content = [regex]::Replace($content, '"projecte"', '"equivalence"')
    $content = [regex]::Replace($content, "'projecte'", "'equivalence'")

    # Restore protected tokens
    $content = $content.Replace("@@KEEP_PE_RETEXTURE@@", "ProjectE Retexture")
    $content = $content.Replace("@@KEEP_UPSTREAM_REPO@@", "sinkillerj/ProjectE")
    $content = $content.Replace("@@KEEP_CF_PROJECTE@@", "mc-mods/projecte")
    # Restore intentional upstream-name mentions in legal/credit sentences after ProjectE->Equivalence
    $content = $content.Replace("@@KEEP_PROJECTE_NAME@@", "ProjectE")
    return $content
}

$files = Get-ChildItem -Recurse -File -Include $textExt | Where-Object {
    $_.FullName -notmatch $excludeDirRegex -and
    $_.Name -ne "rebrand-equivalence.ps1"
}

$changed = 0
foreach ($f in $files) {
    # Skip ancient upstream changelog dumps (historical)
    if ($f.Name -in @("Changelog.txt", "ChangelogMC110.txt")) { continue }

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
  modId="equivalence"
  version="`${version}"
  displayName="Equivalence"
  displayURL="https://github.com/Yaskulsky/projecte-26-port"
  logoFile="logo.png"
  authors="Yaskulsky"
  credits="Based on MIT-licensed code originally by SinKillerJ, MaPePeR, williewillus, Lilylicious, pupnewfster et al. EE2 creator - x3n0ph0b3. Item/block retextures - Bbublick (ProjectE Retexture). Philosopher's Stone - Retro Exchange. Maintainer - Yaskulsky."
  description='''Equivalence for Minecraft 26.1.2 / NeoForge. Forked from the MIT-licensed ProjectE codebase (sinkillerj/ProjectE). Not affiliated with or endorsed by the ProjectE authors.'''

[[dependencies.equivalence]]
  modId="minecraft"
  type="required"
  versionRange="`${mc_version}"
  side="BOTH"
[[dependencies.equivalence]]
  modId="neoforge"
  type="required"
  versionRange="`${neo_version}"
  side="BOTH"

[[dependencies.equivalence]]
modId="mekanism"
type="incompatible"
reason="Requires an update to support changes to recipe mappers"
versionRange="[,10.7.8]"
side="BOTH"
"@
[System.IO.File]::WriteAllText((Join-Path $root $tomlPath), $tomlBody)

Write-Host "=== Done ===" -ForegroundColor Cyan
Write-Host "Next: review git diff, bump version if desired, then .\gradlew.bat build -x test"
