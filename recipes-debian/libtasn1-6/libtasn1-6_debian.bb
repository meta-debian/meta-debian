#
# base recipe: /meta/recipes-support/gnutls/libtasn1_4.5.bb
# base branch: master
# base commit: 0d9903d8b9d090b70ed71de22b4f7ad34ee3fc4b
#

SUMMARY = "Library for ASN.1 and DER manipulation"
HOMEPAGE = "http://www.gnu.org/software/libtasn1/"

PR = "r0"
inherit debian-package

LICENSE = "GPLv3+ & LGPLv2.1+"
LICENSE_${PN}-bin = "GPLv3+"
LICENSE_${PN} = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://COPYING.LIB;md5=4fbd65380cdd255951079008b364516c \
                    file://README;endline=8;md5=c3803a3e8ca5ab5eb1e5912faa405351"

SRC_URI += "file://dont-depend-on-help2man.patch \
           "
inherit autotools texinfo binconfig lib_package
