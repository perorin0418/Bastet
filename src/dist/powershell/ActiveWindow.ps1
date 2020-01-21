Param([int]$sec = 10, [int]$processId = 1)
chcp 65001
$code = @'
    [DllImport("user32.dll")]
     public static extern IntPtr GetForegroundWindow();
    [DllImport("user32.dll")]
    public static extern IntPtr GetWindowThreadProcessId(IntPtr hWnd, out int ProcessId);
'@

Add-Type $code -Name Utils -Namespace Win32
$forProcId = [IntPtr]::Zero;

while(Get-Process | Where-Object ID -eq $processId){
    $now = Get-Date -Format "yyyy/MM/dd HH:mm:ss"
    $hwnd = [Win32.Utils]::GetForegroundWindow()
    $null = [Win32.Utils]::GetWindowThreadProcessId($hwnd, [ref] $forProcId)
    $ps = Get-Process | Where-Object ID -eq $forProcId
    [String]"**********************"
    [String]$now
    [String]$ps.MainWindowTitle
    [String]$ps.Path
    Start-Sleep -s $sec
}
