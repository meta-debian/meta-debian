#
# Basse recipe: meta/recipes-multimedia/libpng/libpng_1.6.8.bb
# Base branch: daisy
# Base commit: 9e4aad97c3b4395edeb9dc44bfad1092cdf30a47
#

SUMMARY = "PNG image format decoding library"
HOMEPAGE = "http://www.libpng.org/"

inherit debian-package
inherit autotools binconfig pkgconfig

PR = "r1"
DEPENDS = "zlib"

LICENSE = "Libpng"
LIC_FILES_CHKSUM = " \
file://LICENSE;md5=c3d807a85c09ebdff087f18b4969ff96 \
file://png.h;beginline=310;endline=424;md5=b87b5e9252a3e14808a27b92912d268d \
"

do_install_append() {
	install -d ${D}${base_libdir}
	
	# Correct libraries location and links according to Debian package
	mv ${D}${libdir}/libpng12.so.* ${D}${base_libdir}

	links="libpng12.so.0 libpng12.so libpng.so.3"
	for i in ${links}; do
		if [ -f ${D}${libdir}/${i} ];then
			rm ${D}${libdir}/${i}
		fi
	done
	ln -sf ../../lib/libpng12.so.0 ${D}${libdir}/libpng12.so.0
	ln -sf libpng12.so.0 ${D}${libdir}/libpng12.so
	ln -sn ../../lib/libpng12.so.0 ${D}${libdir}/libpng.so.3
}

# Add package libpng12 and libpng3
PACKAGES =+ "${PN}12 ${PN}3"
RPROVIDES_${PN}-dev += "${PN}12-dev"

FILES_${PN}12 = " \
	${base_libdir}/libpng12${SOLIBS} \
	${libdir}/libpng12${SOLIBS} \
"
FILES_libpng3 = "${libdir}/libpng.so.3"

# Correct name of .deb file
DEBIANNAME_${PN}12 = "${PN}12-0"

BBCLASSEXTEND = "native nativesdk"
