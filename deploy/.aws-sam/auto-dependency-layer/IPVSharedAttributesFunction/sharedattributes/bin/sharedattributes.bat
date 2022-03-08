@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  sharedattributes startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and SHAREDATTRIBUTES_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto execute

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\sharedattributes.jar;%APP_HOME%\lib\powertools-tracing-1.11.0.jar;%APP_HOME%\lib\lib.jar;%APP_HOME%\lib\powertools-core-1.11.0.jar;%APP_HOME%\lib\aws-lambda-java-core-1.2.1.jar;%APP_HOME%\lib\aws-lambda-java-events-3.11.0.jar;%APP_HOME%\lib\jackson-annotations-2.13.1.jar;%APP_HOME%\lib\jackson-dataformat-yaml-2.13.1.jar;%APP_HOME%\lib\aws-java-sdk-dynamodb-1.12.122.jar;%APP_HOME%\lib\aws-xray-recorder-sdk-aws-sdk-v2-instrumentor-2.11.0.jar;%APP_HOME%\lib\aws-xray-recorder-sdk-aws-sdk-v2-2.11.0.jar;%APP_HOME%\lib\aws-xray-recorder-sdk-core-2.11.0.jar;%APP_HOME%\lib\aws-java-sdk-xray-1.11.903.jar;%APP_HOME%\lib\aws-java-sdk-s3-1.12.122.jar;%APP_HOME%\lib\aws-java-sdk-kms-1.12.122.jar;%APP_HOME%\lib\aws-java-sdk-core-1.12.122.jar;%APP_HOME%\lib\jackson-dataformat-cbor-2.13.1.jar;%APP_HOME%\lib\jackson-core-2.13.1.jar;%APP_HOME%\lib\powertools-parameters-1.8.0.jar;%APP_HOME%\lib\jmespath-java-1.12.122.jar;%APP_HOME%\lib\jackson-databind-2.13.1.jar;%APP_HOME%\lib\oauth2-oidc-sdk-9.25.jar;%APP_HOME%\lib\slf4j-simple-1.7.32.jar;%APP_HOME%\lib\dynamodb-enhanced-2.17.89.jar;%APP_HOME%\lib\kms-2.17.100.jar;%APP_HOME%\lib\dynamodb-2.17.89.jar;%APP_HOME%\lib\ssm-2.17.75.jar;%APP_HOME%\lib\secretsmanager-2.17.75.jar;%APP_HOME%\lib\aws-json-protocol-2.17.100.jar;%APP_HOME%\lib\aws-core-2.17.130.jar;%APP_HOME%\lib\aspectjrt-1.9.7.jar;%APP_HOME%\lib\aws-xray-recorder-sdk-aws-sdk-core-2.11.0.jar;%APP_HOME%\lib\joda-time-2.8.1.jar;%APP_HOME%\lib\nimbus-jose-jwt-9.19.jar;%APP_HOME%\lib\jcip-annotations-1.0-1.jar;%APP_HOME%\lib\content-type-2.2.jar;%APP_HOME%\lib\json-smart-2.4.7.jar;%APP_HOME%\lib\lang-tag-1.5.jar;%APP_HOME%\lib\auth-2.17.130.jar;%APP_HOME%\lib\regions-2.17.130.jar;%APP_HOME%\lib\protocol-core-2.17.100.jar;%APP_HOME%\lib\sdk-core-2.17.130.jar;%APP_HOME%\lib\profiles-2.17.130.jar;%APP_HOME%\lib\url-connection-client-2.17.75.jar;%APP_HOME%\lib\apache-client-2.17.100.jar;%APP_HOME%\lib\netty-nio-client-2.17.100.jar;%APP_HOME%\lib\http-client-spi-2.17.130.jar;%APP_HOME%\lib\metrics-spi-2.17.130.jar;%APP_HOME%\lib\json-utils-2.17.130.jar;%APP_HOME%\lib\utils-2.17.130.jar;%APP_HOME%\lib\slf4j-api-1.7.32.jar;%APP_HOME%\lib\gson-2.8.9.jar;%APP_HOME%\lib\httpclient-4.5.13.jar;%APP_HOME%\lib\commons-lang3-3.5.jar;%APP_HOME%\lib\annotations-2.17.130.jar;%APP_HOME%\lib\eventstream-1.0.1.jar;%APP_HOME%\lib\httpcore-4.4.13.jar;%APP_HOME%\lib\commons-logging-1.2.jar;%APP_HOME%\lib\commons-codec-1.15.jar;%APP_HOME%\lib\netty-reactive-streams-http-2.0.5.jar;%APP_HOME%\lib\netty-reactive-streams-2.0.5.jar;%APP_HOME%\lib\reactive-streams-1.0.3.jar;%APP_HOME%\lib\snakeyaml-1.28.jar;%APP_HOME%\lib\accessors-smart-2.4.7.jar;%APP_HOME%\lib\ion-java-1.0.2.jar;%APP_HOME%\lib\third-party-jackson-core-2.17.130.jar;%APP_HOME%\lib\netty-codec-http2-4.1.68.Final.jar;%APP_HOME%\lib\netty-codec-http-4.1.68.Final.jar;%APP_HOME%\lib\netty-handler-4.1.68.Final.jar;%APP_HOME%\lib\netty-codec-4.1.68.Final.jar;%APP_HOME%\lib\netty-transport-native-epoll-4.1.68.Final-linux-x86_64.jar;%APP_HOME%\lib\netty-transport-native-unix-common-4.1.68.Final.jar;%APP_HOME%\lib\netty-transport-4.1.68.Final.jar;%APP_HOME%\lib\netty-buffer-4.1.68.Final.jar;%APP_HOME%\lib\netty-resolver-4.1.68.Final.jar;%APP_HOME%\lib\netty-common-4.1.68.Final.jar;%APP_HOME%\lib\asm-9.1.jar


@rem Execute sharedattributes
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %SHAREDATTRIBUTES_OPTS%  -classpath "%CLASSPATH%"  %*

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable SHAREDATTRIBUTES_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%SHAREDATTRIBUTES_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
