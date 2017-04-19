DESCRIPTION = "\
	UnixODBC is an implementation of the Open Database Connectivity standard,\
	a database abstraction layer that allows applications to be used with many\
	different relational databases by way of a single library."
HOMEPAGE = "http://www.unixodbc.org"

PR = "r0"
inherit debian-package
PV = "2.3.1"

LICENSE = "LGPL-2.0+ & GPLv2+"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=d7b37bf80a3df5a65b355433ae36d206 \
	file://exe/COPYING;md5=f73069ee5fe10af114e5300a37d32d44"

DEBIAN_PATCH_TYPE = "nopatch"

inherit autotools-brokensep
DEPENDS += "libtool"
#configure follow debian/rules
EXTRA_OECONF += "\
	--enable-static --enable-ltdllib --without-pth \
	--enable-drivers --enable-driverc"

do_configure() {
	gnu-configize --force
	libtoolize --force --copy
	aclocal
	autoconf
	automake --add-missing
	oe_runconf
}

#install follow Debian jessie
do_install_append() {
	install -d ${D}${libdir}/odbc
	cp -L ${D}${libdir}/lib*S.so ${D}${libdir}/libnn.so ${D}${libdir}/odbc/
	
	#remove the unwanted files
	rm ${D}${libdir}/lib*S.*
	rm ${D}${libdir}/libnn.*
	rm ${D}${libdir}/*.la
	rm ${D}${libdir}/libodbcpsql.*
	rm ${D}${libdir}/libtemplate.*
	rm ${D}${sysconfdir}/odbcinst.ini
	rm ${D}${bindir}/odbc_config
	
	LINKLIB=$(basename $(readlink ${D}${libdir}/libodbccr.so))
	ln -s $LINKLIB ${D}${libdir}/libodbccr.so.1

	LINKLIB=$(basename $(readlink ${D}${libdir}/libodbcinst.so))
	ln -s $LINKLIB ${D}${libdir}/libodbcinst.so.1

	LINKLIB=$(basename $(readlink ${D}${libdir}/libodbc.so))
	ln -s $LINKLIB ${D}${libdir}/libodbc.so.1
}

PACKAGES =+ " libodbc odbcinst odbcinst1debian2 "

FILES_libodbc = "\
	${libdir}/libodbc.so.* 		\
	${libdir}/libodbccr.so.* 	\
	${libdir}/odbc/libnn.so		\
	"

FILES_odbcinst = "\
	${sysconfdir}/odbc.ini 		\
	${bindir}/odbcinst		\
"
FILES_odbcinst1debian2 = "		\
	${libdir}/libodbcinst.so.* 	\
	${libdir}/odbc/*		\
	"
FILES_${PN}-dbg += "${libdir}/odbc/.debug/*"

DEBIANNAME_libodbc = "libodbc1"
DEBIAN_NOAUTONAME_odbcinst1debian2 = "1"

BBCLASSEXTEND = "native"
