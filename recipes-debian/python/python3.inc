#
# base recipe: meta/recipes-devtools/python/python.inc
# base branch: jethro
#

SUMMARY = "The Python Programming Language"
HOMEPAGE = "http://www.python.org"
LICENSE = "PSFv2"
SECTION = "devel/python"

INC_PR = "r0"
DPN = "python3.4"
inherit debian-package

LIC_FILES_CHKSUM = "file://LICENSE;md5=64fc2b30b67d0a8423c250e0386ed72f"

PYTHON_MAJMIN = "3.4"
DISTRO_SRC_URI ?= "file://sitecustomize.py"

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
