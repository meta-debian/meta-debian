SUMMARY = "Interactive high-level object-oriented language (version 2.7)"
DESCRIPTION= "Python is a high-level, interactive, object-oriented language. Its 2.7 version \
includes an extensive class library with lots of goodies for \
network programming, system administration, sounds and graphics. \
"
require recipes-devtools/python/python.inc

inherit debian-package
require recipes-debian/sources/python2.7.inc

DEBIAN_UNPACK_DIR = "${WORKDIR}/Python-${PV}"
DEBIAN_PATCH_TYPE = "quilt"

LICENSE = "PSFv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e466242989bd33c1bd2b6a526a742498" 

do_debian_unpack_extra() {
	cd ${DEBIAN_UNPACK_DIR}
	patch -p1 < ${WORKDIR}/${BPN}2.7_${DPV}.diff
}

do_debian_patch_prepend() {
	cpp -E -o - ${S}/debian/patches/series.in | egrep -v '^(#.*|$$)' > ${S}/debian/patches/series
}
