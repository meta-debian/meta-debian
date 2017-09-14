PR = "r0"

inherit debian-package
PV = "2.0.21-stable"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=45c5316ff684bcfe2f9f86d8b1279559"

# libevent-openssl depends on openssl
DEPENDS = "openssl"

inherit autotools

# Configure follow debian/rules
EXTRA_OECONF += "--disable-libevent-regress"

do_install_append(){
	# Debian don't need binary from libevent
	rm -rf ${D}${bindir}
}

PACKAGES =+ "${PN}-core ${PN}-extra ${PN}-openssl ${PN}-pthreads"

FILES_${PN}-core = "${libdir}/libevent_core-*"
FILES_${PN}-extra = "${libdir}/libevent_extra-*"
FILES_${PN}-openssl = "${libdir}/libevent_openssl-*"
FILES_${PN}-pthreads = "${libdir}/libevent_pthreads-*"

# Follow debian/control
RDEPENDS_${PN}-extra = "${PN}-core"
RDEPENDS_${PN}-openssl = "${PN}-core"
RDEPENDS_${PN}-pthreads = "${PN}-core"

DEBIANNAME_${PN}-dev = "${PN}-dev"
DEBIAN_NOAUTONAME_${PN}-dev = "1"
