#
# base recipe: meta/recipes-bsp/usbutils/usbutils_007.bb
# base branch: daisy
#

SUMMARY = "Host side USB console utilities"
DESCRIPTION = "Contains the lsusb utility for inspecting the devices connected to the USB bus."
HOMEPAGE = "http://www.linux-usb.org"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

PR = "r0"
inherit debian-package
PV = "007"

DEPENDS = "libusb1 zlib virtual/libiconv"

inherit autotools gettext pkgconfig

# Follow debian/rules
EXTRA_OECONF += " \
	--datadir=${localstatedir}/lib/${DPN} \
	--mandir=${mandir} \
	--infodir=${infodir} \
"

do_install_append() {
	mv ${D}${sbindir}/update-usbids.sh ${D}${sbindir}/update-usbids

	install -d ${D}${datadir}/misc
	ln -sf ../../..${localstatedir}/lib/${DPN}/usb.ids ${D}${datadir}/misc/usb.ids
}

FILES_${PN} += "${datadir}/misc/"
