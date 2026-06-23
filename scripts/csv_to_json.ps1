param(
    [string]$csvPath = "scripts/indonesian_food_nutrition.csv",
    [string]$jsonPath = "app/src/main/assets/food_data.json"
)

$ProjectRoot = Split-Path -Parent $PSScriptRoot
$csvFile = Join-Path $ProjectRoot $csvPath
$jsonFile = Join-Path $ProjectRoot $jsonPath

if (-not (Test-Path $csvFile)) {
    Write-Error "CSV file not found: $csvFile"
    exit 1
}

Write-Host "Reading CSV from $csvFile ..."
$data = Import-Csv $csvFile -Encoding UTF8 | ForEach-Object {
    [PSCustomObject]@{
        id         = [int]$_.id
        name       = $_.name
        calories   = [float]$_.calories
        proteins   = [float]$_.proteins
        fat        = [float]$_.fat
        carbs      = [float]$_.carbohydrate
        image      = $_.image
    }
}

$json = $data | ConvertTo-Json -Compress
Set-Content -Path $jsonFile -Value $json -Encoding UTF8
Write-Host "JSON written to $jsonFile ($($data.Count) items)"
