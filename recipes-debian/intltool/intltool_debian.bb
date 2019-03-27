SUMMARY = "Utility scripts for internationalizing XML"
SECTION = "devel"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

inherit debian-package
require recipes-debian/sources/intltool.inc
FILESPATH_append = ":${COREBASE}/meta/recipes-devtools/intltool/intltool"

SRC_URI += " \
           file://intltool-nowarn.patch \
           file://remove-perl-check.patch \
           file://noperlcheck.patch \
           "

# file://perl-522-deprecations.patch

UPSTREAM_CHECK_URI = "https://launchpad.net/intltool/trunk/"

DEPENDS = "libxml-parser-perl-native"
RDEPENDS_${PN} = "gettext-dev libxml-parser-perl"
DEPENDS_class-native = "libxml-parser-perl-native gettext-native"

inherit autotools pkgconfig perlnative

export PERL = "${bindir}/env perl"
PERL_class-native = "/usr/bin/env nativeperl"
PERL_class-nativesdk = "/usr/bin/env perl"

# gettext is assumed to exist on the host
RDEPENDS_${PN}_class-native = "libxml-parser-perl-native"
RRECOMMENDS_${PN} = "perl-modules"
RRECOMMENDS_${PN}_class-native = ""

FILES_${PN}-dev = ""
FILES_${PN} += "${datadir}/aclocal"

INSANE_SKIP_${PN} += "dev-deps"

BBCLASSEXTEND = "native nativesdk"
