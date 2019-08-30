#
# Base recipe: meta/recipes-extended/cpio/cpio_2.12.bb
# Branch: warrior
#
SUMMARY = "GNU cpio -- a program to manage archives of files"
DESCRIPTION = "GNU cpio is a tool for creating and extracting archives, or copying \
files from one place to another.  It handles a number of cpio formats \
as well as reading and writing tar files. \
"
inherit debian-package
require recipes-debian/sources/cpio.inc

FILESPATH_append = ":${COREBASE}/meta/recipes-extended/cpio/cpio-2.12"

LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=f27defe1e96c2e1ecd4e0c9be8967949"

# Remove patch 0001-Fix-CVE-2015-1197.patch and 0001-CVE-2016-2037-1-byte-out-of-bounds-write.patch
# because these patches applied by debian patches.
# Remove 0001-Fix-segfault-with-append.patch
# because it depends on 0001-CVE-2016-2037-1-byte-out-of-bounds-write.patch.
SRC_URI += "file://0001-Unset-need_charset_alias-when-building-for-musl.patch \
           "
inherit autotools-brokensep gettext texinfo

EXTRA_OECONF += "DEFAULT_RMT_DIR=${sbindir}"

do_configure_prepend() {
	# Remove cpio.texi in Makefile.am
	# because of removing non DFSG-compliant doc/cpio.info and doc/cpio.texi from source
	sed -i -e "s|info_TEXINFOS = cpio.texi||" ${S}/doc/Makefile.am
}

do_install () {
	autotools_do_install
	if [ "${base_bindir}" != "${bindir}" ]; then
	install -d ${D}${base_bindir}/
	mv "${D}${bindir}/cpio" "${D}${base_bindir}/cpio"
	rmdir ${D}${bindir}/
	fi

	# Avoid conflicts with the version from tar
	mv "${D}${mandir}/man8/rmt.8" "${D}${mandir}/man8/rmt-cpio.8"
}

PACKAGES =+ "${PN}-rmt"

FILES_${PN}-rmt = "${sbindir}/rmt*"

inherit update-alternatives

ALTERNATIVE_PRIORITY = "100"

ALTERNATIVE_${PN} = "cpio"
ALTERNATIVE_${PN}-rmt = "rmt"

ALTERNATIVE_LINK_NAME[cpio] = "${base_bindir}/cpio"

ALTERNATIVE_PRIORITY[rmt] = "50"
ALTERNATIVE_LINK_NAME[rmt]="${sbindir}/rmt"

BBCLASSEXTEND = "native"
