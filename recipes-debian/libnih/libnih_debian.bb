SUMMARY = "NIH Utility Library"
DESCIPTION = "libnih is a light-weight \"standard library\" of C functions to ease the \
	development of other libraries and applications, especially those \
	normally found in /lib. This package contains the shared library."
HOMEPAGE = "https://launchpad.net/libnih"
LICENSE = "GPL-2.0+"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

inherit debian-package
PV = "1.0.3"

DEPENDS += "libnih-native dbus"

PR = "r0"

inherit autotools gettext

DEBIAN_PATCH_TYPE = "nopatch"
BBCLASSEXTEND = "native"
do_install_append() {
	install -d ${D}${base_libdir}
	mv ${D}${libdir}/libnih${SOLIBS} ${D}${base_libdir}
	mv ${D}${libdir}/libnih-dbus${SOLIBS} ${D}${base_libdir}

	LINKLIB=$(basename $(readlink ${D}${libdir}/libnih.so))
	ln -sf ../../lib/$LINKLIB ${D}${libdir}/libnih.so

	LINKLIB=$(basename $(readlink ${D}${libdir}/libnih-dbus.so))
	ln -sf ../../lib/$LINKLIB ${D}${libdir}/libnih-dbus.so
}

PACKAGES =+ "${PN}-dbus nih-dbus-tool ${PN}-dbus-dev"

FILES_${PN}-dbus = "${base_libdir}/libnih-dbus${SOLIBS}"
FILES_nih-dbus-tool = "${bindir}/nih-dbus-tool"
FILES_${PN}-dbus-dev = "${includedir}/libnih-dbus.h \
                        ${includedir}/nih-dbus/* \
                        ${libdir}/libnih-dbus.so \
                        ${libdir}/pkgconfig/libnih-dbus.pc"
FILES_${PN} = "${base_libdir}/libnih${SOLIBS}"
