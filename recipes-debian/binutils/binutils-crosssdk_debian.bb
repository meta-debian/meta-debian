#
# base recipe: meta/recipes-devtools/binutils/binutils-crosssdk_2.25.bb
# base branch: master
# base commit: 3b7c38458856805588d552508de10944ed38d9f2
#

require binutils-cross_${PV}.bb

PR = "r0"

inherit crosssdk

PN = "binutils-crosssdk-${TARGET_ARCH}"

PROVIDES = "virtual/${TARGET_PREFIX}binutils-crosssdk"

SRC_URI += "file://0001-Generate-relocatable-SDKs.patch"

do_configure_prepend () {
	sed -i 's#/usr/local/lib /lib /usr/lib#${SDKPATHNATIVE}/lib ${SDKPATHNATIVE}/usr/lib /usr/local/lib /lib /usr/lib#' ${S}/ld/configure.tgt
}
