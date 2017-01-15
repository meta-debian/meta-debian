#
# base recipe: /meta/recipes-support/gnutls/libtasn1_4.5.bb
# base branch: master
# base commit: 0d9903d8b9d090b70ed71de22b4f7ad34ee3fc4b
#

SUMMARY = "Library for ASN.1 and DER manipulation"
HOMEPAGE = "http://www.gnu.org/software/libtasn1/"

PR = "r0"
inherit debian-package
PV = "4.2"

LICENSE = "GPLv3+ & LGPLv2.1+"
LICENSE_${PN}-bin = "GPLv3+"
LICENSE_${PN} = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://COPYING.LIB;md5=4fbd65380cdd255951079008b364516c \
                    file://README;endline=8;md5=c3803a3e8ca5ab5eb1e5912faa405351"

#install follow Debian jessie
do_install_append() {
	#remove unwanted files
	rm ${D}${libdir}/libtasn1.la
	
	LINKLIB=$(basename $(readlink ${D}${libdir}/libtasn1.so))
	chmod 0644 ${D}${libdir}/${LINKLIB}
}
inherit autotools texinfo binconfig lib_package
DEBIANNAME_${PN}-dev = "${PN}-dev"
DEBIANNAME_${PN}-dbg = "${PN}-dbg"
