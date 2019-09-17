# base recipe: meta/recipes-devtools/libtool/libtool-native_2.4.6.bb
# base branch: warrior

require libtool.inc

DEPENDS = ""

SRC_URI += "file://prefix.patch"

inherit native

EXTRA_OECONF = " --with-libtool-sysroot=${STAGING_DIR_NATIVE}"

# Don't apply debian/patches/link_all_deplibs.patch                             
# This patch causes an error while linking objects to create                    
# gettext's libraries although library dependency flag was already added.       
# As author's comment, shared library on ELF system should already known which  
# libs it need to link, but the case seem is not true for gettext.                                    
do_debian_patch_prepend() {                                                     
	sed -i -e "/link_all_deplibs/ d" ${S}/debian/patches/series    
}

do_configure_prepend() {
	# Remove any existing libtool m4 since old stale versions would break
	# any upgrade
	rm -f ${STAGING_DATADIR}/aclocal/libtool.m4
	rm -f ${STAGING_DATADIR}/aclocal/lt*.m4
}

do_install() {
	autotools_do_install
	install -d ${D}${bindir}/
	install -m 0755 ${HOST_SYS}-libtool ${D}${bindir}/${HOST_SYS}-libtool
}
