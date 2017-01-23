#
# base recipe: http://cgit.openembedded.org/cgit.cgi/meta-openembedded/
# tree/meta-oe/recipes-devtools/libedit/libedit_20150325-3.1.bb?h=master
# base branch: master
#

PR = "r0"

inherit debian-package
PV = "3.1-20140620"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=1e4228d0c5a9093b01aeaaeae6641533"

DEPENDS = "ncurses libbsd"

inherit autotools pkgconfig

do_install_append() {
	ln -sf readline.h ${D}${includedir}/editline/history.h
}

DEBIANNAME_${PN}-dbg = "libedit2-dbg"

FILE_${PN}-dev += "${includedir}/editline/history.h"
