SUMMARY = "hardware identification / configuration data"
DESCRIPTION = "\
	This package contains various hardware identification and \
	configuration data, such as the pci.ids database, or the XFree86/xorg \
	Cards database.It is needed for the kudzu hardware detection.\
"
HOMEPAGE = "http://git.fedorahosted.org/git/hwdata.git"
PR = "r0"
inherit debian-package
PV = "0.267"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263\
	file://LICENSE;md5=1556547711e8246992b999edd9445a57"
inherit autotools-brokensep
DEBIAN_PATCH_TYPE = "nopatch"

do_install_append() {
	#remove unwanted files
	rm ${D}${datadir}/${PN}/*.txt
	rm -r ${D}${libdir}
}
RDEPENDS_${PN} += "usbutils pciutils"
