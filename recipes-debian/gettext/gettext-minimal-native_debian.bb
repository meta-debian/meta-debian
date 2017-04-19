#
# base recipe: meta/recipes-core/gettext/gettext-minimal-native_0.18.3.2.bb
# base branch: daisy
#

PR = "r0"

inherit debian-package
PV = "0.19.3"

LICENSE = "GPL-3.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DPN = "gettext"

INHIBIT_DEFAULT_DEPS = "1"
INHIBIT_AUTOTOOLS_DEPS = "1"

inherit native

do_install () {
        install -d ${D}${datadir}/aclocal/
        cp ${WORKDIR}/*.m4 ${D}${datadir}/aclocal/
        install -d ${D}${datadir}/gettext/po/
        cp ${WORKDIR}/config.rpath ${D}${datadir}/gettext/
        cp ${WORKDIR}/Makefile.in.in ${D}${datadir}/gettext/po/
        cp ${WORKDIR}/remove-potcdate.sin ${D}${datadir}/gettext/po/
}

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
