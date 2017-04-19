#
# base recipe: meta/recipes-devtools/python/python.inc
# base branch: jethro
#

SUMMARY = "The Python Programming Language"
HOMEPAGE = "http://www.python.org"
LICENSE = "PSFv2"
SECTION = "devel/python"

INC_PR = "r1"
DPN = "python3.4"
inherit debian-package
PV = "3.4.2"

LIC_FILES_CHKSUM = "file://LICENSE;md5=64fc2b30b67d0a8423c250e0386ed72f"

PYTHON_MAJMIN = "3.4"

SRC_URI += "file://add_site-packages_to_getsitepackages.patch"

inherit autotools

PYTHONLSBOPTS_linuxstdbase = ""
EXTRA_OECONF = " \
		--enable-ipv6 \
		--enable-loadable-sqlite-extensions \
		--with-dbmliborder=bdb:gdbm \
		--with-computed-gotos \
		--without-ensurepip \
		--with-system-expat \
		--with-system-libmpdec \
		--with-threads \
		--with-pymalloc \
		--without-cxx-main \
		--with-signal-module \
		--enable-shared \
"

# create file series for apply patch file of debian
do_debian_patch_prepend() {
	# create series file from series.in file
	cpp -E \
		-D${DISTRO} \
		-Darch_os_${HOST_OS} -Darch_${HOST_ARCH} \
		-o - ${S}/debian/patches/series.in \
		| egrep -v '^(#.*|$$)' > ${S}/debian/patches/series
}
