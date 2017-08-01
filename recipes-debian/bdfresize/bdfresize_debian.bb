SUMMARY = "tool for resizing BDF format font"
DESCRIPTION = "Bdfresize is a command to magnify or reduce fonts which are described with \
the standard BDF format."
HOMEPAGE = "http://openlab.jp/efont/"

inherit debian-package
PV = "1.5"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

inherit autotools

BBCLASSEXTEND = "native nativesdk"
