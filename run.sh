#!/bin/sh
set -e # Exit early if any commands fail
(
  cd "$(dirname "$0")" # Ensure compile steps are run within the repository directory
  mvn -B package -Ddir=/tmp/codecrafters-build-http-server-java
)
exec java -jar /tmp/codecrafters-build-http-server-java/java_http.jar "$@"
