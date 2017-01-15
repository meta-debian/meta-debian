SUMMARY = "FreeTDS command-line utilities"
DESCRIPTION = "\
FreeTDS is an implementation of the Tabular DataStream protocol, used for \
connecting to MS SQL and Sybase servers over TCP/IP. \
"
HOMEPAGE = "http://www.freetds.org/"
PR = "r0"
inherit debian-package
PV = "0.91"

LICENSE = "GPLv2+ & LGPLv2+"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=c93c0550bd3173f4504b2cbd8991e50b \
	file://COPYING.LIB;md5=55ca817ccb7d5b5b66355690e9abc605"
inherit autotools

DEPENDS += "gnutls unixodbc glib-2.0 krb5"
EXTRA_OECONF += "\
                --with-gnu-ld --with-tdsver=4.2 \
                --with-unixodbc=${STAGING_DIR_HOST}${prefix} \
                --sysconfdir=${sysconfdir}/${DPN} \
                --enable-sybase-compat --with-gnutls --enable-krb5"

# There is no debian/source or debian/patches
DEBIAN_PATCH_TYPE = "nopatch"

do_install_append() {
	install -d ${D}${libdir}/odbc
	LINKLIB=$(basename $(readlink ${D}${libdir}/libtdsodbc.so))
	#follow debian/rules
	mv ${D}${libdir}/$LINKLIB \
		${D}${libdir}/odbc/libtdsodbc.so

	#follow debian/tdsodbc.install
	install -D -m 0644 ${S}/debian/odbcinst.ini \
		${D}${datadir}/tdsodbc/odbcinst.ini
	#follow debian/freetds-common.install
	install -d ${D}${datadir}/${DPN}
	mv ${D}${sysconfdir}/${DPN}/freetds.conf \
		${D}${datadir}/${DPN}/freetds.conf
	
	rm ${D}${libdir}/libtdsodbc.*
	rm ${D}${sysconfdir}/${DPN}/* ${D}${libdir}/*.la
}
PACKAGES =+ "libct libsybdb tdsodbc ${PN}-common"
PKG_${PN} = "${PN}-bin"
FILES_${PN}-common += "${datadir}/${DPN}"
FILES_libct = "${libdir}/libct.so.*"
FILES_libsybdb = "${libdir}/libsybdb.so.*"
FILES_tdsodbc = "${libdir}/odbc/libtdsodbc.so ${datadir}/tdsodbc"
FILES_${PN}-dbg += "${libdir}/odbc/.debug"

RDEPENDS_libct += "${PN}-common"
RDEPENDS_${PN} += "${PN}-common"
RDEPENDS_tdsodbc += "${PN}-common"
RDEPENDS_${PN}-dev += "libct libsybdb"
