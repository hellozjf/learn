call mvn clean package -DskipTests
call mkdir bin
call copy src\main\resources\* bin
call copy target\test-ftp-1.0.4.jar bin
call mkdir bin\upload