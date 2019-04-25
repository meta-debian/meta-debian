#!/bin/bash

set -xe

IMAGENAME=deby-image
CNAME=deby
DOCKER_DIR=$(dirname $(readlink -f "$0"))
META_DEBIAN_DIR=${DOCKER_DIR}/..

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
	-v $DOCKER_DIR/.build-downloads:/home/deby/build/downloads:rw \
	-u $uid \
	--rm \
	-i \
	-d \
	--name $CNAME \
	$IMAGENAME

test -z "$TEST_TARGETS" && TEST_TARGETS="core-image-minimal"
test -z "$TEST_MACHINES" && TEST_MACHINES="qemux86"
$E "export TEMPLATECONF=meta-debian/conf; \
    source ./poky/oe-init-build-env; \
    for machine in $TEST_MACHINES; do \
      sed -i -e \"s/\(^MACHINE\s*?*=\).*/\1 \\\"\$machine\\\"/\" conf/local.conf; \
      bitbake $TEST_TARGETS; \
    done"
