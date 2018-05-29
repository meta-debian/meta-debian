#
# base recipe: meta/recipes-devtools/libtool/libtool-native_2.4.6.bb
# base branch: master
# base commit: d886fa118c930d0e551f2a0ed02b35d08617f746
#

require libtool.inc

inherit native

DEPENDS = ""

EXTRA_OECONF = " --with-libtool-sysroot=${STAGING_DIR_NATIVE}"

SRC_URI += " \
file://prefix.patch \
"

# Don't apply debian/patches/link_all_deplibs.patch                             
# This patch causes an error while linking objects to create                    
# gettext's libraries although library dependency flag was already added.       
# As author's comment, shared library on ELF system should already known which  
# libs it need to link, but the case seem is not true for gettext.                                    
do_debian_patch_prepend() {                                                     
	sed -i -e "/link_all_deplibs/ d" ${S}/debian/patches/series             
}
