$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$logDir = Join-Path $root "logs"
$pidDir = Join-Path $root ".pids"
New-Item -ItemType Directory -Force -Path $logDir, $pidDir | Out-Null

$services = @(
    @{ Name = "eureka"; Jar = "pet-cloud-eureka\target\pet-cloud-eureka-2.0.0.jar"; Delay = 5 },
    @{ Name = "user"; Jar = "pet-user-service\target\pet-user-service-2.0.0.jar"; Delay = 2 },
    @{ Name = "pet"; Jar = "pet-pet-service\target\pet-pet-service-2.0.0.jar"; Delay = 2 },
    @{ Name = "interaction"; Jar = "pet-interaction-service\target\pet-interaction-service-2.0.0.jar"; Delay = 2 },
    @{ Name = "gateway"; Jar = "pet-cloud-gateway\target\pet-cloud-gateway-2.0.0.jar"; Delay = 0 }
)

foreach ($service in $services) {
    $jar = Join-Path $root $service.Jar
    if (-not (Test-Path -LiteralPath $jar)) {
        throw "缺少 $jar，请先在 pet-adopt-server 执行 mvn clean package。"
    }
    $stdout = Join-Path $logDir ($service.Name + ".log")
    $stderr = Join-Path $logDir ($service.Name + "-error.log")
    $process = Start-Process -FilePath "java" -ArgumentList "-jar", $jar -WorkingDirectory $root -WindowStyle Hidden -RedirectStandardOutput $stdout -RedirectStandardError $stderr -PassThru
    Set-Content -Encoding ASCII -Path (Join-Path $pidDir ($service.Name + ".pid")) -Value $process.Id
    Write-Host ("已启动 {0}，PID {1}" -f $service.Name, $process.Id)
    if ($service.Delay -gt 0) { Start-Sleep -Seconds $service.Delay }
}

Write-Host "全部服务已启动。API: http://localhost:8080/api，Eureka: http://localhost:8761"
