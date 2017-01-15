SUMMARY = "This package contains the core Xapian runtime library"
DESCRIPTION = "\
	The Xapian search engine library is a highly adaptable toolkit which allows \
	developers to easily add advanced indexing and search facilities to their own \
	applications.  It implements the probabilistic model of information retrieval, \
	and provides facilities for performing ranked free-text searches, relevance \
	eedback, phrase searching, boolean searching, stemming, and simultaneous \
	pdate and searching.  It is highly scalable, and is capable of working with \
	collections containing hundreds of millions of documents. \
"
HOMEPAGE = "http://xapian.org/"
PR = "r1"
inherit debian-package
PV = "1.2.19"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=4325afd396febcb659c36b49533135d4"
inherit autotools

#install follow Debian jessie
do_install_append() {
	install -d ${D}${libdir}/xapian-examples/examples
	mv ${D}${bindir}/simple* ${D}${libdir}/xapian-examples/examples/
}
PACKAGES =+ "libxapian xapian-examples"

DEBIANNAME_${PN} = "xapian-tools"
DEBIANNAME_${PN}-dev = "libxapian-dev"
DEBIANNAME_${PN}-doc = "xapian-doc"
DEBIANNAME_${PN}-dbg = "libxapian22-dbg"

FILES_libxapian = "${libdir}/libxapian.so.*"
FILES_xapian-examples = "${libdir}/xapian-examples/examples/simple*"
FILES_${PN}-dev += "${libdir}/cmake/xapian/* ${bindir}/xapian-config"
FILES_${PN}-dbg += "${libdir}/xapian-examples/examples/.debug/*"

#follow debian/control
RDEPENDS_libxapian-dev += "libxapian libc6-dev"
RDEPENDS_xapian-tools += "libxapian"

# depends on uuid.h at the build time
DEPENDS += "util-linux"
