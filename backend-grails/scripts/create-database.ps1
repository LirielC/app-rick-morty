param(
    [string]$DbUsername = "root",
    [string]$DbPassword = "",
    [string]$DatabaseName = "rick_morty_app"
)

$mysqlAdminPath = "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqladmin.exe"

if (-not (Test-Path $mysqlAdminPath)) {
    throw "Cliente mysqladmin não encontrado em 'C:\Program Files\MySQL\MySQL Server 8.0\bin'."
}

$passwordArg = if ([string]::IsNullOrWhiteSpace($DbPassword)) { @() } else { @("-p$DbPassword") }

& $mysqlAdminPath -u $DbUsername @passwordArg create $DatabaseName 2>$null

Write-Host "Banco '$DatabaseName' validado/criado com sucesso."
