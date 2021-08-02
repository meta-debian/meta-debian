SUMMARY = "pythonic binding for the libxml2 and libxslt libraries"
DESCRIPTION = "lxml is a new Python binding for libxml2 and libxslt, completely\n\
 independent from existing Python bindings. Its aim:\n\
 .\n\
   * Pythonic API.\n\
   * Documented.\n\
   * Use Python unicode strings in API.\n\
   * Safe (no segfaults).\n\
   * No manual memory management!\n\
 .\n\
 lxml aims to provide a Pythonic API by following as much as possible\n\
 the ElementTree API, trying to avoid inventing too many new APIs,\n\
 or the user's having to learn new things -- XML is complicated enough."
HOMEPAGE = "http://lxml.de/"

PR = "r0"
inherit debian-package
PV = "3.4.0"

# Debian's source code isn't contains patch file
DEBIAN_PATCH_TYPE = "nopatch"
LICENSE = "BSD & GPLv2 & Zlib & Python-2.0"
LIC_FILES_CHKSUM = "\
    file://LICENSES.txt;md5=f9f1dc24f720c143c2240df41fe5073b \
    file://doc/licenses/BSD.txt;md5=700a1fc17f4797d4f2d34970c8ee694b \
    file://doc/licenses/GPL.txt;md5=94d55d512a9ba36caa9b7df079bae19f \
    file://src/lxml/isoschematron/resources/rng/iso-schematron.rng;startline=2;endline=7;md5=a2914d12acc7066b3f5b60e8f94a012e \
    file://src/lxml/isoschematron/resources/xsl/iso-schematron-xslt1/iso_schematron_message.xsl;beginline=2;endline=24;md5=cc86b7b2bbc678e13f58ea403eb9929b \
"

inherit setuptools

DEPENDS += "libxml2 libxslt zlib"

# required by distutils/sysconfig.py to get config dir
export DEB_HOST_MULTIARCH

KEEP_NONARCH_BASELIB = "1"

DISTUTILS_BUILD_ARGS += " \
                     --with-xslt-config='pkg-config libxslt' \
                     --with-xml2-config='pkg-config libxml-2.0' \
"

DISTUTILS_INSTALL_ARGS += " \
                     --with-xslt-config='pkg-config libxslt' \
                     --with-xml2-config='pkg-config libxml-2.0' \
"

do_configure_prepend() {
    sed -i -e 's/--version/--modversion/' ${B}/setupinfo.py
}
