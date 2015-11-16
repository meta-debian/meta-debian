#
# base recipe:
#	http://cgit.openembedded.org/meta-openembedded/tree/meta-oe/recipes-devtools/ltrace/ltrace_git.bb
#

PR = "r0"

inherit debian-package

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=eb723b61539feef013de476e68b5c50a"

DEPENDS = "elfutils"

# configure-allow-to-disable-selinux-support.patch:
#	Add option to enable/disable selinux support.
SRC_URI += "file://configure-allow-to-disable-selinux-support.patch"

inherit autotools

PACKAGECONFIG ?= "${@base_contains('DISTRO_FEATURES', 'selinux', 'selinux', '', d)}"
PACKAGECONFIG[unwind] = "--with-libunwind,--without-libunwind,libunwind"
PACKAGECONFIG[selinux] = "--enable-selinux,--disable-selinux,libselinux"

LDFLAGS += "-lstdc++"
