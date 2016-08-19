FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

# pandaboard es rev b2 uses OMAP4460 processor 
LINUX_DEFCONFIG = "omap2plus_defconfig"

KERNEL_DEVICETREE ?= "omap4-panda.dtb omap4-panda-es.dtb"
