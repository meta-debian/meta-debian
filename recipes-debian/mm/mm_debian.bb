SUMMARY = " Shared memory library"
DESCRIPTION = "\
	OSSP mm is a two layer abstraction library which simplifies the use of \
	shared memory between forked (and therefore closely related) processes.\
	It hides all platform-dependent aspects of the process from the user \
	and provides a malloc(3)-style API."

PR = "r0"
inherit debian-package
PV = "1.4.2"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0ba10c36898067a75f550bfa27a47b8b"

inherit autotools-brokensep pkgconfig

#configure follow debian/rules
EXTRA_OECONF += "--with-shm=IPCSHM"

do_configure() {
	autoreconf ${S}
	#set share memory maxsize is 32M
	oe_runconf 'ac_cv_maxsegsize=33554432'
}
#Correct DEBIAN_PATCH_TYPE
DEBIAN_PATCH_TYPE = "nopatch"

do_install_append() {
	rm ${D}${libdir}/libmm.la
}

PKG_${PN} = "libmm14"
PKG_${PN}-dev = "lib${PN}-dev"
PKG_${PN}-dbg = "lib${PN}-dbg"

FILES_${PN}-dev += "${bindir}/mm-config"
