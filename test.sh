#!/bin/bash

set -xe

IMAGENAME=deby-image
CNAME=deby
META_DEBIAN_DIR=$(dirname $(readlink -f "$0"))

uid=1000
if [ "$UID" != "0" ]; then
	uid=$UID
fi

E="docker exec -u $uid $CNAME /bin/bash -c "

docker run \
	--env http_proxy="$http_proxy" \
	--env https_proxy="$https_proxy" \
	--env no_proxy="$no_proxy" \
	--workdir /home/deby \
	--cap-add SYS_ADMIN \
	-v $META_DEBIAN_DIR:/home/deby/poky/meta-debian:rw \
	-v $META_DEBIAN_DIR/.build-downloads:/home/deby/build/downloads:rw \
	-u $uid \
	--rm \
	-i \
	-d \
	--name $CNAME \
	$IMAGENAME

test -z "$TEST_TARGETS" && TEST_TARGETS="core-image-minimal"
$E "export TEMPLATECONF=meta-debian/conf; \
    source ./poky/oe-init-build-env; \
    bitbake $TEST_TARGETS"
