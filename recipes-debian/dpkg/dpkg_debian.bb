require recipes-devtools/dpkg/${PN}_1.17.4.bb
FILESEXTRAPATHS_prepend = "${COREBASE}/meta/recipes-devtools/dpkg/dpkg:"

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

# We want to use dpkg source code to build virtual/update-alternatives 
# instead of opkg-utils - which is not supported by Debian. But dpkg depends
# on 'ncurses' and 'bzip2', which inherit update-alternatives.bbclass, then
# causes loop dependencies. So we assign another provider for 
# virtual/update-alternatives, please refer to update-alternatives-dpkg_debian.bb

DEPENDS_remove_class-native = "virtual/update-alternatives-native"

do_install_append_class-target () {
	rm ${D}${sbindir}/update-alternatives
}

PACKAGES_remove = "update-alternatives-dpkg"
