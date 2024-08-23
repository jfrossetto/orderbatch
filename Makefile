# defaul shell
SHELL = /bin/bash

# Rule "help"
.PHONY: help
.SILENT: help
help:
	echo "Uso make [param]"
	echo "param:"
	echo ""
	echo "start-dev  	  - inicia docker-compose ambiente desenvolvimento "
	echo "stop-dev  	  - stop docker-compose ambiente desenvolvimento "
	echo "status-dev  	  - status docker-compose dev"
	echo ""
	echo "start-app  	  - inicia docker-compose app "
	echo "stop-app  	  - stop docker-compose app "
	echo ""
	echo "help		      - show this message"

start-dev:
	docker compose -f docker/docker-compose.yml up -d

stop-dev:
	docker compose -f docker/docker-compose.yml down

status-dev:
	docker compose -f docker/docker-compose.yml ps
	
start-app:
	docker compose -f docker/docker-compose-app.yml up -d
	
stop-app:
	docker compose -f docker/docker-compose-app.yml up -d

push-image:
	docker tag orderbatch:latest jfrossetto/orderbatch:latest
	docker push jfrossetto/orderbatch:latest

build-image:
	./gradlew clean build
	docker rmi -f jfrossetto/orderbatch:latest
	docker build --force-rm -t orderbatch:latest .

kill-nones:
	docker images | grep none | awk '{print $3}' | xargs docker rmi
