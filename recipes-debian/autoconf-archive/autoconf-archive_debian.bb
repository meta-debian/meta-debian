SUMMARY = "a collection of freely re-usable Autoconf macros"
HOMEPAGE = "http://www.gnu.org/software/autoconf-archive/"
SECTION = "devel"
LICENSE = "GPL-3.0-with-autoconf-exception"
LIC_FILES_CHKSUM = "file://COPYING;md5=11cc2d3ee574f9d6b7ee797bdce4d423 \
    file://COPYING.EXCEPTION;md5=fdef168ebff3bc2f13664c365a5fb515"

inherit debian-package
require recipes-debian/sources/autoconf-archive.inc

def __split_version(d):
    pv = d.getVar("PV", True)

    # split date
    year, month, day = pv[:4], pv[4:6], pv[6:]

    return "%s.%s.%s" % (year, month, day)

UNPACK_SRC_VERSION ?= "${@__split_version(d)}"

DEBIAN_UNPACK_DIR = "${WORKDIR}/${BPN}-${UNPACK_SRC_VERSION}"


inherit autotools allarch

PACKAGES = "${PN} ${PN}-doc"

FILES_${PN} += "${datadir}/aclocal"

BBCLASSEXTEND = "native nativesdk"
