#
# base recipe: meta/recipes-devtools/libtool/libtool-2.4.6.inc
# base branch: master
# base commit: d886fa118c930d0e551f2a0ed02b35d08617f746
# 

SUMMARY = "Generic library support script"
DESCRIPTION = "This is GNU libtool, a generic library support script. \
Libtool hides the complexity of generating special library types \
(such as shared libraries) behind a consistent interface."
HOMEPAGE = "http://www.gnu.org/software/libtool/libtool.html"

inherit debian-package
require recipes-debian/sources/libtool.inc

LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = "\
file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
file://libltdl/COPYING.LIB;md5=4fbd65380cdd255951079008b364516c \
"

FILESPATH_append = ":${COREBASE}/meta/recipes-devtools/libtool/libtool"
SRC_URI += "\
file://trailingslash.patch \
file://rename-with-sysroot.patch \
file://use-sysroot-in-libpath.patch \
file://fix-final-rpath.patch \
file://fix-rpath.patch \
file://norm-rpath.patch \
file://dont-depend-on-help2man.patch \
file://fix-resolve-lt-sysroot.patch \
file://nohardcodepaths.patch \
"

inherit autotools texinfo

EXTRA_AUTORECONF = "--exclude=libtoolize"

DEPENDS = "libtool-native"

do_compile_prepend () {                                                         
        # Sometimes this file doesn't get rebuilt, force the issue
        rm -f ${S}/build-aux/ltmain.sh
        make build-aux/ltmain.sh
        ./config.status
} 

PACKAGES =+ "libltdl"
FILES_libltdl = "${libdir}/libltdl${SOLIBS}"
FILES_${PN} += "${datadir}/aclocal"
FILES_${PN}-dev_remove = "${datadir}/aclocal"

export CONFIG_SHELL="/bin/bash"
