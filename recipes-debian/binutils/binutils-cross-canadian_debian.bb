#
# base recipe: meta/recipes-devtools/binutils/binutils-cross-canadian_2.25.bb 
# base branch: master
# base commit: 3b7c38458856805588d552508de10944ed38d9f2
#

require binutils.inc

PR = "r0"

inherit cross-canadian

PN = "binutils-cross-canadian-${TRANSLATED_TARGET_ARCH}"

DEPENDS = "flex-native bison-native virtual/${HOST_PREFIX}gcc-crosssdk virtual/nativesdk-libc nativesdk-zlib nativesdk-gettext"
EXTRA_OECONF += "--with-sysroot=${SDKPATH}/sysroots/${TUNE_PKGARCH}${TARGET_VENDOR}-${TARGET_OS}"
 
# We have to point binutils at a sysroot but we don't need to rebuild if this changes
# e.g. we switch between different machines with different tunes.
EXTRA_OECONF[vardepsexclude] = "TUNE_PKGARCH"
               
do_install () {
	autotools_do_install

	# We're not interested in the libs or headers, these would come from the
	# nativesdk or target version of the binutils recipe
	rm -rf ${D}${prefix}/${TARGET_SYS}
	rm -rf ${D}${prefix}/${SDK_SYS}
	rm -f ${D}${libdir}/libbfd*
	rm -f ${D}${libdir}/libiberty*
	rm -f ${D}${libdir}/libopcodes*
	rm -f ${D}${includedir}/*.h

	cross_canadian_bindirlinks
}

BBCLASSEXTEND = ""
