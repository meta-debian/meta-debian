#
# Base recipe: meta/recipes-graphics/xorg-lib/libpciaccess_0.13.2.bb
# Base branch: daisy
#

SUMMARY = "Generic PCI access library for X"

DESCRIPTION = "libpciaccess provides functionality for X to access the \
PCI bus and devices in a platform-independent way."

require xorg-lib-common.inc
PV = "0.13.2"

PR = "${INC_PR}.0"

LICENSE = "MIT & MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=277aada5222b9a22fbf3471ff3687068"

# remove required X11 in xorg-lib-common.inc
REQUIRED_DISTRO_FEATURES = ""

# There is not debian patch file
DEBIAN_PATCH_TYPE = "nopatch"
