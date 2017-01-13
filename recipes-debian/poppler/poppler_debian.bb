SUMMARY = "PDF rendering library"

DESCRIPTION = "PDF rendering library based on Xpdf PDF viewer"

DEPENDS = "fontconfig"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

PR = "r0"

inherit debian-package autotools pkgconfig gettext
PV = "0.26.5"
