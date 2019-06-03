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

.PHONY: start test clean
