UID := $(shell id -u)
ifeq ($(UID),0)
	UID = 1000
endif

export UID

start:
	docker-compose run work

test:
	docker-compose up --build test

clean:
	rm -rf ../tests/logs

cleanall:
	docker-compose rm -svf work
	docker-compose rm -svf test
	docker volume rm -f docker_downloads
	docker rmi -f deby-image

.PHONY: start test clean
