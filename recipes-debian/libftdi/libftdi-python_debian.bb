SUMMARY = "Python module to control and program the FTDI USB controller"
DESCRIPTION = "\
	This library could talk to FTDI's FT232 and FT245 type USB chips from \
	userspace. It uses libusb to communicate with the chips. \
	Functionalities include the possibility to use the chips in standard \
	mode, in bitbang mode, and to read or write the serial EEPROM. \
"
HOMEPAGE = "http://www.intra2net.com/en/developer/libftdi/"

PR = "r1"
DPN = "libftdi"
inherit debian-package

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
	#remove the unwanted files
	for file in ${D}${bindir}/*
	do
		[ $file != ${D}${bindir}/libftdi-config ] && rm $file
	done
	rm ${D}${libdir}/python2.7/dist-packages/libftdi-0.20.egg-info \
		${D}${libdir}/python2.7/dist-packages/ftdi.pyc ${D}${libdir}/*.la
}
PACKAGES =+ "python-ftdi"

FILES_${PN}-dbg += "${libdir}/python2.7/dist-packages/.debug/*"
FILES_python-ftdi = "${libdir}/python2.7/dist-packages/*"
FILES_${PN}-dev += "${bindir}/${DPN}-config"
PKG_${PN} = "${DPN}1"
PKG_${PN}-dev = "${DPN}-dev"
PKG_${PN}-dbg = "${DPN}1-dbg"
