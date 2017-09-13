#
# base recipe: meta/recipes-devtools/dpkg/dpkg_1.17.4.bb
# base branch: daisy
#

INC_PR = "r1"

LICENSE = "GPLv2+ & BSD-2-Clause"
LIC_FILES_CHKSUM = " \
	file://COPYING;md5=751419260aa954499f7abaabaa882bbe \
	file://scripts/Dpkg/Gettext.pm;endline=22;md5=e497bef519c4517b0dda7d6aaedb3763 \
"

inherit debian-package
PV = "1.17.27"

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
	file://glibc2.5-sync_file_range.patch \
"

