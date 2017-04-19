#
# base recipe: meta/recipes-extended/libarchive/libarchive_3.1.2.bb
# base version: jethro
#

SUMMARY = "Support for reading various archive formats"
DESCRIPTION = "C library and command-line tools for reading and writing tar, cpio, zip, ISO, and other archive formats"
HOMEPAGE = "http://www.libarchive.org/"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=b4e3ffd607d6686c6cb2f63394370841"

inherit debian-package autotools
PV = "3.1.2"
# lib_package??

# needed to build with a separated build tree
SRC_URI += "file://mkdir.patch"

# follow Debian's configuration; actual linked libraries
PACKAGECONFIG ?= "acl \
                  xattr \
                  zlib \
                  bz2 \
                  xz \
                  libxml2 \
                  lzo \
                  nettle \
                 "

# features that are optionally selectable as a back-end engine of libarchive
PACKAGECONFIG[acl] = "--enable-acl,--disable-acl,acl,"
PACKAGECONFIG[xattr] = "--enable-xattr,--disable-xattr,attr,"
PACKAGECONFIG[zlib] = "--with-zlib,--without-zlib,zlib,"
PACKAGECONFIG[bz2] = "--with-bz2lib,--without-bz2lib,bzip2,"
PACKAGECONFIG[xz] = "--with-lzmadec --with-lzma,--without-lzmadec --without-lzma,xz-utils,"
PACKAGECONFIG[openssl] = "--with-openssl,--without-openssl,openssl,"
PACKAGECONFIG[libxml2] = "--with-xml2,--without-xml2,libxml2,"
PACKAGECONFIG[expat] = "--with-expat,--without-expat,expat,"
PACKAGECONFIG[lzo] = "--with-lzo2,--without-lzo2,lzo,"
PACKAGECONFIG[nettle] = "--with-nettle,--without-nettle,nettle,"

# same as debian/rules
# --with/without options are set by PACKAGECONFIG
EXTRA_OECONF = "--enable-bsdtar=shared --enable-bsdcpio=shared"

PACKAGES =+ "bsdcpio bsdtar"

FILES_bsdcpio = "${bindir}/bsdcpio"
FILES_bsdtar = "${bindir}/bsdtar"

RDEPENDS_bsdcpio += "${PN}"
RDEPENDS_bsdtar += "${PN}"

BBCLASSEXTEND = "native nativesdk"
