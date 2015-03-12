DESCRIPTION = "The LTSI is an ecosystem-wide collaborative project hosted at \                                                                                                     the Linux Foundation to create and maintain a common Linux base for the use \                                                                                                      in a variety of CE products and to enable faster contributions upstream and \   
better alignment with the mainline kernel."

SECTION = "kernel"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

inherit kernel kernel-checkout kernel-defconfig

LINUX_SRCREV = "linux-3.10.y-zynq-backport"
LINUX_VERSION = "3.10.24"

LINUX_CONF = "${WORKDIR}/test.defconfig"
SRC_URI += "file://test.defconfig"

DEPENDS += "bc-native"

do_compile[depends] += "bc-native:do_populate_sysroot"

# Required to make 'KERNEL_IMAGETYPE = "vmlinux"' available
do_compile_append() {
        mkdir -p ${B}/arch/${ARCH}/boot
	cd ${B}/arch/${ARCH}/boot
        ln -sf ../../../vmlinux vmlinux
}
