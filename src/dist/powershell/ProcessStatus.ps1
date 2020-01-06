$psArray = [System.Diagnostics.Process]::GetProcesses()
foreach ($ps in $psArray){
    [String]$ps.ProcessName + " : " + $ps.MainWindowTitle + " : " + $ps.Path + " : " + $ps.CPU + " : " + $ps.Description
}
