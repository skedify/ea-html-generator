@echo off

set dirpath=C:\inetpub\
set dirname=wwwroot
set tmpdir=%dirpath%%dirname%-tmp
set host=REPLACE_WITH_HOST
set port=REPLACE_WITH_PORT
set datasource=REPLACE_WITH_DATASOURCE
set dsn=REPLACE_WITH_DSN
set username=REPLACE_WITH_USERNAME
set password=REPLACE_WITH_PASSWORD
set package=REPLACE_WITH_PACKAGE

:: Currently unused, as this script doesn't seem to need the license key?
:: This allows the user to automatically use the provided license key, at the end of the strict we undo this setting
:: reg add "HKCU\Software\Sparx Systems\EA400\EA\OPTIONS" /v AutoCheckoutSharedKeyArray /t REG_BINARY /d 02 /f
:: reg add "HKCU\Software\Sparx Systems\EA400\EA\OPTIONS" /v SharedKeyFolder /t REG_SZ /d "C:\Program Files (x86)\Sparx Systems\Keystore\Service" /f

echo Creating temporary directory: %tmpdir%
md %tmpdir%

echo Generating EA HTML
java -jar %~dp0ea-html-generator-1.1.0.jar --input "REPLACE_WITH_CONNECTION_NAME --- ;Connect=Cloud=protocol:http,address:%host%,port:%port%;Data Source=%datasource%;DSN=%dsn%;" --username %username% --password %password% --package %package% --output %tmpdir%

if %errorlevel% neq 0 (
  exit %errorlevel%
)

echo Replacing HTML in directory: %dirpath%%dirname%
move %dirpath%%dirname%\web.config %dirpath%web.config.tmp
rd /S /Q %dirpath%%dirname%
ren %tmpdir% %dirname%
move %dirpath%web.config.tmp %dirpath%%dirname%\web.config

:: Currently unused, as this script doesn't seem to need the license key?
:: This just sets it back to the default, but doesn't release the license key
:: reg delete "HKCU\Software\Sparx Systems\EA400\EA\OPTIONS" /v AutoCheckoutSharedKeyArray /f
:: reg delete "HKCU\Software\Sparx Systems\EA400\EA\OPTIONS" /v SharedKeyFolder /f
