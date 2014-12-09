require recipes-support/attr/${BPN}_2.2.52.bb
FILESEXTRAPATHS_prepend = "\
${COREBASE}/meta/recipes-support/attr/${BPN}:\
${COREBASE}/meta/recipes-support/attr/files:\
"

inherit debian-package
DEBIAN_SECTION = "utils"

DPR = "0"

LICENSE = "LGPLv2.1+ & GPLv2+"
LICENSE_${PN} = "GPLv2+"
LICENSE_lib${BPN} = "LGPLv2.1+"
LIC_FILES_CHKSUM = " \
file://doc/COPYING;md5=c781d70ed2b4d48995b790403217a249 \
file://doc/COPYING.LGPL;md5=9e9a206917f8af112da634ce3ab41764 \
"

SRC_URI += " \
file://run-ptest \
file://acl-fix-the-order-of-expected-output-of-getfacl.patch \
file://relative-libdir.patch;striplevel=0 \
file://add-missing-configure.ac.patch \
"
