#
# base recipe: meta/recipes-extended/xz/xz_5.2.3.bb
# base branch: master
# base commit: d886fa118c930d0e551f2a0ed02b35d08617f746
#

SUMMARY = "Utilities for managing LZMA compressed files"
HOMEPAGE = "http://tukaani.org/xz/"

inherit debian-package autotools gettext
PV = "5.2.2"
DPN = "xz-utils"
PROVIDES = "xz"

DEPENDS += "gettext-native"
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
LICENSE_liblzma = "PD"

LIC_FILES_CHKSUM = "file://COPYING;md5=c475b6c7dca236740ace4bba553e8e1c \
                    file://COPYING.GPLv2;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING.GPLv3;md5=d32239bcb673463ab874e80d47fae504 \
                    file://COPYING.LGPLv2.1;md5=4fbd65380cdd255951079008b364516c \
                    file://lib/getopt.c;endline=23;md5=2069b0ee710572c03bb3114e4532cd84 "

BBCLASSEXTEND = "native nativesdk"

# Set license GPLv2+ for {PN}-locale* packages
python package_do_split_locales_append() {
    for l in sorted(locales):
        ln = legitimize_package_name(l)
        pkg = pn + '-locale-' + ln
        packages.append(pkg)
        d.setVar('LICENSE_' + pkg, "GPLv2+")
}

inherit autotools gettext

PACKAGES =+ "liblzma"

FILES_liblzma = "${base_libdir}/liblzma*${SOLIBS}"

inherit update-alternatives
ALTERNATIVE_PRIORITY = "100"
ALTERNATIVE_${PN} = "xz xzcat unxz \
                     lzma lzcat unlzma"
