SUMMARY = "GNU extension language and Scheme interpreter"
DESCRIPTION = "Guile is a Scheme implementation designed for real world programming, \
 providing a rich Unix interface, a module system, an interpreter, and \
 many extension languages.  Guile can be used as a standard #! style \
 interpreter, via #!/usr/bin/guile, or as an extension language for \
 other applications via libguile."
HOMEPAGE = "http://www.gnu.org/software/guile/"

PR = "r0"
inherit debian-package
PV = "2.0.11+1"

LICENSE = "GPLv3+ & LGPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://COPYING.LESSER;md5=b52f2d57d10c4f7ee67a7eb9615d5d24 \
                    file://LICENSE;md5=e86114604bd43a4d23351c30b446e789"

# Disable build doc to avoid error in do_configure
# 	| error: cannot open < doc/ref/guile-2.0.texi
SRC_URI += "file://Disable-build-doc_debian.patch"
inherit autotools pkgconfig gettext
DEPENDS_class-native += "libgc libunistring gmp libffi readline libtool"
DEPENDS_class-target += "${PN}-native libgc libunistring gmp libffi readline libtool"

CACHED_CONFIGUREVARS += "ac_cv_path_GUILE_FOR_BUILD=${STAGING_BINDIR_NATIVE}/${DPN}"

do_install_append() {
	# base on debian/rules
	install -d ${D}${libdir}/${DPN}${base_bindir} \
	           ${D}${datadir}/gdb/auto-load
	cp -a ${D}${bindir}/guile ${D}${libdir}/${DPN}${base_bindir}/
	mv ${D}${bindir}/guile ${D}${bindir}/${DPN}
	mv ${D}${libdir}/libguile-2.0.so*-gdb.scm ${D}${datadir}/gdb/auto-load
}
PACKAGES =+ "${PN}-libs"
FILES_${PN}-libs = "\
	${libdir}/${DPN}${base_bindir}/* \
	${libdir}/guile/*/ccache \
	${libdir}/*${SOLIBS} \
	${datadir}/guile/*/ice-9* \
	${datadir}/guile/*/language* \
	${datadir}/guile/*/oop* \
	${datadir}/guile/*/rnrs* \
	${datadir}/guile/*/srfi* \
	${datadir}/guile/*/statprof.scm \
	${datadir}/guile/*/sxml* \
	${datadir}/guile/*/system* \
	${datadir}/guile/*/texinfo* \
	${datadir}/guile/*/web* \
	${datadir}/guile/*/guile-* \
"
FILES_${PN}-dev += "\
	${libdir}/libguile-2.0.so*-gdb.scm \
	${bindir}/guild \
	${bindir}/guile-* \
	${datadir}/guile/*/scripts* \
	${datadir}/gdb/auto-load* \
"
FILES_${PN}-dbg += "${libdir}/${PN}${base_bindir}/.debug"
BBCLASSEXTEND = "native"
# skip libdir QA check for /usr/share/gdb/auto-load/libguile-2.0.so.22.7.2-gdb.scm
# Avoid QA Issue: 
# | guile-2.0-dev: found library in wrong location: 
# | /usr/share/gdb/auto-load/libguile-2.0.so.22.7.2-gdb.scm [libdir]
INSANE_SKIP_${PN}-dev = "libdir"

RDEPENDS_${PN}_class-target += "${PN}-libs"
RPROVIDES_${PN}-dev_class-target += "libguile-dev"
RPROVIDES_${PN}-libs_class-target += "${PN}-slib"
