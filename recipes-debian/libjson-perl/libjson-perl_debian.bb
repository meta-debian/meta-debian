SUMMARY = "module for manipulating JSON-formatted data"
PR = "r0"

inherit debian-package

LICENSE = "GPL-1+"
LIC_FILES_CHKSUM = "file://README;md5=309aa8b91686700797df6e79ceb678f0"

inherit cpan

FILES_${PN} += "${datadir}"

BBCLASSEXTEND = "native nativesdk"
