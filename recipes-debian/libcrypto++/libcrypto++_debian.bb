SUMMARY = "General purpose cryptographic library"
DESCRIPTION = "General purpose cryptographic library for C++."
HOMEPAGE = "http://www.cryptopp.com"

PR = "r0"

inherit debian-package
PV = "5.6.1"
DPN = "libcrypto--"

LICENSE = "PD"
LIC_FILES_CHKSUM = "file://License.txt;md5=1b74c85cef702b12d7e7fee0dbedeb66"

inherit autotools-brokensep pkgconfig

# Configure follow debian/rules
EXTRA_OECONF = "--config-cache --libexecdir=${libdir}"

LDFLAGS += "-pthread -lpthread"

do_configure() {
	# Autoreconf needs the automake input to be Makefile.am, so we make
	# sure the upstream GNUmakefile do not shadow the resulting Makefile.
	olddir=`pwd`
	cd ${S}
	rm -f config.cache GNUmakefile
	cd debian
	cp -f configure.ac Makefile.am config.h.in libcrypto++.pc.in ${S}
	cd $olddir
	autoreconf --force --install
	oe_runconf
}

do_install_append() {
	# Follow debian/rules and debian/libcrypto++-utils.install
	rm -f ${D}${bindir}/cryptestcwd
	mkdir -p ${D}${datadir}/crypto++
	cp -r ${B}/TestVectors ${D}${datadir}/crypto++
	chmod -x ${D}${datadir}/crypto++/TestVectors/*
	cp -r ${B}/TestData ${D}${datadir}/crypto++
	mkdir ${D}${libdir}/pkgconfig
	cp -r ${B}/*.pc ${D}${libdir}/pkgconfig

	# Follow debian/libcrypto++-dev.install
	mv ${D}${includedir}/crypto-- ${D}${includedir}/crypto++

	# Follow debian/libcrypto++-dev.links
	ln -s crypto++ ${D}${includedir}/cryptopp
	ln -s libcrypto++.a ${D}${libdir}/libcryptopp.a
	ln -s libcrypto++.so ${D}${libdir}/libcryptopp.so

	# Follow debian/libcrypto++9.links
	ln -s libcrypto++.so.9 ${D}${libdir}/libcryptopp.so.9
}

PACKAGES =+ "${PN}-utils"

FILES_${PN}-utils = " \
    ${bindir}/* \
    ${datadir}/crypto++/TestVectors \
    ${datadir}/crypto++/TestData \
"

DEBIANNAME_${PN}-dbg = "${PN}9-dbg"
