FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
SRC_URI_append = " \
	file://base.config \
"

SRC_URI_append_qemuall += "file://qemu-emlinux.config"

SRC_URI_append_raspberrypi3-64 += "file://raspberrypi3-64.config"
LINUX_DEFCONFIG_raspberrypi3-64 = "defconfig"
KERNEL_IMAGETYPE_raspberrypi3-64 = "Image"
KERNEL_DEVICETREE_raspberrypi3-64 = "broadcom/bcm2837-rpi-3-b-plus.dtb \
                                     broadcom/bcm2837-rpi-3-b.dtb"
