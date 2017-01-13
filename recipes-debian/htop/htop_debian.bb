#
# base recipe: http://cgit.openembedded.org/meta-openembedded/tree/meta-oe/recipes-support/htop/htop_1.0.3.bb
#

SUMMARY = "htop process monitor"
HOMEPAGE = "http://htop.sf.net"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=c312653532e8e669f30e5ec8bdc23be3"

PR = "r0"
inherit debian-package
PV = "1.0.3"

DEBIAN_PATCH_TYPE = "quilt"

DEPENDS = "ncurses"
RDEPENDS_${PN} = "ncurses-term"

LDFLAGS_append_libc-uclibc = " -lubacktrace"

inherit autotools

# Enable configuration base on debian/rules
EXTRA_OECONF += " --enable-taskstats --enable-openvz --enable-vserver \
		--enable-cgroup --enable-native-affinity"
