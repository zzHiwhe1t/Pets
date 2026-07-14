$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$pidDir = Join-Path $root ".pids"

if (-not (Test-Path -LiteralPath $pidDir)) {
    Write-Host "没有找到微服务进程记录。"
    exit 0
}

Get-ChildItem -LiteralPath $pidDir -Filter "*.pid" | ForEach-Object {
    $processId = [int](Get-Content -LiteralPath $_.FullName)
    $process = Get-Process -Id $processId -ErrorAction SilentlyContinue
    if ($null -ne $process) {
        Stop-Process -Id $processId
        Write-Host ("已停止 PID {0}" -f $processId)
    }
    Remove-Item -LiteralPath $_.FullName -Force
}
