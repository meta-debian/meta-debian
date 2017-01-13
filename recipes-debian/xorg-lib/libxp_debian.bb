require xorg-lib-common.inc
PV = "1.0.2"

DESCRIPTION = "X Printing Extension (Xprint) client library"

PR = "${INC_PR}.0"

LIC_FILES_CHKSUM = "file://COPYING;md5=9504a1264f5ddd4949254a57c0f8d6bb"

DEPENDS += "libxext libxau printproto"

CFLAGS_append += " -I ${S}/include/X11/XprintUtil -I ${S}/include/X11/extensions"

# There is no debian patch files
DEBIAN_PATCH_TYPE = "quilt"

# Change package follow Debian
DEBIANNAME_${PN}-dbg = "${PN}6-dbg"

EXTRA_OECONF += "--enable-static"

# Exclude libXp.la and man3 follow Debian
do_install_append() {
	rm -rf ${D}${libdir}/libXp.la
	rm -rf ${D}${mandir}/man3
}
