#!/bin/sh

echo "building the app"
echo "mvn compile jib:dockerBuild"
mvn compile jib:dockerBuild

echo "running the app"
echo "docker-compose up"
exec docker-compose up