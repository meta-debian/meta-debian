PR = "r0"

inherit debian-package
PV = "2.13"
DPN = "nkf"

LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://../nkf.c;endline=22;md5=a30d8d09c708efb8fa61f6bedb1d6677"

S = "${WORKDIR}/git/NKF.mod"

inherit cpan
