#!/bin/sh
# Install packages required to use poky and meta-debian

if [ "$(whoami)" != "root" ]; then
	echo "Please run this script as root"
	exit 1
fi

# https://www.yoctoproject.org/docs/2.7/ref-manual/ref-manual.html#required-git-tar-and-python-versions
PKG_ESSENTIALS="git tar python3"

# "Essentials" in https://www.yoctoproject.org/docs/2.7/ref-manual/ref-manual.html#ubuntu-packages
PKG_ESSENTIALS_DEBIAN=" \
	gawk wget git-core diffstat unzip texinfo gcc-multilib \
	build-essential chrpath socat cpio python python3 python3-pip \
	python3-pexpect xz-utils debianutils iputils-ping python3-git \
	python3-jinja2 libegl1-mesa libsdl1.2-dev xterm python3-debian \
	"

# No extra package is required at the moment
PKG_EXTRA=""

apt-get install ${@} ${PKG_ESSENTIALS} ${PKG_ESSENTIALS_DEBIAN} ${PKG_EXTRA}
