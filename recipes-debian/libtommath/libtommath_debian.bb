SUMMARY = "multiple-precision integer library"

DESCRIPTION = "LibTomMath is a C language library that provides a vast array \
of highly optimized functions for number theory."

LICENSE = "PD"
LIC_FILES_CHKSUM = "file://debian/copyright;\
md5=98bc0cb525cb6dc43f4375836db2f87c"

inherit debian-package autotools
DEBIAN_SECTON = "libs"
DPR = "0"

SRC_URI += "\
        file://replace-group-wheel.patch \
"
