#
# base recipe: meta/recipes-devtools/python/python.inc
# base branch: master
#

INC_PR = "r0"

inherit debian-package

DPN = "python2.7"

LICENSE = "PSFv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=dff3d00f049545862992d2d097831a13"

DEBIAN_PATCH_TYPE = "quilt"

PYTHON_MAJMIN = "2.7"

inherit autotools

PYTHONLSBOPTS = "--with-wctype-functions"
PYTHONLSBOPTS_linuxstdbase = "ac_cv_sizeof_off_t=8"

EXTRA_OECONF = " \
	--enable-ipv6 \
	--enable-unicode=ucs4 \
	--with-dbmliborder=bdb:gdbm \
	--with-system-expat \
	--with-threads \
	--with-pymalloc \
	--without-cxx-main \
	--with-signal-module \
	--enable-shared \
	ac_cv_header_bluetooth_bluetooth_h=no ac_cv_header_bluetooth_h=no \
	${PYTHONLSBOPTS} \
"
