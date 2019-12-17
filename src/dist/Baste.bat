@echo off
cd /d %~dp0

powershell -NoProfile -ExecutionPolicy Unrestricted -windowstyle hidden .\powershell\Launch.ps1
