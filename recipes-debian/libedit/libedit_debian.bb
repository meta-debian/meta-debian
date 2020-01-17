#
# recipe base: meta-openembedded/meta-oe/recipes-devtools/libedit/libedit_20190324-3.1.bb
# branch: warrior
SUMMARY = "BSD editline and history libraries"
DESCRIPTION = "Command line editor library provides generic line editing, \
history, and tokenization functions. \
\
It slightly resembles GNU readline."
HOMEPAGE = "https://www.thrysoee.dk/editline/"

inherit debian-package
require recipes-debian/sources/libedit.inc

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=1e4228d0c5a9093b01aeaaeae6641533"

DEPENDS = "ncurses libbsd groff"

inherit pkgconfig autotools
DEBIAN_UNPACK_DIR = "${WORKDIR}/libedit-20181209-3.1"

EXTRA_OECONF += "LIBBSD_CFLAGS="-isystem ${STAGING_INCDIR}/bsd -DLIBBSD_OVERLAY""

do_install_prepend (){
	install -d ${D}${includedir}/editline
	ln -sf ${includedir}/editline/readline.h ${D}${includedir}/editline/history.h
}
