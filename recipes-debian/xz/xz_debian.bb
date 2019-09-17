# base recipe: meta/recipes-extended/xz/xz_5.2.4.bb
# base branch: warrior

SUMMARY = "Utilities for managing LZMA compressed files"
HOMEPAGE = "http://tukaani.org/xz/"

# - liblzma is in the public domain.
# - xz, xzdec, and lzmadec command line tools are in the public
#   domain unless GNU getopt_long had to be compiled and linked
#   in from the lib directory. The getopt_long code is under
#   GNU LGPLv2.1+.
# - The scripts to grep, diff, and view compressed files have been
#   adapted from gzip. These scripts and their documentation are
#   under GNU GPLv2+.
# - All the documentation in the doc directory and most of the
#   XZ Utils specific documentation files in other directories
#   are in the public domain.
# - Translated messages are in the public domain.
# - The build system contains public domain files, and files that
#   are under GNU GPLv2+ or GNU GPLv3+. None of these files end up
#   in the binaries being built.
# - Test files and test code in the tests directory, and debugging
#   utilities in the debug directory are in the public domain.
# - The extra directory may contain public domain files, and files
#   that are under various free software licenses.
#
# The source includes bits of PD, GPLv2, GPLv3, LGPLv2.1+, but the only file
# which is GPLv3 is an m4 macro which isn't shipped in any of our packages,
# and the LGPL bits are under lib/, which appears to be used for libgnu, which
# appears to be used for DOS builds. So we're left with GPLv2+ and PD.
LICENSE = "GPLv2+ & GPL-3.0-with-autoconf-exception & LGPLv2.1+ & PD"
LICENSE_${PN} = "GPLv2+"
LICENSE_${PN}-dev = "GPLv2+"
LICENSE_${PN}-staticdev = "GPLv2+"
LICENSE_${PN}-doc = "GPLv2+"
LICENSE_${PN}-dbg = "GPLv2+"
LICENSE_${PN}-locale = "GPLv2+"
LICENSE_liblzma = "PD"

LIC_FILES_CHKSUM = " \
    file://COPYING;md5=97d554a32881fee0aa283d96e47cb24a \
    file://COPYING.GPLv2;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
    file://COPYING.GPLv3;md5=d32239bcb673463ab874e80d47fae504 \
    file://COPYING.LGPLv2.1;md5=4fbd65380cdd255951079008b364516c \
    file://lib/getopt.c;endline=23;md5=2069b0ee710572c03bb3114e4532cd84 \
"

inherit debian-package
require recipes-debian/sources/xz-utils.inc

DEBIAN_QUILT_PATCHES = ""

inherit autotools gettext

PACKAGES =+ "liblzma"

FILES_liblzma = "${libdir}/liblzma*${SOLIBS}"

inherit update-alternatives
ALTERNATIVE_PRIORITY = "100"
ALTERNATIVE_${PN} = "xz xzcat unxz \
                     lzma lzcat unlzma"

BBCLASSEXTEND = "native nativesdk"

export CONFIG_SHELL="/bin/sh"
