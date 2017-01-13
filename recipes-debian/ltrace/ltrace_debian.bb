#
# base recipe:
#	http://cgit.openembedded.org/meta-openembedded/tree/meta-oe/recipes-devtools/ltrace/ltrace_git.bb
#

PR = "r1"

inherit debian-package
PV = "0.7.3"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=eb723b61539feef013de476e68b5c50a"

DEPENDS = "elfutils binutils"

# configure-allow-to-disable-selinux-support.patch:
#	Add option to enable/disable selinux support.
SRC_URI += "file://configure-allow-to-disable-selinux-support.patch"

inherit autotools
# Don't use selinux support
EXTRA_OECONF += "--disable-selinux"
PACKAGECONFIG[unwind] = "--with-libunwind,--without-libunwind,libunwind"

LDFLAGS += "-lstdc++"
