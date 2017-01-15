DESCRIPTION = "\
SQLite is a C library that implements an SQL database engine. \
Programs that link with the SQLite library can have SQL database \
access without running a separate RDBMS process. \
"
PR = "r0"
inherit debian-package
PV = "2.8.17"

LICENSE = "PD"
LIC_FILES_CHKSUM = "\
	file://src/md5.c;beginline=15;endline=30;md5=c7cae0a182bef159173da0711ab3e7b5"
inherit autotools

# Correct the name and path to libtool: ${B}/${HOST_SYS}-libtool
SRC_URI += "file://fix-hardcode-libtool.patch"

DEPENDS += "tcl readline"
export config_BUILD_CC = "${BUILD_CC}"
export config_BUILD_CFLAGS = "${BUILD_CFLAGS}"
export config_BUILD_LIBS = "${BUILD_LDFLAGS}"
export config_TARGET_CC = "${CC}"
export config_TARGET_LINK = "${CCLD}"
export config_TARGET_CFLAGS = "${CFLAGS}"
export config_TARGET_LFLAGS = "${LDFLAGS}"

EXTRA_OECONF += "\
        config_TARGET_TCL_INC="-I${STAGING_INCDIR}/tcl8.6" \
        config_BUILD_CFLAGS="${CFLAGS} -DTHREADSAFE=1" \
        config_TARGET_LIBS="-ltcl8.6 -lpthread" \
        config_TARGET_READLINE_INC="${STAGING_INCDIR}/readline/readline.h" \
        --enable-utf8"

# Avoid a parallel build problem
PARALLEL_MAKE = ""
#follow debian/rules
do_compile() {
	oe_runmake all libtclsqlite.la doc
}

do_install_append() {
	install -d ${D}${libdir}/${DPN}
	${HOST_SYS}-libtool --mode=install install libtclsqlite.la \
		${D}${libdir}/${DPN}
	install -m 0644 ${S}/debian/pkgIndex.tcl ${D}${libdir}/${DPN}
	rm ${D}${libdir}/${DPN}/libtclsqlite.a \
		${D}${libdir}/${DPN}/libtclsqlite.so \ 
		${D}${libdir}/${DPN}/libtclsqlite.la
}
PACKAGES =+ "lib${PN}-tcl lib${PN}"

FILES_lib${PN}-tcl = "${libdir}/${DPN}/*"
FILES_lib${PN} = "${libdir}/libsqlite.so.*"
FILES_${PN}-dbg += "${libdir}/${DPN}/.debug"
PKG_${PN}-dev = "lib${PN}0-dev"
