#
# based on meta/recipes-bsp/u-boot/u-boot.inc in jethro
#
# UBOOT_CONFIG related settings are removed from
# the original recipe to simplify this recipe
#

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=a2c678cfd4a4d97135585cad908541c6"

PROVIDES = "virtual/bootloader"

# NOTE: if UBOOT_ENV is set, need to add ${UBOOT_ENV}.${UBOOT_ENV_SUFFIX}
SRC_URI = "${BOOTLOADER_GIT_URI}/${BOOTLOADER_GIT_PREFIX}u-boot.git;protocol=${BOOTLOADER_GIT_PROTOCOL}"

# latest stable: v2016.07
SRCREV = "19ce924ff914f315dc2fdf79f357825c513aed6e"
PV = "v2016.07+git${SRCPV}"

S = "${WORKDIR}/git"

# machine dependent package
PACKAGE_ARCH = "${MACHINE_ARCH}"

# openssl-native is required to build some native programs
# in tools directory that depend on libssl
# with specific configurations (see tools/Makefile).
# dtc-native is required to build U-Boot with device tree.
DEPENDS += "openssl-native dtc-native"

# must be defined in machine configration
UBOOT_MACHINE ??= ""

# regarding the variables below, follow poky's namings
# Some versions of u-boot use .bin and others use .img.  By default use .bin
# but enable individual recipes to change this value.
UBOOT_SUFFIX ??= "bin"
UBOOT_IMAGE ?= "u-boot-${MACHINE}-${PV}-${PR}.${UBOOT_SUFFIX}"
UBOOT_BINARY ?= "u-boot.${UBOOT_SUFFIX}"
UBOOT_SYMLINK ?= "u-boot-${MACHINE}.${UBOOT_SUFFIX}"
UBOOT_MAKE_TARGET ?= "all"

# Some versions of u-boot build an SPL (Second Program Loader) image that
# should be packaged along with the u-boot binary as well as placed in the
# deploy directory.  For those versions they can set the following variables
# to allow packaging the SPL.
SPL_BINARY ??= ""
SPL_IMAGE ?= "${SPL_BINARY}-${MACHINE}-${PV}-${PR}"
SPL_SYMLINK ?= "${SPL_BINARY}-${MACHINE}"

# Additional environment variables or a script can be installed alongside
# u-boot to be used automatically on boot.  This file, typically 'uEnv.txt'
# or 'boot.scr', should be packaged along with u-boot as well as placed in the
# deploy directory.  Machine configurations needing one of these files should
# include it in the SRC_URI and set the UBOOT_ENV parameter.
UBOOT_ENV ??= ""
UBOOT_ENV_SUFFIX ?= "txt"
UBOOT_ENV_BINARY ?= "${UBOOT_ENV}.${UBOOT_ENV_SUFFIX}"
UBOOT_ENV_IMAGE ?= "${UBOOT_ENV}-${MACHINE}-${PV}-${PR}.${UBOOT_ENV_SUFFIX}"
UBOOT_ENV_SYMLINK ?= "${UBOOT_ENV}-${MACHINE}.${UBOOT_ENV_SUFFIX}"

# override some compile variables that are not given by bitbake
EXTRA_OEMAKE = 'CROSS_COMPILE=${TARGET_PREFIX} CC="${TARGET_PREFIX}gcc ${TOOLCHAIN_OPTIONS}" V=1'
EXTRA_OEMAKE += 'HOSTCC="${BUILD_CC} ${BUILD_CFLAGS} ${BUILD_LDFLAGS}"'

do_compile() {
	unset LDFLAGS
	unset CFLAGS
	unset CPPFLAGS

	if [ -z "${UBOOT_MACHINE}" ]; then
		bberror "UBOOT_MACHINE is not defined"
		bbfatal "please set UBOOT_MACHINE to the target machine configuration"
	fi
	oe_runmake ${UBOOT_MACHINE}
	oe_runmake ${UBOOT_MAKE_TARGET}
}

do_install() {
	# install core binary
	install -d ${D}/boot
	install ${S}/${UBOOT_BINARY} ${D}/boot/${UBOOT_IMAGE}
	ln -sf ${UBOOT_IMAGE} ${D}/boot/${UBOOT_BINARY}

	# install SPL
	if [ -n "${SPL_BINARY}" ]; then
		install ${S}/${SPL_BINARY} ${D}/boot/${SPL_IMAGE}
		ln -sf ${SPL_IMAGE} ${D}/boot/${SPL_BINARY}
	fi

	# install additional script
	if [ -n "${UBOOT_ENV}" ]; then
		install ${WORKDIR}/${UBOOT_ENV_BINARY} \
			${D}/boot/${UBOOT_ENV_IMAGE}
		ln -sf ${UBOOT_ENV_IMAGE} ${D}/boot/${UBOOT_ENV_BINARY}
	fi
}

FILES_${PN} += "/boot"
FILES_${PN}-dbg += "/boot/.debug"

inherit deploy

do_deploy() {
	# deploy core binary
	install -d ${DEPLOYDIR}
	install ${S}/${UBOOT_BINARY} ${DEPLOYDIR}/${UBOOT_IMAGE}
	rm -f ${DEPLOYDIR}/${UBOOT_BINARY} ${DEPLOYDIR}/${UBOOT_SYMLINK}
	ln -sf ${UBOOT_IMAGE} ${DEPLOYDIR}/${UBOOT_BINARY}
	ln -sf ${UBOOT_IMAGE} ${DEPLOYDIR}/${UBOOT_SYMLINK}

	# deploy SPL
	if [ -n "${SPL_BINARY}" ]; then
		install ${S}/${SPL_BINARY} ${DEPLOYDIR}/${SPL_IMAGE}
		rm -f ${DEPLOYDIR}/${SPL_BINARY} ${DEPLOYDIR}/${SPL_SYMLINK}
		ln -sf ${SPL_IMAGE} ${DEPLOYDIR}/${SPL_BINARY}
		ln -sf ${SPL_IMAGE} ${DEPLOYDIR}/${SPL_SYMLINK}
	fi

	# deploy additional script
	if [ -n "${UBOOT_ENV}" ]; then
		install ${WORKDIR}/${UBOOT_ENV_BINARY} ${DEPLOYDIR}/${UBOOT_ENV_IMAGE}
		rm -f ${DEPLOYDIR}/${UBOOT_ENV_BINARY} ${DEPLOYDIR}/${UBOOT_ENV_SYMLINK}
		ln -sf ${UBOOT_ENV_IMAGE} ${DEPLOYDIR}/${UBOOT_ENV_BINARY}
		ln -sf ${UBOOT_ENV_IMAGE} ${DEPLOYDIR}/${UBOOT_ENV_SYMLINK}
	fi
}

addtask deploy before do_build after do_compile
