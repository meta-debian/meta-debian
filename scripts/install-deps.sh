#!/bin/sh
# Install packages required to use poky and meta-debian

if [ "$(whoami)" != "root" ]; then
	echo "Please run this script as root"
	exit 1
fi

# https://www.yoctoproject.org/docs/2.2/ref-manual/ref-manual.html#required-git-tar-and-python-versions
PKG_ESSENTIALS="git tar python3"

# "Essentials" in https://www.yoctoproject.org/docs/2.2/ref-manual/ref-manual.html#ubuntu-packages
PKG_ESSENTIALS_DEBIAN=" \
	gawk wget git-core diffstat unzip texinfo gcc-multilib \
	build-essential chrpath socat \
	"

# In addition to the above, the following packages are required:
#   cpio: listed in SANITY_REQUIRED_UTILITIES is missing in the manual above
#   python: pseudo-native requires `/usr/bin/env python` as python2.7
PKG_EXTRA="cpio python"

apt-get install ${@} ${PKG_ESSENTIALS} ${PKG_ESSENTIALS_DEBIAN} ${PKG_EXTRA}
