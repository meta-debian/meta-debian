#
# base recipe: meta/recipes-core/expat/expat_2.1.0.bb
# base branch: daisy
#

PR = "r0"

SUMMARY = "A stream-oriented XML parser library"
DESCRIPTION = "Expat is an XML parser library written in C. It is a stream-oriented parser in which an application registers handlers for things the parser might find in the XML document (like start tags)"

inherit debian-package
PV = "2.1.0"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=1b71f681713d1256e1c23b0890920874"

inherit autotools gzipnative

# This package uses an archive format known to have issue with some
# versions of gzip
do_unpack[depends] += "gzip-native:do_populate_sysroot"

do_configure_prepend () {
        rm -f ${S}/conftools/libtool.m4
}

PACKAGES =+ "lib${PN}"

FILES_lib${PN} = "${libdir}/lib*${SOLIBS}"

DEBIANNAME_${PN}-dev = "lib${PN}1-dev"

# expat-dev is equal to libexpat-dev
RPROVIDES_${PN}-dev += "lib${PN}-dev"

BBCLASSEXTEND = "native nativesdk"
