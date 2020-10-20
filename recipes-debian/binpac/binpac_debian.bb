SUMMARY = "high level protocol parser language"
DESCRIPTION = "BinPAC is a high level language for describing protocol parsers and \
generates C++ code. It is currently maintained and distributed with the Bro Network \
Security Monitor distribution, however, the generated parsers may be used with other \
programs besides Bro."

HOMEPAGE = " http://www.bro.org/"

LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=5139995cafc8b5273b85dd8bab509855"

inherit debian-package
require recipes-debian/sources/binpac.inc
inherit autotools cmake 
DEBIAN_QUILT_PATCHES = ""

EXTRA_OECMAKE += "-DCMAKE_SKIP_RPATH=TRUE"
DEPENDS  += " bison-native flex-native"


BBCLASSEXTEND = "native nativesdk"
