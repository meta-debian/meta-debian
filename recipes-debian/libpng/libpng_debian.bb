#
# Basse recipe: meta/recipes-multimedia/libpng/libpng_1.6.8.bb
# Base branch: daisy
# Base commit: 9e4aad97c3b4395edeb9dc44bfad1092cdf30a47
#

SUMMARY = "PNG image format decoding library"
HOMEPAGE = "http://www.libpng.org/"

inherit autotools binconfig pkgconfig debian-package

PR = "r0"
DEPENDS = "zlib"
LIBV = "16"

LICENSE = "Libpng"
LIC_FILES_CHKSUM = " \
file://LICENSE;md5=c3d807a85c09ebdff087f18b4969ff96 \
file://png.h;beginline=310;endline=424;md5=b87b5e9252a3e14808a27b92912d268d \
"

# Work around missing symbols
EXTRA_OECONF_append_arm = " ${@bb.utils.contains("TUNE_FEATURES", "neon", "--enable-arm-neon=on", "--enable-arm-neon=off" ,d)}"

do_install_append() {
	install -d ${D}${base_libdir}
	
	# Move libraries to /lib according to Debian package
	mv ${D}${libdir}/libpng12.so.* ${D}${base_libdir}
	ln -sf ${D}${base_libdir}/libpng12.so.0.50.0 ${D}${libdir}/libpng12.so.0
}

# Add package libpng3
PACKAGES =+ "${PN}-tools libpng3"

FILES_${PN} = "${base_libdir}/libpng12.so.0* ${libdir}/libpng12.so.0"
FILES_libpng3 = "${libdir}/libpng.so.*"
FILES_${PN}-tools = "${bindir}/png-fix-itxt ${bindir}/pngfix"

# Correct name of .deb file
DEBIANNAME_libpng3 = "libpng3"

BBCLASSEXTEND = "native nativesdk"
