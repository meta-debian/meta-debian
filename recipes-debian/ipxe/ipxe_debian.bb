SUMMARY = "PXE boot firmware"
HOMEPAGE = "http://ipxe.org"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://../COPYING.GPLv2;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit debian-package
require recipes-debian/sources/ipxe.inc

S = "${DEBIAN_UNPACK_DIR}/src"

COMPATIBLE_HOST = '(x86_64|i.86).*-(linux|freebsd.*)'
DEPENDS = "cdrtools-native xz-native syslinux"

do_configure() {
	# Fix lzma.h and -llzma not found
	sed -i -e "s#\(^HOST_CFLAGS\s*:=.*\)#\1 ${BUILD_CFLAGS}#" ${S}/Makefile
	sed -i -e "s#\(^ZBIN_LDFLAGS\s*:=.*\)#\1 ${BUILD_LDFLAGS}#" ${S}/Makefile.housekeeping
}

build_rom() {
	oe_runmake V=1 NO_WERROR=1 VERSION=${PV} $@
}

do_compile() {
	export ISOLINUX_BIN=${STAGING_LIBDIR}${datadir}/syslinux/isolinux.bin

	targets=`grep -hoE 'src/bin(-[^/]*)?/\S+' ${DEBIAN_UNPACK_DIR}/debian/*.install \
	         | sed -e 's@src/@@'`
	mkdir -p `dirname $targets | sort -u`
	for t in $targets; do
		if echo $t | grep -q "bin-efi/.*.efirom"; then
			f=`basename $t | sed -e 's@.efirom$@@'`
			build_rom bin/$f.rom bin-x86_64-efi/$f.efirom
			${S}/util/catrom.pl bin/$f.rom bin-x86_64-efi/$f.efirom > $t
		elif echo $t | grep -q "bin-efi/.*.iso"; then
			f=`basename $t | sed -e 's@.iso$@@'`
			build_rom bin/$f.lkrn bin-x86_64-efi/$f.efi
			${S}/util/geniso -o $t bin/$f.lkrn bin-x86_64-efi/$f.efi
		else
			build_rom $t
		fi
	done
}

do_install() {
	cd ${DEBIAN_UNPACK_DIR}
	mkdir -p usr/lib/ipxe/qemu boot

	sed -e '/#/d' \
	    -e 's@^@cp -rf @' \
	    -e 's@ =>@@' debian/*.install > ipxe_install
	sed -e '/#/d' \
	    -e 's@^/usr/lib/ipxe/@@' \
	    -e 's@ /usr/lib@ usr/lib@' \
	    -e 's@^@ln -sf @' debian/*.links > ipxe_link
	chmod +x ipxe_install ipxe_link
	./ipxe_install
	./ipxe_link
}

do_install_append_class-target() {
	mv usr boot etc ${D}/
}

do_install_append_class-native() {
	install -d ${D}${STAGING_DIR_NATIVE}
	mv usr ${D}${STAGING_DIR_NATIVE}/
}

PACKAGES =+ "${PN}-qemu"

FILES_${PN} += "/boot"
FILES_${PN}-qemu = " \
    ${libdir}/ipxe/qemu \
    ${libdir}/ipxe/e1000_82540.rom \
    ${libdir}/ipxe/82540em.rom \
    ${libdir}/ipxe/eepro100.rom \
    ${libdir}/ipxe/ns8390.rom \
    ${libdir}/ipxe/pcnet32.rom \
    ${libdir}/ipxe/rtl8139.rom \
    ${libdir}/ipxe/virtio-net.rom \
"

BBCLASSEXTEND = "native"
