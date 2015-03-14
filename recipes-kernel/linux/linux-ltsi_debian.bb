DESCRIPTION = "The LTSI is an ecosystem-wide collaborative project hosted at \
the Linux Foundation to create and maintain a common Linux base for the use \
in a variety of CE products and to enable faster contributions upstream and \
better alignment with the mainline kernel."

SECTION = "kernel"

inherit kernel kernel-defconfig

require linux-shared-source.inc
require linux-ltsi-common.inc

LINUX_CONF = "${SW}/test.defconfig"
defconfig[dirs] = "${SW}"
do_defconfig[depends] += " \
	linux-ltsi-source:do_patch \
	linux-libc-headers:do_install \
"
B = "${WORKDIR}/build"

DEPENDS += "bc-native"

# Required to make 'KERNEL_IMAGETYPE = "vmlinux"' available
do_compile_append() {
        mkdir -p ${B}/arch/${ARCH}/boot
	cd ${B}/arch/${ARCH}/boot
        ln -sf ../../../vmlinux vmlinux
}
