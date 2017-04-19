SUMMARY = "Database driver for libdbi"
HOMEPAGE = "http://libdbi.sourceforge.net/"

PR = "r0"
inherit debian-package
PV = "0.9.0"

LICENSE = "LGPLv2+"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=d8045f3b8f929c1cb29a1e3fd737b499"
inherit autotools-brokensep

DEPENDS += "libdbi mysql sqlite3 postgresql sqlite"
EXTRA_OECONF += "\
	--with-pgsql \
	--with-dbi-libdir=${STAGING_LIBDIR} \
	--with-mysql \
	--with-mysql-incdir=${STAGING_INCDIR} \
	--with-mysql-libdir=${STAGING_LIBDIR} \
	--with-sqlite3 \
	--with-sqlite3-incdir=${STAGING_INCDIR} \
	--with-sqlite3-libdir=${STAGING_LIBDIR} \
	--with-sqlite \
	--with-sqlite-incdir=${STAGING_INCDIR} \
	--with-sqlite-libdir=${STAGING_LIBDIR} \
	--with-freetds \
	--with-freetds-incdir=${STAGING_INCDIR} \
	--with-freetds-libdir=${STAGING_LIBDIR} \
"
# Avoid a parallel build problem
PARALLEL_MAKE = ""

do_install_append() {
	#remove unwanted files
	rm ${D}${libdir}/dbd/*.a
}
PACKAGES =+ "\
	libdbd-freetds libdbd-mysql libdbd-pgsql libdbd-sqlite libdbd-sqlite3"
FILES_libdbd-freetds = "${libdir}/dbd/libdbdfreetds.*"
FILES_libdbd-mysql = "${libdir}/dbd/libdbdmysql.*"
FILES_libdbd-pgsql = "${libdir}/dbd/libdbdpgsql.*"
FILES_libdbd-sqlite = "${libdir}/dbd/libdbdsqlite.*"
FILES_libdbd-sqlite3 = "${libdir}/dbd/libdbdsqlite3.*"
FILES_${PN}-dbg += "${libdir}/dbd/.debug"
