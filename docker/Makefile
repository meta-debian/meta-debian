UID := $(shell id -u)
ifeq ($(UID),0)
	UID = 1000
endif

export UID
ifneq ("$(shell which docker-compose)", "")
	DOCKER_COMPOSE=docker-compose
else
	DOCKER_COMPOSE=docker compose
endif

start:
	$(DOCKER_COMPOSE) run work

refresh:
	# Rebuild docker image
	$(DOCKER_COMPOSE) build --no-cache work

test: build_test qemu_ptest

build_test:
	$(DOCKER_COMPOSE) run --rm build_test

qemu_ptest:
	$(DOCKER_COMPOSE) run --rm qemu_ptest

clean:
	rm -rf ../tests/logs
	rm -rf ../tests/html

cleanall:
	$(DOCKER_COMPOSE) rm -svf work
	$(DOCKER_COMPOSE) rm -svf build_test
	$(DOCKER_COMPOSE) rm -svf qemu_ptest
	docker volume rm -f docker_downloads
	docker rmi -f deby-image

.PHONY: refresh start test build_test qemu_ptest clean cleanall
