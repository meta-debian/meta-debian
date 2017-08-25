#
# base recipe: meta/recipes-devtools/intltool
# base branch: daisy
#

SUMMARY = "Utility scripts for internationalizing XML"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

PR = "r0"
inherit debian-package
PV = "0.50.2"

# All of the intltool scripts have the correct paths to perl already
# embedded into them and can find perl fine, so we add the remove xml-check
# in the intltool.m4 via the remove-xml-check.patch
NATIVEPATCHES = "file://noperlcheck.patch \
                 file://remove-xml-check.patch"
NATIVEPATCHES_class-native = "file://use-nativeperl.patch" 

SRC_URI += "file://intltool-nowarn.patch \
           file://uclibc.patch \
           ${NATIVEPATCHES} \
           "

DEPENDS = "libxml-parser-perl-native"
RDEPENDS_${PN} = "gettext-dev libxml-parser-perl"
DEPENDS_class-native = "libxml-parser-perl-native"
# gettext is assumed to exist on the host
RDEPENDS_${PN}_class-native = "libxml-parser-perl-native"
RRECOMMENDS_${PN} = "perl-modules"
RRECOMMENDS_${PN}_class-native = ""

FILES_${PN}-dev = ""
FILES_${PN} += "${datadir}/aclocal"

INSANE_SKIP_${PN} += "dev-deps"

inherit autotools pkgconfig perlnative

export PERL = "${bindir}/env perl"
PERL_class-native = "/usr/bin/env nativeperl"

BBCLASSEXTEND = "native nativesdk"
