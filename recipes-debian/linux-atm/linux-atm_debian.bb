#
# base recipe: https://github.com/openembedded/meta-openembedded/blob/master/meta-networking/recipes-support/linux-atm/linux-atm_2.5.2.bb
# base branch: master
#

SUMMARY = "Drivers and tools to support ATM networking under Linux"
HOMEPAGE = "http://linux-atm.sourceforge.net/"
SECTION = "libs"

PR = "r0"
inherit debian-package
PV = "2.5.1"

LICENSE = "GPL-2.0 & LGPL-2.0"

DEPENDS = "virtual/kernel flex flex-native"

# Add LDFLAGS_FOR_BUILD when doing link for qgen
SRC_URI += " file://link-with-ldflags.patch"

EXTRA_OECONF += " --libdir=${base_libdir} "

LIC_FILES_CHKSUM = " \
	file://COPYING;md5=d928de9537d846935a98af3bbc6e6ee1 \
	file://COPYING.GPL;md5=ac2db169b9309e240555bc77be4f1a33 \
	file://COPYING.LGPL;md5=6e29c688d912da12b66b73e32b03d812 \
    "

inherit autotools-brokensep pkgconfig

PARALLEL_MAKE = ""

do_install_append (){
	install -d ${D}${sysconfdir}/init.d
	install -d ${D}${libdir}
	install -d ${D}${base_sbindir}

	install -m 0755 ${S}/debian/atm-tools.atm ${D}${sysconfdir}/init.d/atm
	mv ${D}${base_libdir}/libatm.a ${D}${libdir}/
	mv ${D}${sbindir}/atmarp* ${D}${base_sbindir}/	

	LINKLIB=$(basename $(readlink ${D}${base_libdir}/libatm.so))
	rm ${D}${base_libdir}/libatm.so
	ln -s ../../lib/${LINKLIB} ${D}${libdir}/libatm.so
}


PACKAGES =+ " br2684ctl libatm libatm-dev atm-tools "

FILES_br2684ctl = " ${sbindir}/br2684ctl"

FILES_libatm = " ${base_libdir}/libatm.so.1* "

FILES_libatm-dev = " \
	${includedir}/* \
	${libdir}/libatm.so \
    "

FILES_atm-tools = " \
	${sysconfdir}/ \
	${base_sbindir}/* \
	${sbindir}/* \
	${bindir}/* \
    "
DEBIANNAME_libatm-dev = "libatm1-dev"
