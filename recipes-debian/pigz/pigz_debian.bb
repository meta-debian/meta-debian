SUMMARY = "A parallel implementation of gzip"
DESCRIPTION = "pigz, which stands for parallel implementation of gzip, is a \
fully functional replacement for gzip that exploits multiple processors and \
multiple cores to the hilt when compressing data. pigz was written by Mark \
Adler, and uses the zlib and pthread libraries."
HOMEPAGE = "http://zlib.net/pigz/"
SECTION = "console/utils"
LICENSE = "Zlib & Apache-2.0"
LIC_FILES_CHKSUM = "file://pigz.c;md5=9ae6dee8ceba9610596ed0ada493d142;beginline=7;endline=21"

inherit debian-package
require recipes-debian/sources/pigz.inc

# Point this at the homepage in case /fossils/ isn't updated
UPSTREAM_CHECK_URI = "http://zlib.net/${BPN}/"
UPSTREAM_CHECK_REGEX = "pigz-(?P<pver>.*)\.tar"

DEPENDS = "zlib"

EXTRA_OEMAKE = "-e MAKEFLAGS="

do_install() {
	# Install files into /bin (FHS), which is typical place for gzip
	install -d ${D}${base_bindir}
	install ${B}/pigz ${D}${base_bindir}/pigz
	ln -nsf pigz ${D}${base_bindir}/unpigz
	ln -nsf pigz ${D}${base_bindir}/pigzcat
}

BBCLASSEXTEND = "native nativesdk"
