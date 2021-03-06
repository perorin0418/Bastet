Param([int]$sec = 10, [int]$processId = 1)
chcp 65001
while(Get-Process | Where-Object ID -eq $processId){
    $now = Get-Date -Format "yyyy/MM/dd HH:mm:ss"
    $psArray = [System.Diagnostics.Process]::GetProcesses()
    foreach ($ps in $psArray){
        if($ps.MainWindowTitle -ne ""){
            [String]"**********************"
            [String]$now
            [String]$ps.MainWindowTitle
            [String]$ps.Path
        }
    }
    Start-Sleep -s $sec
}
