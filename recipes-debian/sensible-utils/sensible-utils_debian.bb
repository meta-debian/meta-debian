SUMMARY = "Utilities for sensible alternative selection"
DESCRIPTION = "This package provides a number of small utilities which are used\n\
by programs to sensibly select and spawn an appropriate browser,\n\
editor, or pager.\n\
.\n\
The specific utilities included are: sensible-browser sensible-editor\n\
sensible-pager"

PR = "r0"

inherit debian-package
PV = "0.0.9"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=19d43732fbe50ffbf510e2315e81d895"

inherit autotools

PACKAGE_ARCH = "all"

do_install_append() {
	install -d ${D}${libdir}/mime/packages
	install -m 0644 ${S}/debian/mime ${D}${libdir}/mime/packages/sensible-utils
}

FILES_${PN} += "${libdir}/mime"
