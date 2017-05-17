#
# Base recipe: meta/recipes-extended/pixz/pixz_1.0.6.bb
# Base branch: master
# Base commit: af1f77a1eb9d1dc3de17c9b0a2b74d76ada40544
#

SUMMARY = "Parallel, indexed xz compressor"

DEPENDS = "xz libarchive"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5cf6d164086105f1512ccb81bfff1926"

SRC_URI += "file://936d8068ae19d95260d3058f41dd6cf718101cd6.patch \
            file://0001-configure-Detect-headers-before-using-them.patch \
            file://0002-endian-Use-macro-bswap_64-instead-of-__bswap_64.patch \
"

EXTRA_OECONF += "--without-manpage"
CFLAGS_append_libc-musl = " -D_GNU_SOURCE"
CACHED_CONFIGUREVARS += "ac_cv_file_src_pixz_1=no"

inherit debian-package
PV = "1.0.2"

BBCLASSEXTEND = "native"

do_install() {
	install -d ${D}${bindir}
	install -m 0755 ${S}/pixz ${D}${bindir}
}
