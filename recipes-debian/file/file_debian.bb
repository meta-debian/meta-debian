#
# base recipe: meta/recipes-devtools/file/file_5.16.bb
# base branch: daisy
#

PR = "r0"

SUMMARY = "File classification tool"
DESCRIPTION = "File attempts to classify files depending \
on their contents and prints a description if a match is found."

inherit debian-package

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=975f248ba2aad6c08f97d927adf001c4"

DEPENDS = "zlib file-native"
DEPENDS_class-native = "zlib-native"

inherit autotools

FILES_${PN} += "${datadir}/misc/*.mgc"

do_install_append_class-native() {
	create_cmdline_wrapper ${D}/${bindir}/file \
		--magic-file ${datadir}/misc/magic.mgc
}

do_install_append_class-nativesdk() {
	create_cmdline_wrapper ${D}/${bindir}/file \
		--magic-file ${datadir}/misc/magic.mgc
}

BBCLASSEXTEND = "native nativesdk"
