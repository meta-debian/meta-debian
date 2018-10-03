#
# base recipe: meta/recipes-core/libxml/libxml2_2.9.8.bb
# base branch: master
# base commit: 63a4ff7cf5f7d1671ab85800bc2212dd9cd9748d
#

SUMMARY = "XML C Parser Library and Toolkit"
DESCRIPTION = "The XML Parser Library allows for manipulation of XML files. \
Libxml2 exports Push and Pull type parser interfaces for both XML and HTML. \
It can do DTD validation at parse time, on a parsed document instance \
or with an arbitrary DTD.  Libxml2 includes complete XPath, XPointer \
and Xinclude implementations.  It also has a SAX like interface, \
which is designed to be compatible with Expat."
HOMEPAGE = "http://www.xmlsoft.org/"
BUGTRACKER = "http://bugzilla.gnome.org/buglist.cgi?product=libxml2"

inherit debian-package
require recipes-debian/sources/libxml2.inc

LICENSE = "MIT"
LIC_FILES_CHKSUM = " \
	file://Copyright;md5=2044417e2e5006b65a8b9067b683fcf1 \
	file://hash.c;beginline=6;endline=15;md5=96f7296605eae807670fb08947829969 \
	file://list.c;beginline=4;endline=13;md5=cdbfa3dee51c099edb04e39f762ee907 \
	file://trio.c;beginline=5;endline=14;md5=6c025753c86d958722ec76e94cae932e \
"

DEPENDS = "zlib virtual/libiconv"

FILESPATH_append = ":${COREBASE}/meta/recipes-core/libxml/libxml2"
SRC_URI += " \
	file://restore-python-include-flag.patch \
	file://python-sitepackages-dir.patch \
	file://libxml-m4-use-pkgconfig.patch \
"

BINCONFIG = "${bindir}/xml2-config"

PACKAGECONFIG ??= "python \
    ${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)} \
"
PACKAGECONFIG[python] = "--with-python=${PYTHON},--without-python,python3"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"

inherit autotools pkgconfig binconfig-disabled

inherit ${@bb.utils.contains('PACKAGECONFIG', 'python', 'python3native', '', d)}

RDEPENDS_${PN}-python += "${@bb.utils.contains('PACKAGECONFIG', 'python', 'python3-core', '', d)}"

export PYTHON_SITE_PACKAGES="${PYTHON_SITEPACKAGES_DIR}"

# WARNING: zlib is required for RPM use
EXTRA_OECONF = "--without-debug --without-legacy --with-catalog --without-docbook --with-c14n --without-lzma --with-fexceptions"
EXTRA_OECONF_class-native = "--without-legacy --without-docbook --with-c14n --without-lzma --with-zlib"
EXTRA_OECONF_class-nativesdk = "--without-legacy --without-docbook --with-c14n --without-lzma --with-zlib"
EXTRA_OECONF_linuxstdbase = "--with-debug --with-legacy --with-docbook --with-c14n --without-lzma --with-zlib"

python populate_packages_prepend () {
    # autonamer would call this libxml2-2, but we don't want that
    if d.getVar('DEBIAN_NAMES'):
        d.setVar('PKG_libxml2', '${MLPREFIX}libxml2')
}

PACKAGES += "${PN}-utils ${PN}-python"

FILES_${PN}-staticdev += "${PYTHON_SITEPACKAGES_DIR}/*.a"
FILES_${PN}-dev += "${libdir}/xml2Conf.sh ${libdir}/cmake/*"
FILES_${PN}-utils += "${bindir}/*"
FILES_${PN}-python += "${PYTHON_SITEPACKAGES_DIR}"

do_install_append_class-native () {
	# Docs are not needed in the native case
	rm ${D}${datadir}/gtk-doc -rf
}

BBCLASSEXTEND = "native nativesdk"
