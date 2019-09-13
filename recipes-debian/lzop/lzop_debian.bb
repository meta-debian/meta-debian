SUMMARY = "Real-time file compressor"
DESCRIPTION = "lzop is a compression utility which is designed to be a companion to gzip. \n\
It is based on the LZO data compression library and its main advantages over \n\
gzip are much higher compression and decompression speed at the cost of some \n\
compression ratio. The lzop compression utility was designed with the goals \n\
of reliability, speed, portability and with reasonable drop-in compatibility \n\
to gzip."
DEPENDS += "lzo"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=dfeaf3dc4beef4f5a7bdbc35b197f39e \
                    file://src/lzop.c;beginline=5;endline=21;md5=6797bd3ed0a1a49327b7ebf9366ebd86"

inherit debian-package
require recipes-debian/sources/lzop.inc
FILESPATH_append = ":${COREBASE}/meta/recipes-support/lzop/lzop"

SRC_URI += "\
           file://acinclude.m4 \
          "

inherit autotools

do_configure_prepend () {
    install -Dm 0644 ${WORKDIR}/acinclude.m4 ${S}/acinclude.m4
}

BBCLASSEXTEND = "native nativesdk"
