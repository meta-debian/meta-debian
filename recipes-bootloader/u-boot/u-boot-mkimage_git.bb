#
# based on meta/recipes-bsp/u-boot/u-boot-mkimage_2015.07.bb in jethro
# all definitions are same as the base recipe except u-boot.inc
#

require u-boot.inc

DEPENDS = "openssl"

EXTRA_OEMAKE = 'CROSS_COMPILE="${TARGET_PREFIX}" CC="${CC} ${CFLAGS} ${LDFLAGS}" STRIP=true V=1'

do_compile () {
	oe_runmake sandbox_defconfig
	oe_runmake cross_tools NO_SDL=1
}

do_install () {
	install -d ${D}${bindir}
	install -m 0755 tools/mkimage ${D}${bindir}/uboot-mkimage
	ln -sf uboot-mkimage ${D}${bindir}/mkimage
}

BBCLASSEXTEND = "native nativesdk"
