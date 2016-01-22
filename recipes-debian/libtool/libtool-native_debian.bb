#
# Base recipe: meta/recipes-devtools/libtool/libtool-native_2.4.2.bb
# Base-branch: daisy
#

require libtool.inc

PR = "${INC_PR}.1"

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
