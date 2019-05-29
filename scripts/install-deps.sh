#!/bin/sh
# Install packages defined as the "Essentials" in
# https://www.yoctoproject.org/docs/2.7/ref-manual/ref-manual.html#ubuntu-packages

if [ "$(whoami)" != "root" ]; then
	echo "Please run this script as root"
	exit 1
fi

apt-get install gawk wget git-core diffstat unzip texinfo gcc-multilib \
build-essential chrpath socat cpio python python3 python3-pip python3-pexpect \
xz-utils debianutils iputils-ping python3-git python3-jinja2 libegl1-mesa libsdl1.2-dev \
xterm
