SUMMARY = "Audio visualization framework"
DESCRIPTION = "\
Libvisual is a generic visualization framework that allows\
applications to easily access and manage visualization plugins.	\
Audio visualization is the process of making pretty moving images \
that are correlated in some way to the audio currently being played \
by a media player.  Most audio visualization is tied to a specific \
application or media player, making it difficult to share code.	\
Libvisual allows applications to use existing visualization plugins \
written for the libvisual framework.\
"
HOMEPAGE = "http://sourceforge.net/projects/libvisual/"
PR = "r0"
inherit debian-package
PV = "0.4.0"
LIB_VER = "0.4"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=1b83fd9e43985ce0c01e7b2a65d6432c"

#fix QA issue host include and/or library paths were used
SRC_URI += "file://fix-using-host-library_debian.patch"

inherit autotools-brokensep gettext pkgconfig

EXTRA_OECONF += "--enable-static"

do_compile_append () {
	oe_runmake -C ${S}/po all-yes
}
do_install_append() {
	oe_runmake -C ${S}/po install-data-yes \
		DOMAIN=libvisual-${LIB_VER} DESTDIR=${D}
	rm ${D}${libdir}/*.la
}
PARALLEL_MAKE = ""
FILES_${PN} += "${datadir}"
