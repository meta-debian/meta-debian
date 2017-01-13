SUMMARY = "audio extraction tool for sampling CDs"
DESCRIPTION = "\
An audio extraction tool for sampling CDs. Unlike similar programs such as \
cdda2wav, cdparanoia goes to great lengths to try to extract the audio \
information without any artifacts such as jitter. \
"
HOMEPAGE = "http://www.xiph.org/paranoia/"
PR = "r0"
inherit debian-package
PV = "3.10.2+debian"

LICENSE = "GPLv2+ & LGPLv2.1+"
LIC_FILES_CHKSUM = "\
	file://COPYING-GPL;md5=1ed9d357695b2e3ef099df37fed63d96 \
	file://COPYING-LGPL;md5=d370feaa1c9edcdbd29ca27ea3d2304d"

#Destination to install is $(DESTDIR), not install to host machine
SRC_URI += "file://Correct-destdir-to-install_debian.patch"
inherit autotools-brokensep
PARALLEL_MAKE = ""

PACKAGES =+ "lib${PN}"

FILES_lib${PN} = "\
	${libdir}/libcdda_interface.so.* ${libdir}/libcdda_paranoia.so.*"

PKG_lib${PN} = "lib${PN}0"
PKG_${PN}-dev = "lib${PN}-dev"
