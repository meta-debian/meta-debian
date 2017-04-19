#
# linux-base-qemu-config.inc
#
# This file defines the default kernel configuration files for QEMU targets.
# All values are not used if MACHINE is not a QEMU target.
# These variables should be defined in QEMU machine configurations, but we put
# them here instead because we cannot touch the configurations in OE-Core.
#

LINUX_DEFCONFIG_qemux86 ?= "i386_defconfig"
SRC_URI_append_qemux86 = " file://qemu.config"

LINUX_DEFCONFIG_qemux86-64 ?= "x86_64_defconfig"
SRC_URI_append_qemux86-64 = " file://qemu.config"

LINUX_DEFCONFIG_qemuarm ?= "versatile_defconfig"
SRC_URI_append_qemuarm = " file://qemu.config file://${MACHINE}.config"

LINUX_DEFCONFIG_qemumips ?= "malta_defconfig"
SRC_URI_append_qemumips = " file://qemu.config file://${MACHINE}.config"

LINUX_DEFCONFIG_qemuarm64 ?= "defconfig"
SRC_URI_append_qemuarm64 = " file://qemu.config"

# TODO: should be based on a defconfig
LINUX_DEFCONFIG_qemuppc ?= ""
SRC_URI_append_qemuppc = " file://${MACHINE}.config"
