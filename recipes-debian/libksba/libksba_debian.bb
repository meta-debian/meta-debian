#
# base recipe: meta/recipes-support/libksba/libksba_1.3.5.bb
# base branch: warrior

SUMMARY = "Easy API to create and parse X.509 and CMS related objects"
DESCRIPTION = "KSBA (pronounced Kasbah) is a library to make X.509 certificates as\n\
 well as the CMS easily accessible by other applications.  Both\n\
 specifications are building blocks of S/MIME and TLS.\n\
 .\n\
 KSBA provides these subsystems: ASN.1 Parser, BER Decoder, BER\n\
 Encoder, Certificate Handling and CMS Handling."
HOMEPAGE = "http://www.gnupg.org/related_software/libksba/"

LICENSE = "GPLv2+ | LGPLv3+ | GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=fd541d83f75d038c4e0617b672ed8bda \
                    file://COPYING.GPLv2;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING.GPLv3;md5=2f31b266d3440dd7ee50f92cf67d8e6c \
                    file://COPYING.LGPLv3;md5=e6a600fd5e1d9cbde2d983680233ad02 \
                   "

inherit debian-package
require recipes-debian/sources/libksba.inc

DEPENDS = "libgpg-error"

BINCONFIG = "${bindir}/ksba-config"

inherit autotools binconfig-disabled pkgconfig texinfo

FILESEXTRAPATHS =. "${COREBASE}/meta/recipes-support/libksba/libksba:"
SRC_URI += "file://ksba-add-pkgconfig-support.patch"

do_configure_prepend () {
	# Else these could be used in preference to those in aclocal-copy
	rm -f ${S}/m4/gpg-error.m4
}

BBCLASSEXTEND = "native"
