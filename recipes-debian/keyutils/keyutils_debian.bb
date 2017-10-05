SUMMARY = "Linux Key Management Utilities"
DESCRIPTION = "Keyutils is a set of utilities for managing the key retention facility in the\n\
kernel, which can be used by filesystems, block devices and more to gain and\n\
retain the authorization and encryption keys required to perform secure\n\
operations."
HOMEPAGE = "http://people.redhat.com/~dhowells/keyutils/"
LICENSE = "GPL-2.0 & LGPL-2.1"
SECTION = "admin"
LIC_FILES_CHKSUM = "file://LICENCE.GPL;md5=5f6e72824f5da505c1f4a7197f004b45 \
                    file://LICENCE.LGPL;md5=7d1cacaa3ea752b72ea5e525df54a21f"

inherit debian-package
PV = "1.5.9"

SRC_URI += "file://0001-create-relative-symbolic-link.patch"

CFLAGS += "-fPIE"

do_install_append() {
	oe_runmake install DESTDIR=${D} \
		LIBDIR=${base_libdir} USRLIBDIR=${libdir}
}

PACKAGES =+ "lib${PN} lib${PN}-dev"
FILES_lib${PN} = "${base_libdir}"
FILES_lib${PN}-dev = "${includedir} ${libdir}/libkeyutils.so"
