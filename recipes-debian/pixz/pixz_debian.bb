#
# Base recipe: meta/recipes-extended/pixz/pixz_1.0.6.bb
# Base branch: master
# Base commit: af1f77a1eb9d1dc3de17c9b0a2b74d76ada40544
#

SUMMARY = "Parallel, indexed xz compressor"

DEPENDS = "xz libarchive"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5cf6d164086105f1512ccb81bfff1926"

CFLAGS_append_libc-musl = " -D_GNU_SOURCE"

inherit debian-package
PV = "1.0.2"

BBCLASSEXTEND = "native"

do_compile() {
	oe_runmake CC="${CC}" OPT="${CFLAGS} ${CPPFLAGS}"
}

do_install() {
	install -d ${D}${bindir}
	install -m 0755 ${S}/pixz ${D}${bindir}
}
