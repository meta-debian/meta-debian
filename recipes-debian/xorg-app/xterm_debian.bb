#
# debian
#
SUMMARY = "X terminal emulator"

DESCRIPTION = "xterm is a terminal emulator for the X Window System. \
It provides DEC VT102 and Tektronix 4014 compatible terminals for programs \
that cannot use the window system directly.  This version implements ISO/ANSI \
colors and most of the control sequences used by DEC VT220 terminals."

HOMEPAGE = "http://invisible-island.net/xterm/xterm.html"

inherit debian-package autotools
SECTION = "x11"
DEBIAN_SECTION = "x11"
PR = "r0"
DPR = "0"

LICENSE = "MIT"
LIC_FILES_CHKSUM = " \
file://xterm.h;beginline=3;endline=31;md5=540cf18ccc16bc3c5fea40d2ab5d8d51 \
"

DEPENDS += "ncurses libxft libxrender libxaw libxkbfile libxcursor"

# Apply debian patch by quilt
DEBIAN_PATCH_TYPE = "quilt"

do_configure() {
	sed -i -e "s:-V -qversion::" ${S}/configure
	oe_runconf
}

FILES_${PN} += "${libdir}"
