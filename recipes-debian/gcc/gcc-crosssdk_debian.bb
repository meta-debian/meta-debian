#
# base recipe: meta/recipes-devtools/gcc/gcc-crosssdk_4.8.bb
# base branch: daisy
#

PR = "r0"
require gcc-cross_${PV}.bb

inherit crosssdk

PN = "gcc-crosssdk-${TARGET_ARCH}"

SYSTEMHEADERS = "${SDKPATHNATIVE}${prefix_nativesdk}/include"
SYSTEMLIBS = "${SDKPATHNATIVE}${base_libdir_nativesdk}/"
SYSTEMLIBS1 = "${SDKPATHNATIVE}${libdir_nativesdk}/"

GCCMULTILIB = "--disable-multilib"

DEPENDS = "virtual/${TARGET_PREFIX}binutils-crosssdk virtual/nativesdk-${TARGET_PREFIX}libc-for-gcc gettext-native ${NATIVEDEPS}"
PROVIDES = "virtual/${TARGET_PREFIX}gcc-crosssdk virtual/${TARGET_PREFIX}g++-crosssdk"
