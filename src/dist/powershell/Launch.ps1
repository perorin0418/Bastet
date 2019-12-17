
# カレントディレクトリ取得
$currentPath = (Split-Path -Parent $MyInvocation.MyCommand.Path) + "\.."

# PATHを設定
$env:PATH = $currentPath + "\runtime\bellsoft\jdk-11.0.5\bin" + ";" + $env:PATH

# JAVA_HOMEを設定
$env:JAVA_HOME = $currentPath + "\runtime\bellsoft\jdk-11.0.5"

# 7zip自己解凍ファイル
$7zipSelfExtract = $currentPath + "\runtime\bellsoft\jdk-11.0.5.exe"
if (Test-Path $7zipSelfExtract){
    Start-Process -FilePath $7zipSelfExtract -ArgumentList "-y" -Wait
    Remove-Item ($currentPath + "\runtime\bellsoft\jdk-11.0.5.exe")
    Remove-Item ($currentPath + "\runtime\bellsoft\jdk-11.0.5.7z.*")
}

# スプラッシュ設定
$env:TAMA_CHANG_OPTS = "-splash:" + $currentPath + "\resource\splash\splash.png"

# batファイル実行
$execFile = $currentPath + "\bin\tama-chang.bat"
$file_contents = Get-Content $execFile | foreach { $_ -replace "set CLASSPATH=%APP_HOME%.*", "set CLASSPATH=%APP_HOME%\lib\*" }
$file_contents | Out-File -Encoding utf8 $execFile
Start-Process -FilePath $execFile -WindowStyle Hidden
