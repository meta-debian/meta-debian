require ${COREBASE}/meta/recipes-devtools/dpkg/dpkg.inc

inherit debian-package
require recipes-debian/sources/dpkg.inc
FILESPATH_append = ":${COREBASE}/meta/recipes-devtools/dpkg/dpkg"

LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI += " \
           file://noman.patch \
           file://remove-tar-no-timestamp.patch \
           file://arch_pm.patch \
           file://add_armeb_triplet_entry.patch \
           file://0002-Adapt-to-linux-wrs-kernel-version-which-has-characte.patch \
           file://0002-Our-pre-postinsts-expect-D-to-be-set-when-running-in.patch \
           file://0004-The-lutimes-function-doesn-t-work-properly-for-all-s.patch \
           file://0004-dpkg-compiler.m4-remove-Wvla.patch \
           file://0006-add-musleabi-to-known-target-tripets.patch \
           file://0006-dpkg-deb-build.c-Remove-usage-of-clamp-mtime-in-tar.patch \
           file://0001-dpkg-Support-muslx32-build.patch \
           "

SRC_URI_append_class-native = " file://glibc2.5-sync_file_range.patch "

FILES_${PN} += "${datadir}/polkit-1"
