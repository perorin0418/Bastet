
$DateTime=Get-Date (Get-Date).Datetime  -Format yyyyMMdd_HHmmss
Start-Transcript (".\log\powershell_" + $DateTime + ".log")

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

# 専用のjava.exeに置き換え
$cutomizedJava = $currentPath + "\runtime\bellsoft\java.exe"
$originalJava = $currentPath + "\runtime\bellsoft\jdk-11.0.5\bin\java.exe"
Copy-Item -Force $cutomizedJava $originalJava

# スプラッシュ設定
$env:BASTET_OPTS = $env:BASTET_OPTS + " -splash:" + $currentPath + "\resource\splash\splash.png -Dfile.encoding=UTF-8"

# batファイル実行
$execFile = $currentPath + "\bin\Bastet.bat"
$file_contents = Get-Content $execFile | foreach { $_ -replace "set CLASSPATH=%APP_HOME%.*", "set CLASSPATH=%APP_HOME%\lib\*" }
$file_contents | Out-File -Encoding utf8 $execFile
Copy-Item -Force ".\log\stdlog" (".\log\" + $DateTime + "_std.log")
Copy-Item -Force ".\log\errlog" (".\log\" + $DateTime + "_err.log")

Start-Process -FilePath $execFile -WindowStyle Hidden -RedirectStandardOutput log\stdlog -RedirectStandardError log\errlog
Stop-Transcript
