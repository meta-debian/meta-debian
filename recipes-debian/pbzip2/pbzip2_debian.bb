#
# base recipe: meta/recipes-extended/pbzip2/pbzip2_1.1.12.bb
# base version: jethro
#

SUMMARY = "PBZIP2 is a parallel implementation of bzip2"
DESCRIPTION = "PBZIP2 is a parallel implementation of the bzip2 block-sorting \
file compressor that uses pthreads and achieves near-linear speedup on SMP \
machines. The output of this version is fully compatible with bzip2 v1.0.2 or \
newer (ie: anything compressed with pbzip2 can be decompressed with bzip2)."
HOMEPAGE = "http://compression.ca/pbzip2/"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=a0103c113a3613a95e551b86ad99d63d"

inherit debian-package
PV = "1.1.9"

# source format is 3.0 but there is no patch
DEBIAN_QUILT_PATCHES = ""

DEPENDS = "bzip2"
DEPENDS_append_class-native = " bzip2-replacement-native"

do_configure[noexec] = "1"

EXTRA_OEMAKE = "CXX='${CXX} ${CXXFLAGS}' LDFLAGS='${LDFLAGS}'"

do_install() {
	install -d ${D}${bindir}
	install -m 0755 pbzip2 ${D}${bindir}
}

BBCLASSEXTEND = "native"
