#
# base recipe: meta/recipes-devtools/gcc/gcc-crosssdk-initial_4.8.bb
# base branch: daisy
#

require gcc-cross-initial_${PV}.bb

inherit crosssdk

PN = "gcc-crosssdk-initial-${SDK_SYS}"

SYSTEMHEADERS = "${SDKPATHNATIVE}${prefix_nativesdk}/include"
SYSTEMLIBS = "${SDKPATHNATIVE}${base_libdir_nativesdk}/"
SYSTEMLIBS1 = "${SDKPATHNATIVE}${libdir_nativesdk}/"

DEPENDS = "virtual/${TARGET_PREFIX}binutils-crosssdk gettext-native ${NATIVEDEPS}"
PROVIDES = "virtual/${TARGET_PREFIX}gcc-initial-crosssdk"
