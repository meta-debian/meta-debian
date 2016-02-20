SUMMARY = "Shared library reduction script"
DESCRIPTION = "\
	mklibs produces cut-down shared libraries that contain only the	\
	routines required by a particular set of executables.  This is	\
	useful when building closed systems where disk space is at a premium, \
	such as the Debian installer. \
	"
PR = "r0"
inherit debian-package

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "\
	file://src/mklibs-readelf/elf.cpp;md5=eac8712efb9999f3ad0606c57d13424e \
	"
inherit autotools-brokensep gettext pkgconfig
DEPENDS += "libtimedate-perl-native"

PACKAGES =+ "${PN}-copy"

FILES_${PN}-copy += "\
	${bindir}/mklibs-copy ${bindir}/mklibs-readelf \
	${libdir}/mklibs/* ${docdir}/mklibs-copy/* \
	${mandir}/man1/mklibs-copy.1.gz ${mandir}/man1/mklibs-readelf.1.gz \
"
