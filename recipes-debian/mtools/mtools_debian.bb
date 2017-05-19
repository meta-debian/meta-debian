#
# base recipe: meta/recipes-devtools/mtools/mtools_4.0.18.bb
# base branch: jehro
#

SUMMARY = "Tools for manipulating MSDOS files"
DESCRIPTION = "Mtools is a collection of utilities to access MS-DOS disks from Unix \
without mounting them. It supports Win'95 style long file names, OS/2 \
Xdf disks, ZIP/JAZ disks and 2m disks (store up to 1992kB on a high \
density 3 1/2 disk)."
HOMEPAGE = "http://www.gnu.org/software/mtools/"

inherit debian-package
PV = "4.0.18"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI += "file://0001-Continue-even-if-fs-size-is-not-divisible-by-sectors.patch"

inherit autotools texinfo

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11 floppyd', '', d)}"
PACKAGECONFIG[libbsd] = "ac_cv_lib_bsd_main=yes,ac_cv_lib_bsd_main=no,libbsd"
PACKAGECONFIG[floppyd] = "--enable-floppyd,--disable-floppyd,"
PACKAGECONFIG[x11] = "--with-x,--without-x,libx11"

do_install_prepend() {
	# Create bindir to fix parallel installation issues
	install -d ${D}${bindir} \
	           ${D}${datadir}
}

do_install_append() {
	# According to debian/mtools.install
	install -D -m 0644 ${S}/mtools.conf ${D}${docdir}/mtools/examples/mtools.conf
	install -D -m 0644 ${S}/debian/mtools.conf ${D}${sysconfdir}/mtools.conf
}

do_install_append_class-native() {
	create_wrapper ${D}${bindir}/mcopy \
	    GCONV_PATH=${libdir}/gconv
}

PACKAGES =+ "floppyd"
FILES_floppyd = "${bindir}/floppyd*"

RDEPENDS_${PN} = "glibc-gconv-ibm850"
RRECOMMENDS_${PN} = "\
	glibc-gconv-ibm437 \
	glibc-gconv-ibm737 \
	glibc-gconv-ibm775 \
	glibc-gconv-ibm851 \
	glibc-gconv-ibm852 \
	glibc-gconv-ibm855 \
	glibc-gconv-ibm857 \
	glibc-gconv-ibm860 \
	glibc-gconv-ibm861 \
	glibc-gconv-ibm862 \
	glibc-gconv-ibm863 \
	glibc-gconv-ibm865 \
	glibc-gconv-ibm866 \
	glibc-gconv-ibm869 \
	"

BBCLASSEXTEND = "native nativesdk"
