require recipes-devtools/libtool/${PN}_2.4.2.bb
FILESEXTRAPATHS_prepend = "${COREBASE}/meta/recipes-devtools/libtool/libtool:"

inherit debian-package
DEBIAN_SECTION = "devel"

DPR = "0"

LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI += " \
file://trailingslash.patch \
file://rename-with-sysroot.patch \
file://use-sysroot-in-libpath.patch \
file://fix-final-rpath.patch \
file://avoid_absolute_paths_for_general_utils.patch \
file://fix-rpath.patch \
file://respect-fstack-protector.patch \
file://norm-rpath.patch \
file://dont-depend-on-help2man-edited.patch \
file://fix-resolve-lt-sysroot.patch \
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
