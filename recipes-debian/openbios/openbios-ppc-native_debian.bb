require openbios.inc

SUMMARY = "PowerPC Open Firmware"

# openbios requires cross compiler which is not avaiable on native environment,
# so just reuse output files from target recipe
DEPENDS = "openbios-ppc"

inherit native

do_fetch[noexec] = "1"
do_unpack[noexec] = "1"
do_patch[noexec] = "1"
do_debian_patch[noexec] = "1"
do_populate_lic[noexec] = "1"
do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
	install -d ${D}${datadir}/${BPN} ${D}${datadir}/qemu
	install -m 0644 ${WORKDIR}/recipe-sysroot/usr/share/${BPN}/openbios-ppc ${D}${datadir}/${BPN}/openbios-ppc
	ln -sf ../${BPN}/openbios-ppc ${D}${datadir}/qemu/
}
