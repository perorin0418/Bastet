
Param($FolderPath)

# ユーザー名取得
$UserInfo = [System.Security.Principal.WindowsIdentity]::GetCurrent()
$DomainUserName = $UserInfo.Name

# フォルダーのパス
$FolderPath = $FolderPath + "\" + $DomainUserName
# フォルダー作成
New-Item $FolderPath -ItemType Directory
# フォルダ権限を取得
$ACL = Get-Acl $FolderPath
# 権限の設定(引数:ユーザー名,アクセス権,下位フォルダ継承,下位オブジェクト継承,継承の制限,アクセス許可)
$Permission = ($DomainUserName,"FullControl","None,NONE","None","Allow")
# 権限の設定を反映
$AccessRule = New-Object System.Security.AccessControl.FileSystemAccessRule $Permission
$ACL.SetAccessRuleProtection($true,$false)
$ACL.SetAccessRule($AccessRule)
$ACL | Set-Acl $FolderPath
Write-Host $FolderPath
