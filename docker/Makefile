UID := $(shell id -u)
ifeq ($(UID),0)
	UID = 1000
endif

export UID

start:
	docker-compose run work

test: build_test ptest

build_test:
	docker-compose up --build build_test

ptest:
	docker-compose up --build ptest

clean:
	rm -rf ../tests/logs

cleanall:
	docker-compose rm -svf work
	docker-compose rm -svf build_test
	docker-compose rm -svf ptest
	docker volume rm -f docker_downloads
	docker rmi -f deby-image

.PHONY: start test build_test ptest clean cleanall
