#
# base recipe: meta/recipes-core/expat/expat_2.2.5.bb
# base branch: master
# base commit: d886fa118c930d0e551f2a0ed02b35d08617f746
#
SUMMARY = "A stream-oriented XML parser library"
DESCRIPTION = "Expat is an XML parser library written in C. It is a stream-oriented \
parser in which an application registers handlers for things the parser might find \
in the XML document (like start tags)"

inherit debian-package
PV = "2.2.5"
DPR = "-3"
DSC_URI = "${DEBIAN_MIRROR}/main/e/${BPN}/${BPN}_${PV}${DPR}.dsc;md5sum=afd13bf06215d7858e0ce77cc86a1e55"

DEBIAN_UNPACK_DIR = "${WORKDIR}/libexpat-R_2_2_5"
S = "${DEBIAN_UNPACK_DIR}/expat"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=5b8620d98e49772d95fc1d291c26aa79"

# Empty DEBIAN_QUILT_PATCHES to avoid error "debian/patches not found"
DEBIAN_QUILT_PATCHES = ""

#  Don't build doc to reduce dependency, it depends on docbook-to-man
SRC_URI += "file://disable-build-doc.patch \
            file://autotools.patch"

FILESEXTRAPATHS =. "${FILE_DIRNAME}/files:${COREBASE}/meta/recipes-core/expat/expat:"

inherit autotools lib_package

do_configure_prepend () {
	rm -f ${S}/conftools/libtool.m4
}

BBCLASSEXTEND = "native nativesdk"
