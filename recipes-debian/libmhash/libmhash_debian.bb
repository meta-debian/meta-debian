# base recipe: meta-security/recipes-security/libmhash/libmhash_0.9.9.9.bb
# base branch: warrior

SUMMARY = "Library of hashing algorithms."
DESCRIPTION = "\
  Mhash is a free (under GNU Lesser GPL) library \
  which provides a uniform interface to a large number of hash \
  algorithms. These algorithms can be used to compute checksums, \
  message digests, and other signatures. \
  "
HOMEPAGE = "http://mhash.sourceforge.net/"

LICENSE = "LGPLv2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=3bf50002aefd002f49e7bb854063f7e7"

inherit debian-package
require recipes-debian/sources/mhash.inc

DEBIAN_UNPACK_DIR = "${WORKDIR}/mhash-${PV}.orig"

SECTION = "libs"

SRC_URI += "file://Makefile.test \
    file://mhash.c \
    file://run-ptest \
    "

inherit autotools-brokensep ptest pkgconfig

do_compile_ptest() {
    if [ ! -d ${S}/demo ]; then mkdir ${S}/demo; fi
    cp ${WORKDIR}/Makefile.test ${S}/demo/Makefile
    cp ${WORKDIR}/mhash.c ${S}/demo/
    make -C ${S}/demo CFLAGS="${CFLAGS} -I${S}/include/" LDFLAGS="${LDFLAGS} -L${S}/lib/.libs"
}

do_install_ptest() {
    install -m 0755 ${S}/demo/mhash ${D}${PTEST_PATH}
}
