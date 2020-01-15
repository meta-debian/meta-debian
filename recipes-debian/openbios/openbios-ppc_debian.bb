require openbios.inc

SUMMARY = "PowerPC Open Firmware"

DEPENDS = "fcode-utils-native libxslt-native"

COMPATIBLE_HOST = "(powerpc).*-linux"

do_configure() {
	# uninative does not provide ISO-8859-15
	sed -i -e "s/ISO-8859-15/ISO-8859-1/g" ${S}/config/xml/*

	CROSS_COMPILE=${TARGET_PREFIX} config/scripts/switch-arch builtin-ppc
}

do_compile() {
	oe_runmake -C ${S}/obj-ppc V=1 EXTRACFLAGS="-ffreestanding -fno-pic -fno-stack-protector"
}

do_install() {
	install -d ${D}${datadir}/${BPN} ${D}${datadir}/qemu
	install -m 0644 ${S}/obj-ppc/openbios-qemu.elf ${D}${datadir}/${BPN}/openbios-ppc
	ln -sf ../${BPN}/openbios-ppc ${D}${datadir}/qemu/
}

FILES_${PN} += "${datadir}/qemu"
