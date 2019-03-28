DISTRO_NAME = "Deby"
DISTRO_VERSION = "10.0"
DISTRO_CODENAME = "buster"
SDK_VENDOR = "-debysdk"
SDK_VERSION := "${DISTRO_VERSION}"

MAINTAINER = "meta-debian Development Team"
TARGET_VENDOR = "-deby"

LINUX_GIT_URI ??= "git://git.kernel.org/pub/scm/linux/kernel/git/cip"
LINUX_GIT_PROTOCOL ??= "https"
LINUX_GIT_PREFIX ??= ""
LINUX_GIT_REPO ??= "linux-cip.git"
LINUX_GIT_SRCREV ??= "linux-4.19.y-cip"

# Add an eventhandler that generates DEBIAN_SRC_URI information
# from Debian apt repository.
INHERIT += "debian-source"

# currently only Debian 10 buster is tested
# In Debian, tested distro names given by distro_identifier()
# depend on the following command and files:
# 1. lsb_release -ir => Debian-testing
# 2. /etc/lsb-release => (no package provide this file)
# 3. /etc/os-release => debian
SANITY_TESTED_DISTROS ?= " \
Debian-testing \n \
debian \n \
"

# use toolchain mode for Debian instead of the default
TCMODE ?= "deby"

# preferred providers and versions
require conf/distro/include/debian-preferred-provider.inc
