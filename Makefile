IMAGENAME := deby-image
CNAME := deby
POKY_REV := d94ac527b3a2bf1a8330b676513062bf699fbbe3
META_DEBIAN_DIR := $(dir $(realpath $(firstword $(MAKEFILE_LIST))))

UID := $(shell id -u)
ifeq ($(UID),0)
	UID = 1000
endif

DOCKERRUN ?= docker run \
		--env http_proxy=$(http_proxy) \
		--env https_proxy=$(https_proxy) \
		--env no_proxy=$(no_proxy) \
		--workdir /home/deby \
		-v $(META_DEBIAN_DIR):/home/deby/poky/meta-debian:rw \
		-v $(META_DEBIAN_DIR)/.build-downloads:/home/deby/build/downloads:rw \
		-u $(UID) \
		--rm \
		--name $(CNAME)

start: .build
	$(DOCKERRUN) -it $(IMAGENAME)

.build:
	-docker stop -t0 $(CNAME)
	mkdir -p $(META_DEBIAN_DIR)/.build-downloads
	docker build --build-arg http_proxy="$(http_proxy)" \
	             --build-arg https_proxy="$(https_proxy)" \
	             --build-arg no_proxy="$(no_proxy)" \
	             --build-arg POKY_REV="$(POKY_REV)" \
	             --build-arg UID="$(UID)" \
	             -t $(IMAGENAME) . && touch .build

TEST_TARGETS ?= core-image-minimal
export TEST_TARGETS

test: clean .build
	bash ./test.sh

clean:
	rm -f .build

cleanall: clean
	rm -f $(META_DEBIAN_DIR)/.build-downloads

.PHONY: start clean
