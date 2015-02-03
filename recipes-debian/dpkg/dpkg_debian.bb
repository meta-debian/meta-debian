require recipes-devtools/dpkg/${PN}_1.17.4.bb
FILESEXTRAPATHS_prepend = "${COREBASE}/meta/recipes-devtools/dpkg/${BPN}:"

inherit debian-package
DEBIAN_SECTION = "admin"
DPR = "0"

LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI += " \
file://noman.patch \
file://check_snprintf.patch \
file://check_version.patch \
file://preinst.patch \
file://fix-timestamps.patch \
file://remove-tar-no-timestamp.patch \
file://fix-abs-redefine.patch \
file://arch_pm.patch \
"
