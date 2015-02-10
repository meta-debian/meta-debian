require recipes-core/gettext/gettext-minimal-native_0.18.3.2.bb
FILESEXTRAPATHS_prepend = "\
${COREBASE}/meta/recipes-core/gettext/gettext-minimal-0.18.3.2:\
"

BPN = "gettext"

inherit debian-package
DEBIAN_SECTION = "devel"

DPR = "0"

LICENSE = "GPL-3.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

# Exclude iconv-m4-remove-the-test-to-convert-euc-jp.patch
# because of no file to patch
SRC_URI += " \
file://aclocal.tgz \
file://config.rpath \
file://Makefile.in.in \
file://remove-potcdate.sin \
file://COPYING \
"

# Override Makefile.in.in to fix hardcode version of gettext
# which cause mismatch version error since now we use gettext >= 0.19
SRC_URI += " \
file://Makefile.in.in_debian \
"

do_install_append() {
	cp ${WORKDIR}/Makefile.in.in_debian \
				${D}${datadir}/gettext/po/Makefile.in.in
}
