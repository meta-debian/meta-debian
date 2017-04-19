SUMMARY = "manipulating sets and relations of integer points bounded by linear constraints"
DESCRIPTION = "isl is a library for manipulating sets and relations of integer points \
bounded by linear constraints. Supported operations on sets include \
intersection, union, set difference, emptiness check, convex hull, \
(integer) affine hull, integer projection, and computing the lexicographic \
minimum using parametric integer programming. It also includes an ILP solver \
based on generalized basis reduction."
HOMEPAGE = "http://freecode.com/projects/isl"

inherit debian-package
PV = "0.12.2"

LICENSE = "MIT & BSD-2-Clause"
LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=0c7c9ea0d2ff040ba4a25afa0089624b \
    file://interface/python.cc;endline=32;md5=44ee34620dfecde91f1ee466a817d259 \
"

DEPENDS = "gmp"

inherit autotools

do_install_append() {
	# Base on debian/rules
	install -d ${D}${libdir}/debug/${libdir}
	mv ${D}${libdir}/*-gdb.py ${D}${libdir}/debug/${libdir}/

	sed -i -e 's,/.*/,,' ${D}${includedir}/isl/stdint.h
}

FILES_${PN}-dbg += "${libdir}/debug"

RPROVIDES_${PN} += "lib${PN}"
RPROVIDES_${PN}-dev += "lib${PN}-dev"
