#
# base recipe: meta/recipes-core/glib-2.0/glib-2.0_2.38.2.bb
# base branch: daisy
#

inherit debian-package
PV = "2.42.1"

PR = "r1"
DPN = "glib2.0"

LICENSE = "LGPLv2 & PD"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=3bf50002aefd002f49e7bb854063f7e7\
	file://docs/reference/COPYING;md5=f51a5100c17af6bae00735cd791e1fcc\
"

DEPENDS = "glib-2.0-native virtual/libiconv libffi zlib"
DEPENDS_append_class-target = "${@bb.utils.contains('DISTRO_FEATURES', 'ptest', ' dbus', '', d)} libpcre"
DEPENDS_class-native = "pkgconfig-native gettext-native libffi-native zlib-native"
DEPENDS_class-nativesdk = "nativesdk-libtool nativesdk-libffi nativesdk-zlib ${BPN}-native nativesdk-libpcre"

#
# Patch files:
# 0001-gio-Fix-Werror-format-string-errors-from-mismatched-.patch,
# ptest-dbus.patch, gtest-skip-fixes.patch, gio-test-race.patch 
# in reused recipe was not included in SRC_URI since it was 
# applied already in latest supported version of glib-2.0.
#
SRC_URI += "\
 file://configure-libtool.patch \
 file://fix-conflicting-rand.patch \
 file://add-march-i486-into-CFLAGS-automatically.patch \
 file://glib-2.0-configure-readlink.patch \
 file://run-ptest \
 file://ptest-paths.patch \
 file://uclibc.patch \
"
SRC_URI_append_class-native = " file://glib-gettextize-dir.patch"

PACKAGES =+ "${PN}-utils ${PN}-bash-completion ${PN}-codegen"

LEAD_SONAME = "libglib2.0.*"
FILES_${PN}-utils = "${bindir}/* ${datadir}/glib-2.0/gettext"

inherit autotools gettext pkgconfig ptest

CORECONF = "--disable-dtrace --disable-fam --disable-libelf --disable-systemtap --disable-man"

PTEST_CONF = "${@bb.utils.contains('PTEST_ENABLED', '1', '--enable-installed-tests', '--disable-installed-tests', d)}"
# --disable-selinux: Don't use selinux support
EXTRA_OECONF = "\
	--enable-included-printf=no --with-pcre=system ${CORECONF} ${PTEST_CONF} \
	--disable-selinux"
EXTRA_OECONF_class-native = "${CORECONF} --disable-selinux"
EXTRA_OECONF_append_libc-uclibc = " --with-libiconv=gnu"

do_configure_prepend() {
	sed -i -e '1s,#!.*,#!${USRBINPATH}/env python,' ${S}/gio/gdbus-2.0/codegen/gdbus-codegen.in
}

FILES_${PN} = "${libdir}/lib*${SOLIBS} ${libdir}/gio ${datadir}/glib-2.0/schemas \
               ${datadir}/glib-2.0/gettext/mkinstalldirs ${datadir}/glib-2.0/gettext/po/Makefile.in.in"
FILES_${PN}-dev += "${libdir}/glib-2.0/include \
                    ${libdir}/gio/modules/lib*${SOLIBSDEV} \
                    ${libdir}/gio/modules/*.la"
FILES_${PN}-dbg += "${datadir}/glib-2.0/gdb ${datadir}/gdb \
                    ${libdir}/gio/modules/.debug \
                    ${libdir}/glib-2.0/installed-tests/glib/.debug"
FILES_${PN}-codegen = "${datadir}/glib-2.0/codegen/*.py"
FILES_${PN}-bash-completion = "${sysconfdir}/bash_completion.d \
                               ${datadir}/bash-completion"
FILES_${PN}-ptest += "${libdir}/glib-2.0/installed-tests \
                      ${datadir}/installed-tests/glib"

ARM_INSTRUCTION_SET = "arm"
USE_NLS = "yes"

do_install_append () {
	sed ${D}${bindir}/gtester-report -i -e '1s|^#!.*|#!/usr/bin/env python|'

	# Remove some unpackaged files
	rm -f ${D}${datadir}/glib-2.0/codegen/*.pyc
	rm -f ${D}${datadir}/glib-2.0/codegen/*.pyo

	# Some distros have both /bin/perl and /usr/bin/perl, but we set perl location
	# for target as /usr/bin/perl, so fix it to /usr/bin/perl.
	if [ -f ${D}${bindir}/glib-mkenums ]; then
		sed -i -e '1s,#!.*perl,#! ${USRBINPATH}/env perl,' ${D}${bindir}/glib-mkenums
	fi
}

RDEPENDS_${PN}-ptest += "\
            tzdata \
            tzdata-americas \
            tzdata-asia \
            tzdata-europe \
            tzdata-posix \
            python-dbus \
           "
#RDEPENDS_${PN}-ptest += "\
#            gnome-desktop-testing \
#            python-pygobject \
#            shared-mime-info \
#           "

RDEPENDS_${PN}-ptest_append_libc-glibc = "\
            glibc-gconv-utf-16 \
            glibc-charmap-utf-8 \
            glibc-gconv-cp1255 \
            glibc-charmap-cp1255 \
            glibc-gconv-utf-32 \
            glibc-gconv-utf-7 \
            glibc-gconv-euc-jp \
            glibc-gconv-iso8859-1 \
            glibc-gconv-iso8859-15 \
            glibc-charmap-invariant \
            glibc-localedata-translit-cjk-variants \
           "
BBCLASSEXTEND = "native nativesdk"
