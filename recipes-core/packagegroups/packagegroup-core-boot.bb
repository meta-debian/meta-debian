SUMMARY = "Minimal boot requirements"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

PR = "r0"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

# Set by the machine configuration with packages essential for device bootup
MACHINE_ESSENTIAL_EXTRA_RDEPENDS ?= ""

# Distro can override the following VIRTUAL-RUNTIME providers:
VIRTUAL-RUNTIME_init_manager ?= "busybox"

#
# minimal package set for root filesystem
#
# kernel-modules
#   In general, modules must be installed in any minimal system
# ${VIRTUAL-RUNTIME_init_manager}
#   provides init program (busybox or systemd)
# base-files:
#   provides essential data for most system
# base-passwd:
#   provides the default passwd/group data
# update-alternatives:
#   essential for packages that require update-alternatives.bbclass
#
RDEPENDS_${PN} = "kernel-modules \
                  ${VIRTUAL-RUNTIME_init_manager} \
                  base-files \
                  base-passwd \
                  update-alternatives \
                  ${MACHINE_ESSENTIAL_EXTRA_RDEPENDS} \
                 "
