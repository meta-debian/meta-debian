#
# base recipe: meta/recipes-extended/findutils/findutils_4.4.2.bb
# base branch: daisy
#

PR = "r0"

inherit debian-package
PV = "4.4.2"

LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=f27defe1e96c2e1ecd4e0c9be8967949"

DEPENDS = "bison-native"

# findutils_fix_for_automake-1.12.patch:
#	Avoid "error: automatic de-ANSI-fication support has been removed"
#	by remove AM_C_PROTOTYPES in configure.ac
# findutils_fix_doc_debian.patch:
#	Fix error:
#	  "find-maint.texi:45: misplaced {
#	   find-maint.texi:45: misplaced }
#	   find-maint.texi:236: warning: node next `Make the Compiler Find the Bugs' in menu `The File System Is Being Modified' and in sectioning `Factor Out Repeated Code' differ
#	   ..."
SRC_URI += " \
	file://findutils_fix_for_automake-1.12.patch \
	file://findutils_fix_doc_debian.patch \
"

# http://savannah.gnu.org/bugs/?27299
CACHED_CONFIGUREVARS += "gl_cv_func_wcwidth_works=yes"

inherit autotools gettext update-alternatives

# diffutils assumes non-glibc compilation with uclibc and
# this causes it to generate its own implementations of
# standard functionality.  regex.c actually breaks compilation
# because it uses __mempcpy, there are other things (TBD:
# see diffutils.mk in buildroot)
EXTRA_OECONF_libc-uclibc = "--without-included-regex"

# Configure follow debian/rules
EXTRA_OECONF += " \
	--localstatedir='${localstatedir}/cache/locate' \
	--enable-d_type-optimisation \
	--libexecdir='${libdir}/locate' \
"

do_install_append(){
	# Follow debian/rules
	mv ${D}${bindir}/updatedb ${D}${bindir}/updatedb.${DPN}
	mv ${D}${bindir}/locate ${D}${bindir}/locate.${DPN}
	mv ${D}${mandir}/man1/locate.1 ${D}${mandir}/man1/locate.${DPN}.1
	mv ${D}${mandir}/man1/updatedb.1 ${D}${mandir}/man1/updatedb.${DPN}.1

	mkdir -p ${D}${sysconfdir}/cron.daily
	install -m 0755 ${S}/debian/locate.cron.daily ${D}${sysconfdir}/cron.daily/locate
}

PACKAGES =+ "locate"

FILES_locate = " \
	${sysconfdir} \
	${bindir}/*.${DPN} \
	${libdir}/locate/* \
	${localstatedir}/cache/locate \
"
FILES_${PN}-dbg += "${libdir}/locate/.debug"

RDEPENDS_locate += "findutils"

# Follow debian/locate.postinst
ALTERNATIVE_locate = "locate updatedb"
ALTERNATIVE_TARGET[locate] = "${bindir}/locate.${DPN}"
ALTERNATIVE_TARGET[updatedb] = "${bindir}/updatedb.${DPN}"
ALTERNATIVE_PRIORITY = "20"

BBCLASSEXTEND = "native nativesdk"
