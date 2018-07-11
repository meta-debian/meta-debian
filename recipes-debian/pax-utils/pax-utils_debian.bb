#
# Base recipe: meta/recipes-devtools/pax-utils/pax-utils_1.2.2.bb
# Base branch: master
# Base commit: a5d1288804e517dee113cb9302149541f825d316
#
SUMMARY = "Security-focused ELF files checking tool"
DESCRIPTION = "This is a small set of various PaX aware and related \
utilities for ELF binaries. It can check ELF binary files and running \
processes for issues that might be relevant when using ELF binaries \
along with PaX, such as non-PIC code or executable stack and heap."
HOMEPAGE = "http://www.gentoo.org/proj/en/hardened/pax-utils.xml"

inherit debian-package
PV = "1.2.2"
DPR = "-1"
DSC_URI = "${DEBIAN_MIRROR}/main/p/${BPN}/${BPN}_${PV}${DPR}.dsc;md5sum=fb35e4bb631ea54cc1e2d415b4a3db63"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=eb723b61539feef013de476e68b5c50a"

RDEPENDS_${PN} += "bash"

export GNULIB_OVERRIDES_WINT_T = "0"

# source format is 3.0 but there is no patch
DEBIAN_QUILT_PATCHES = ""

do_configure_prepend() {
	touch ${S}/NEWS ${S}/AUTHORS ${S}/ChangeLog ${S}/README
}

do_install() {
	oe_runmake PREFIX=${D}${prefix} DESTDIR=${D} install
}

BBCLASSEXTEND = "native"

inherit autotools pkgconfig

PACKAGECONFIG ??= ""

PACKAGECONFIG[libcap] = "--with-caps, --without-caps, libcap"
PACKAGECONFIG[libseccomp] = "--with-seccomp, --without-seccomp, libseccomp"
PACKAGECONFIG[pyelftools] = "--with-python, --without-python,, pyelftools"

EXTRA_OECONF += "--enable-largefile"
