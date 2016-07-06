FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

LINUX_DEFCONFIG_vmware-32 ?= "i386_defconfig"
LINUX_DEFCONFIG_vmware-64 ?= "x86_64_defconfig"

SRC_URI += "file://vmware.config"
