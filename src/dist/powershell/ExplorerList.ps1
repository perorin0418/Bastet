Param([int]$sec = 10, [int]$processId = 1)
chcp 65001
$app = New-Object -com "Shell.Application"
while(Get-Process | Where-Object ID -eq $processId){
    $now = Get-Date -Format "yyyy/MM/dd HH:mm:ss"
    $psArr = $app.windows()
    foreach ($ps in $psArr){
        if($ps.LocationName -ne "about:blank"){
            [String]"**********************"
            [String]$now
            [String]$ps.LocationName
            [String]$ps.LocationURL
        }
    }
    Start-Sleep -s $sec
}
