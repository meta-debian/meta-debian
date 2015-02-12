require recipes-devtools/dpkg/${PN}_1.17.4.bb
FILESEXTRAPATHS_prepend = "${COREBASE}/meta/recipes-devtools/dpkg/${BPN}:"

inherit debian-package
DEBIAN_SECTION = "admin"
DPR = "1"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

# Patch file no-vla-warning.patch, dpkg-1.17.4-CVE-2014-0471.patch and
# dpkg-1.17.4-CVE-2014-0471-CVE-2014-3127.patch are no need since
# it has been applied in new version of source code.
SRC_URI += " \
file://noman.patch \
file://check_snprintf.patch \
file://check_version.patch \
file://preinst.patch \
file://fix-timestamps.patch \
file://remove-tar-no-timestamp.patch \
file://fix-abs-redefine.patch \
file://arch_pm.patch \
file://dpkg-configure.service \
file://glibc2.5-sync_file_range.patch \
"
