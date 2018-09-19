SUMMARY = "GNU Portability Library"
HOMEPAGE = "https://www.gnu.org/software/gnulib/"

inherit debian-package
require recipes-debian/sources/gnulib.inc
DEBIAN_UNPACK_DIR = "${WORKDIR}/${BPN}-${@d.getVar('PV', True).replace('+','-')}"

LICENSE = "GPLv3 & GPLv2 & LGPLv3 & LGPLv2 & GFDL-1.3"
LIC_FILES_CHKSUM = "file://COPYING;md5=e4cf3810f33a067ea7ccd2cd889fed21"

inherit native

# Nothing to compile
do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
	install -d ${D}${datadir}/gnulib ${D}${bindir}
	cp -a --no-preserve=ownership build-aux posix-modules config doc lib m4 \
	    modules top tests MODULES.html.sh Makefile gnulib-tool \
	    cfg.mk check-copyright ${D}${datadir}/gnulib/
	ln -sf ../share/gnulib/gnulib-tool ${D}${bindir}/
}
