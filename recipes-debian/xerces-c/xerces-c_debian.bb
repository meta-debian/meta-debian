SUMMARY = "Validating XML parser library for C++"
DESCRIPTION = "\
	Xerces-C++ is a validating XML parser written in a portable subset of C++.\
	Xerces-C++ makes it easy to give your application the ability to read and \
	write XML data. A shared library is provided for parsing, generating,\
	manipulating, and validating XML documents. Xerces-C++ is faithful to \
	the XML 1.0 recommendation and associated standards (DOM 1.0, DOM 2.0,\
	SAX 1.0, SAX 2.0, Namespaces, XML Schema Part 1 and Part 2). \
	It also provides experimental implementations of XML 1.1 and DOM Level 3.0. \
	The parser provides high performance, modularity,and scalability.\ "

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

PR = "r0"

inherit debian-package
inherit autotools
PV = "3.1.1"

INSANE_SKIP_${PN} = "dev-deps"
INSANE_SKIP_libxerces-c-samples = "dev-deps"

PACKAGECONFIG ??= "curl icu"
PACKAGECONFIG[curl] = "--with-curl=${STAGING_DIR},--with-curl=no,curl"
PACKAGECONFIG[icu] = "--with-icu=${STAGING_DIR},--with-icu=no,icu"

PACKAGES =+ "libxerces-c-samples"

FILES_libxerces-c-samples =+ " \
	${bindir}/*"
FILES_${PN} += " \
	${libdir}/libxerces-c-3.1.so"
FILES_${PN}-dev = " \
	${libdir}/*.la \
	${libdir}/pkgconfig \
	${libdir}/libxerces-c.so \
	${includedir}"

PKG_${PN}-dev = "libxerces-c-dev"
PKG_${PN} = "libxerces-c3.1"
RPROVIDES_${PN}-dev += "libxerces-c-dev"
RPROVIDES_${PN} += "libxerces-c3.1"
RDEPENDS_${PN}-dev += "libicu-dev libc-dev"
