SUMMARY = "X terminal emulator"

DESCRIPTION = "xterm is a terminal emulator for the X Window System. \
It provides DEC VT102 and Tektronix 4014 compatible terminals for programs \
that cannot use the window system directly.  This version implements ISO/ANSI \
colors and most of the control sequences used by DEC VT220 terminals."

HOMEPAGE = "http://invisible-island.net/xterm/xterm.html"

PR = "r0"

inherit debian-package autotools
PV = "312"

LICENSE = "MIT"
LIC_FILES_CHKSUM = " \
file://xterm.h;beginline=3;endline=31;md5=540cf18ccc16bc3c5fea40d2ab5d8d51 \
"

DEPENDS += "ncurses libxft libxrender libxaw libxkbfile libxcursor"

EXTRA_OECONF += " \
		--exec-prefix=${prefix} \
		--with-app-defaults=${sysconfdir}/X11/app-defaults \
		--with-icondir=${datadir}/icons \
		--with-icon-theme=yes \
		--with-tty-group=tty \
		--enable-logging \
		--enable-wide-chars \
		--enable-luit \
		--enable-256-color \
		--disable-imake \
		--enable-narrowproto \
		--enable-exec-xterm \
		--enable-dabbrev \
		--enable-backarrow-is-erase \
		--enable-sixel-graphics \
		LUIT=${bindir}/luit"

# Apply debian patch by quilt
DEBIAN_PATCH_TYPE = "quilt"

# Remove unrecognized command line option in configuration file.
do_configure() {
	sed -i -e "s:-V -qversion::" ${S}/configure
	oe_runconf
}

do_install_append() {
	## Install lxterm command and manual page.
	install -m 755 ${S}/debian/local/lxterm ${D}${bindir}
	install -m 755 ${S}/debian/local/lxterm.1 ${D}${mandir}/man1
	## Install terminfo and termcap precompiled definitions.
	install -d ${D}${docdir}/xterm
	install -m 644 ${S}/termcap  ${D}${docdir}/xterm/xterm.termcap
	install -m 644 ${S}/terminfo ${D}${docdir}/xterm/xterm.terminfo
}

FILES_${PN} = "${sysconfdir} ${bindir} ${datadir}/applications ${datadir}/icons \
		${datadir}/menu ${datadir}/pixmaps"
FILES_${PN}-doc = "${docdir} ${mandir}"
