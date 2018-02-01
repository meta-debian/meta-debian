#
# base recipe: meta/recipes-devtools/python/python.inc
# base branch: master
#

INC_PR = "r1"

inherit debian-package
PV = "2.7.9"

DPN = "python2.7"

LICENSE = "PSFv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=dff3d00f049545862992d2d097831a13"

DEBIAN_PATCH_TYPE = "quilt"
KEEP_NONARCH_BASELIB = "1"

PYTHON_MAJMIN = "2.7"

inherit autotools

PYTHONLSBOPTS = "--with-wctype-functions"
PYTHONLSBOPTS_linuxstdbase = "ac_cv_sizeof_off_t=8"

EXTRA_OECONF = " \
	--enable-ipv6 \
	--enable-unicode=ucs4 \
	--with-dbmliborder=bdb:gdbm \
	--with-threads \
	--with-pymalloc \
	--without-cxx-main \
	--with-signal-module \
	--enable-shared \
	ac_cv_header_bluetooth_bluetooth_h=no ac_cv_header_bluetooth_h=no \
	${PYTHONLSBOPTS} \
"

PACKAGECONFIG ??= "system-expat"
PACKAGECONFIG[system-expat] = "--with-system-expat,,expat"

# series file need to be processed before using for applying patches according
# to Debian rules
do_debian_patch_prepend() {
	cpp -E -o - ${S}/debian/patches/series.in | egrep -v '^(#.*|$$)' > ${S}/debian/patches/series
}
