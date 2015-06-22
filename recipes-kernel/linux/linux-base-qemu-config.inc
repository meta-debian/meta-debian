#
# linux-base-qemu-config.inc
#
# This file defines the default kernel configuration files for QEMU targets.
# All values are not used if MACHINE is not a QEMU target.
# These variables should be defined in QEMU machine configurations, but we put
# them here instead because we cannot touch the configurations in OE-Core.
#

LINUX_DEFCONFIG_qemux86 ?= "i386_defconfig"
LINUX_CONFIG_APPEND_qemux86 ?= "${MACHINE}.config"

LINUX_DEFCONFIG_qemux86-64 ?= "x86_64_defconfig"
LINUX_CONFIG_APPEND_qemux86-64 ?= "${MACHINE}.config"

LINUX_DEFCONFIG_qemuarm ?= "versatile_defconfig"
LINUX_CONFIG_APPEND_qemuarm ?= "${MACHINE}.config"

# TODO: should be based on a defconfig
LINUX_CONFIG_qemuppc ?= "${MACHINE}.config"
