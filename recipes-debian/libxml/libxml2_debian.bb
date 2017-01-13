#
# Base recipe: 
#	meta/recipes-core/libxml/libxml2.inc
#	meta/recipes-core/libxml/libxml2_2.9.2.bb
# Base branch: master
# Base commit: d9411412d2cc3ae13cb72227c6513202935012af
#

SUMMARY = "XML C Parser Library and Toolkit"
DESCRIPTION = "The XML Parser Library allows for manipulation of XML files.  Libxml2 exports Push and Pull type parser interfaces for both XML and HTML.  It can do DTD validation at parse time, on a parsed document instance or with an arbitrary DTD.  Libxml2 includes complete XPath, XPointer and Xinclude implementations.  It also has a SAX like interface, which is designed to be compatible with Expat."
HOMEPAGE = "http://www.xmlsoft.org/"
BUGTRACKER = "http://bugzilla.gnome.org/buglist.cgi?product=libxml2"

PR = "r1"
DEPENDS =+ "zlib"

inherit autotools pkgconfig binconfig pythonnative debian-package
PV = "2.9.1+dfsg1"

LICENSE = "MIT"
LIC_FILES_CHKSUM = " \
	file://Copyright;md5=2044417e2e5006b65a8b9067b683fcf1 \
	file://hash.c;beginline=6;endline=15;md5=96f7296605eae807670fb08947829969 \
	file://list.c;beginline=4;endline=13;md5=cdbfa3dee51c099edb04e39f762ee907 \
	file://trio.c;beginline=5;endline=14;md5=6c025753c86d958722ec76e94cae932e \
"

SRC_URI += " \
	file://restore-python-include-flag.patch \
	file://ansidecl.patch \
	file://libxml-64bit.patch \
	file://python-sitepackages-dir.patch \
"

BINCONFIG = "${bindir}/xml2-config"

RDEPENDS_${PN}-python += "python-core"

# We don't DEPEND on binutils for ansidecl.h so ensure we don't use the header
do_configure_prepend () {
        sed -i -e '/.*ansidecl.h.*/d' ${S}/configure.in
}

export PYTHON_SITE_PACKAGES="${PYTHON_SITEPACKAGES_DIR}"

PACKAGECONFIG ??= "python"

PACKAGECONFIG[python] = "--with-python=${PYTHON},--without-python,python"
# WARNING: zlib is require for RPM use
EXTRA_OECONF = "--without-debug --without-legacy --with-catalog --without-docbook --with-c14n --without-lzma --with-fexceptions"
EXTRA_OECONF_class-native = "--with-python=${STAGING_BINDIR}/python --without-legacy --with-catalog --without-docbook --with-c14n --without-lzma"
EXTRA_OECONF_class-nativesdk = "--with-python=${STAGING_BINDIR}/python --without-legacy --with-catalog --without-docbook --with-c14n --without-lzma"
EXTRA_OECONF_linuxstdbase = "--without-python --with-debug --with-legacy --with-catalog --with-docbook --with-c14n --without-lzma"

# required for pythong binding
export HOST_SYS
export BUILD_SYS
export STAGING_LIBDIR
export STAGING_INCDIR

export LDFLAGS += "-ldl"

python populate_packages_prepend () {
    # autonamer would call this libxml2-2, but we don't want that
    if d.getVar('DEBIAN_NAMES', True):
        d.setVar('PKG_libxml2', '${MLPREFIX}libxml2')
}

PACKAGES += "${PN}-utils ${PN}-python"

FILES_${PN}-dbg += "${PYTHON_SITEPACKAGES_DIR}/.debug"
FILES_${PN}-staticdev += "${PYTHON_SITEPACKAGES_DIR}/*.a"
FILES_${PN}-dev += "${libdir}/xml2Conf.sh"
FILES_${PN}-utils += "${bindir}/*"
FILES_${PN}-python += "${PYTHON_SITEPACKAGES_DIR}"
# Correct list of file in libxml2 package
FILES_${PN} = "${libdir}/lib*${SOLIBS}"

# Correct .deb file name
DEBIANNAME_${PN}-python = "python-libxml2"

BBCLASSEXTEND = "native nativesdk"
