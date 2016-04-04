DESCRIPTION = "Common files for SELinux policy management libraries."

PR = "r0"

inherit debian-package

LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=a6f89e2100d9b6cdffcea4f398e37343"

DEPENDS += "ustr libsepol libselinux audit"

do_compile() {
	oe_runmake all LIBBASE="${base_libdir}" DESTDIR="${D}"
}

do_install() {
	oe_runmake install DESTDIR="${D}"
}

PACKAGES =+ "${BPN}-common"
FILES_${BPN}-common = "${sysconfdir}"
DEBIANNAME_${BPN} = "${BPN}1"
DEBIANNAME_${BPN}-dev = "${BPN}1-dev"
