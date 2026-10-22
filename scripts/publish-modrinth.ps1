<#
.SYNOPSIS
    Uploads the DamageTilt jars to the Modrinth project as new versions.

.DESCRIPTION
    Every jar named damagetilt-<mod version>-<minecraft version>.jar becomes one
    Modrinth version (loader: fabric, game version: <minecraft version>).
    Versions whose version number already exists on the project are skipped.

    Token: uses $env:MODRINTH_TOKEN when set, otherwise asks for it (input is hidden).
    Create a token at https://modrinth.com/settings/pats with the scope
    "Create versions" (and "Read versions" if you want to see existing ones).

.PARAMETER JarDir
    Folder that contains the jars, e.g. the extracted "damagetilt-all-jars" artifact.

.PARAMETER Changelog
    Changelog text used for every uploaded version.

.PARAMETER ProjectId
    Modrinth project ID. Defaults to the DamageTilt project (zgg7zTBK).

.PARAMETER DryRun
    Shows what would be uploaded without sending anything to Modrinth.

.EXAMPLE
    .\scripts\publish-modrinth.ps1 -JarDir "C:\Users\mukse\Downloads\damagetilt-all-jars" -DryRun

.EXAMPLE
    .\scripts\publish-modrinth.ps1 -JarDir "C:\Users\mukse\Downloads\damagetilt-all-jars" -Changelog "Updated for 1.21.9-26.3"
#>
param(
    [Parameter(Mandatory = $true)]
    [string] $JarDir,

    [string] $Changelog = "",

    [string] $ProjectId = "zgg7zTBK",

    [switch] $DryRun
)

$ErrorActionPreference = 'Stop'

$api        = 'https://api.modrinth.com/v2'
$userAgent  = 'ErikEdits/damagetilt-publisher/1.0 (https://github.com/ErikEdits/damagetilt-mod)'
$fabricApiId = 'P7dR8mSH'   # Fabric API on Modrinth
$modMenuId   = 'mOgUt4GM'   # Mod Menu on Modrinth

# -- Token --------------------------------------------------------------------
$token = $env:MODRINTH_TOKEN
if (-not $DryRun -and -not $token) {
    $secure = Read-Host 'Modrinth token (input is hidden)' -AsSecureString
    $token = [System.Net.NetworkCredential]::new('', $secure).Password
}
if (-not $DryRun -and -not $token) {
    throw 'No Modrinth token given.'
}

# -- Jars ---------------------------------------------------------------------
if (-not (Test-Path -LiteralPath $JarDir -PathType Container)) {
    throw "Jar folder not found: $JarDir"
}

$namePattern = '^damagetilt-(?<mod>\d+\.\d+\.\d+)-(?<mc>\d+(\.\d+)+)\.jar$'
$jars = Get-ChildItem -LiteralPath $JarDir -Filter 'damagetilt-*.jar' |
    Where-Object { $_.Name -match $namePattern } |
    Sort-Object Name

if (-not $jars) {
    throw "No damagetilt-<mod>-<mc>.jar files found in $JarDir"
}

# -- Existing versions (skip duplicates) --------------------------------------
$existing = Invoke-RestMethod -Uri "$api/project/$ProjectId/version" -Headers @{ 'User-Agent' = $userAgent }
$existingNumbers = @($existing | ForEach-Object { $_.version_number })

Add-Type -AssemblyName System.Net.Http
$client = New-Object System.Net.Http.HttpClient
$null = $client.DefaultRequestHeaders.TryAddWithoutValidation('User-Agent', $userAgent)
if ($token) {
    $null = $client.DefaultRequestHeaders.TryAddWithoutValidation('Authorization', $token)
}

$failed = 0
foreach ($jar in $jars) {
    $null = $jar.Name -match $namePattern
    $modVersion = $Matches['mod']
    $mc         = $Matches['mc']
    $versionNumber = "$modVersion-$mc"

    if ($existingNumbers -contains $versionNumber) {
        Write-Host "[skip]   $versionNumber already exists on Modrinth" -ForegroundColor Yellow
        continue
    }

    $notes = if ($Changelog) { $Changelog } else { "DamageTilt $modVersion for Minecraft $mc." }

    $metadata = [ordered]@{
        name           = "DamageTilt $modVersion for Minecraft $mc"
        version_number = $versionNumber
        changelog      = $notes
        dependencies   = @(
            @{ project_id = $fabricApiId; dependency_type = 'required' }
            @{ project_id = $modMenuId;   dependency_type = 'optional' }
        )
        game_versions  = @($mc)
        version_type   = 'release'
        loaders        = @('fabric')
        featured       = $false
        status         = 'listed'
        project_id     = $ProjectId
        file_parts     = @('file')
    }
    $json = $metadata | ConvertTo-Json -Depth 5 -Compress

    if ($DryRun) {
        Write-Host "[dry-run] $($jar.Name) -> $versionNumber (MC $mc)" -ForegroundColor Cyan
        continue
    }

    try {
        $form = New-Object System.Net.Http.MultipartFormDataContent

        $dataPart = New-Object System.Net.Http.StringContent($json, [System.Text.Encoding]::UTF8, 'application/json')
        $form.Add($dataPart, 'data')

        $bytes = [System.IO.File]::ReadAllBytes($jar.FullName)
        $filePart = New-Object System.Net.Http.ByteArrayContent -ArgumentList (, $bytes)
        $filePart.Headers.ContentType = [System.Net.Http.Headers.MediaTypeHeaderValue]::Parse('application/java-archive')
        $form.Add($filePart, 'file', $jar.Name)

        $response = $client.PostAsync("$api/version", $form).GetAwaiter().GetResult()
        $body = $response.Content.ReadAsStringAsync().GetAwaiter().GetResult()

        if (-not $response.IsSuccessStatusCode) {
            throw "HTTP $([int]$response.StatusCode): $body"
        }

        $created = $body | ConvertFrom-Json
        Write-Host "[ok]     $versionNumber -> https://modrinth.com/mod/damage-tilt/version/$($created.id)" -ForegroundColor Green
    }
    catch {
        $failed++
        Write-Host "[error]  $versionNumber : $($_.Exception.Message)" -ForegroundColor Red
    }
}

$client.Dispose()

if ($failed -gt 0) {
    Write-Host "$failed upload(s) failed." -ForegroundColor Red
    exit 1
}
Write-Host 'Done.' -ForegroundColor Green
