SUMMARY = "C library for country/city/organization to IP address or hostname mapping"
DESCRIPTION = " GeoIP is a C library that enables the user to find the country 	\
		that any IP address or hostname originates from. It uses a file	\
		based database that is accurate as of March 2003. This database	\
		simply contains IP blocks as keys, and countries as values. 	\
		This database should be more complete and accurate than 	\
		using reverse DNS lookups."
HOMEPAGE = "http://dev.maxmind.com/geoip/"

PR = "r1"
inherit debian-package
PV = "1.6.2"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d5d53d6b948c064f4070183180a4fa89"

# source format is 3.0 (quilt) but there is no debian patch
DEBIAN_QUILT_PATCHES = ""

inherit autotools

#split geoip package to sub-packages
PACKAGES =+ "libgeoip"

#compile follow debian/rules
do_compile_append () {
	${CXX} ${CPPFLAGS} ${LDFLAGS} -g ${S}/debian/src/geoip-csv-to-dat.cpp -o \
		${S}/debian/src/geoip-generator -lGeoIP \
		-I${S}/libGeoIP -L${B}/libGeoIP/.libs
	${CXX} ${CPPFLAGS} ${LDFLAGS} -g ${S}/debian/src/geoip-asn-csv-to-dat.cpp -o \
		${S}/debian/src/geoip-generator-asn -lGeoIP \
		-I${S}/libGeoIP -L${B}/libGeoIP/.libs
}

#install follow Debian jessies
do_install_append() {
	install -d ${D}${libdir}/geoip
	install -m 0755 ${S}/debian/src/geoip-generator \
			${D}${libdir}/geoip/
	install -m 0755 ${S}/debian/src/geoip-generator-asn \
                        ${D}${libdir}/geoip/
	install -m 0755 ${S}/debian/src/v4-to-v6-layout.pl \
			${D}${libdir}/geoip/
	LINKLIB=$(basename $(readlink ${D}${libdir}/libGeoIP.so))
	chmod 0644 ${D}${libdir}/${LINKLIB}
	rm ${D}${libdir}/libGeoIP.la		
}
#Correct the package name
DEBIANNAME_${PN} = "${PN}-bin"
DEBIANNAME_${PN}-dev = "lib${PN}-dev"
RPROVIDES_${PN} += "${PN}-bin"
RPROVIDES_${PN}-dev += "lib${PN}-dev"

FILES_libgeoip = "${libdir}/libGeoIP.so.*"
