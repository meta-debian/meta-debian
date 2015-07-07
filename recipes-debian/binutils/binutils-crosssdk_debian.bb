#
# base recipe: meta/recipes-devtools/binutils/binutils-crosssdk_2.24.bb
# base branch: daisy
#

require binutils-cross_${PV}.bb

PR = "r0"

inherit crosssdk

PROVIDES = "virtual/${TARGET_PREFIX}binutils-crosssdk"

SRC_URI += "file://relocatable_sdk.patch"

do_configure_prepend () {
	sed -i 's#/usr/local/lib /lib /usr/lib#${SDKPATHNATIVE}/lib ${SDKPATHNATIVE}/usr/lib /usr/local/lib /lib /usr/lib#' ${S}/ld/configure.tgt
}
