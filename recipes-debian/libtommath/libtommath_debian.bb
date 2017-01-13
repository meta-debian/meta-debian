SUMMARY = "multiple-precision integer library"
DESCRIPTION = "LibTomMath is a C language library that provides a vast array \
of highly optimized functions for number theory."

inherit debian-package autotools-brokensep
PV = "0.42.0"
PR = "r0"

LICENSE = "PD"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=98bc0cb525cb6dc43f4375836db2f87c"

SRC_URI += "\
        file://replace-group-wheel.patch \
"

EXTRA_OEMAKE += " LIBTOOL=${HOST_SYS}-libtool GCC=${HOST_SYS}-gcc"

do_compile() {
	# Modify makefile.shared to add cross-compile options
	sed -i -e "s/libtool/\$\{LIBTOOL}/" ${S}/makefile.shared
	sed -i -e "s/--mode=link/--mode=link --tag=CC/" ${S}/makefile.shared
	sed -i -e "s/gcc/\$\{GCC}/" ${S}/makefile.shared

	oe_runmake -f makefile.shared 
}

do_install() {
	oe_runmake install INSTALL_GROUP=root DESTDIR=${D}

	# Ship libtommath dynamic library to correct directory
	install -m 0755 ${S}/.libs/libtommath.so.0.0.42 ${D}${libdir}

	ln -sf libtommath.so.0.0.42 ${D}${libdir}/libtommath.so.0
	ln -sf libtommath.so.0 ${D}${libdir}/libtommath.so
}

# Correct name of libtommath-dev package
DEBIANNAME_${PN}-dev = "libtommath-dev"

FILES_${PN}-dev = "${includedir} ${libdir}/libtommath.so"
