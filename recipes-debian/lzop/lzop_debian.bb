SUMMARY = "Real-time file compressor"
DESCRIPTION = "lzop is a compression utility which is designed to be a companion to gzip. \
It is based on the LZO data compression library and its main advantages over \
gzip are much higher compression and decompression speed at the cost of some \
compression ratio. The lzop compression utility was designed with the goals \
of reliability, speed, portability and with reasonable drop-in compatibility \
to gzip."
HOMEPAGE = "http://www.lzop.org/"

PR = "r0"
inherit debian-package
PV = "1.03"

# source format is 3.0 but there is no patch
DEBIAN_QUILT_PATCHES = ""

DEPENDS += "lzo"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=dfeaf3dc4beef4f5a7bdbc35b197f39e"

#acinclude.m4:
#	Need to run autoconf
SRC_URI += "file://acinclude.m4 \
"
inherit autotools

do_configure () {
	ln -sf  ../acinclude.m4 ${S}/acinclude.m4
	autotools_do_configure
}

BBCLASSEXTEND += "native nativesdk"
