LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=a2c678cfd4a4d97135585cad908541c6"

# global variables to control target source code
UBOOT_GIT_REPO ?= "${BOOTLOADER_GIT_URI}/${BOOTLOADER_GIT_PREFIX}u-boot.git;branch=master;protocol=${BOOTLOADER_GIT_PROTOCOL}"
# latest stable: v2016.07
UBOOT_GIT_SRCREV ?= "19ce924ff914f315dc2fdf79f357825c513aed6e"

SRC_URI = "${UBOOT_GIT_REPO}"
SRCREV = "${UBOOT_GIT_SRCREV}"
PV = "git${SRCPV}"

S = "${WORKDIR}/git"
