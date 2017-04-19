#
# Base recipe: meta/recipes-support/lzo/lzo_2.06.bb
# Base branch: Daisy
# Base commit: 9e4aad97c3b4395edeb9dc44bfad1092cdf30a47
#
SUMMARY = "Lossless data compression library"
HOMEPAGE = "http://www.oberhumer.com/opensource/lzo/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://src/lzo_init.c;beginline=1;endline=26;md5=b1157836ea1e33cb3c6791ecaee7e9a9"

PR = "r0"
DPN = "lzo2"

inherit autotools debian-package
PV = "2.08"

SRC_URI += " \
	file://acinclude.m4 \
"

EXTRA_OECONF = "--enable-shared --disable-libtool-lock"

do_configure_prepend () {
        cp ${WORKDIR}/acinclude.m4 ${S}/
}

BBCLASSEXTEND = "native nativesdk"
