#
# linux-base-qemu-config.inc
#
# This file defines the default kernel configuration files for QEMU targets.
# All values are not used if MACHINE is not a QEMU target.
# These variables should be defined in QEMU machine configurations, but we put
# them here instead because we cannot touch the configurations in OE-Core.
#

LINUX_DEFCONFIG_qemuarmhf-ca9 ?= "vexpress_defconfig"
SRC_URI_append_qemuarmhf-ca9 = " file://qemu.config"
KERNEL_DEVICETREE_qemuarmhf-ca9 ?= "vexpress-v2p-ca9.dtb"

LINUX_DEFCONFIG_qemuarmhf-ca15 ?= "vexpress_defconfig"
SRC_URI_append_qemuarmhf-ca15 = " file://qemu.config"
KERNEL_DEVICETREE_qemuarmhf-ca15 ?= "vexpress-v2p-ca15-tc1.dtb"
