SUMMARY = "Python module to control and program the FTDI USB controller"
DESCRIPTION = "\
	This library could talk to FTDI's FT232 and FT245 type USB chips from \
	userspace. It uses libusb to communicate with the chips. \
	Functionalities include the possibility to use the chips in standard \
	mode, in bitbang mode, and to read or write the serial EEPROM. \
"
HOMEPAGE = "http://www.intra2net.com/en/developer/libftdi/"

PR = "r3"
DPN = "libftdi"
inherit debian-package
PV = "0.20"

LICENSE = "GPLv2+ & LGPLv2+"
LIC_FILES_CHKSUM = "\
	file://LICENSE;md5=2e20d74de059b32006dc58fafdfa59b0 \
	file://COPYING.LIB;md5=db979804f025cf55aabec7129cb671ed \
	file://COPYING.GPL;md5=751419260aa954499f7abaabaa882bbe"
inherit autotools-brokensep pkgconfig pythonnative

#export some variable from poky, to use for python command
export BUILD_SYS
export HOST_SYS
export STAGING_INCDIR
export STAGING_LIBDIR

DEPENDS += "libusb1 swig-native"

EXTRA_OECONF += "\
	--disable-libftdipp --enable-python-binding \
	PYTHON=${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} \
"
do_compile_prepend() {
	sed -i -e "s:CPPFLAGS=\"\$(CPPFLAGS)\":CPPFLAGS=\"\$(CPPFLAGS) ${HOST_CC_ARCH}\":g" \
		${S}/bindings/python/Makefile
}

do_install_append() {
	#remove these files conflicts with libftdi
	rm -r ${D}${includedir} ${D}${bindir} ${D}${libdir}/pkgconfig
	rm ${D}${libdir}/libftdi* 
}

FILES_${PN}-dbg += "${libdir}/python2.7/dist-packages/.debug/*"
FILES_${PN} = "${libdir}/python2.7/dist-packages/*"
PKG_${PN} = "python-ftdi"
