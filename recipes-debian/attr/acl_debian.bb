SUMMARY = "Utilities for managing POSIX Access Control Lists"
HOMEPAGE = "http://savannah.nongnu.org/projects/acl/"
SECTION = "libs"

LICENSE = "LGPLv2.1+ & GPLv2+"
LICENSE_${PN} = "GPLv2+"
LICENSE_lib${BPN} = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://doc/COPYING;md5=c781d70ed2b4d48995b790403217a249 \
                    file://doc/COPYING.LGPL;md5=9e9a206917f8af112da634ce3ab41764"

DEPENDS = "attr"

inherit debian-package
require recipes-debian/sources/acl.inc

inherit autotools gettext

BBCLASSEXTEND = "native nativesdk"

PACKAGES =+ "lib${BPN}"
FILES_lib${BPN} = "${libdir}/lib*${SOLIBS}"
