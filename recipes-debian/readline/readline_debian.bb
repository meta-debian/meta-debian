#
# Base recipe: meta/recipes-core/readline/readline_6.3.bb
# Base branch: daisy
#

SUMMARY = "Library for editing typed command lines"
DESCRIPTION = "The GNU Readline library provides a set of functions for use by \
applications that allow users to edit command lines as they are typed in. \
Both Emacs and vi editing modes are available. The Readline library includes \
additional functions to maintain a list of previously-entered command lines, \
to recall and perhaps reedit those lines, and perform csh-like history expansion \
on previous commands."

PR = "r2"
DPN = "readline6"

inherit debian-package
PV = "6.3"

DEPENDS += "ncurses"

SRC_URI += " \
    file://acinclude.m4 \
    file://configure-fix.patch \
"

# GPLv2+ (< 6.0), GPLv3+ (>= 6.0)
LICENSE = "GPLv3+"        
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

do_configure_prepend () {
	install -m 0644 ${WORKDIR}/acinclude.m4 ${S}/
}

do_install_append () {                                                          
        # Make install doesn't properly install these                           
        oe_libinstall -so -C shlib libhistory ${D}${libdir}                     
        oe_libinstall -so -C shlib libreadline ${D}${libdir}
	
	if [ ! "$(ls -A ${D}${bindir})" ]; then
		rm -rf ${D}${bindir}             
	fi 
}	
inherit autotools

# In Debian, binary package name of readline is "lib${PN}6"
PKG_${PN} = "lib${PN}6"
PKG_${PN}-dbg = "lib${PN}6-dbg"
PKG_${PN}-dev = "lib${PN}6-dev"
PKG_${PN}-doc = "lib${PN}6-doc"
PKG_${PN}-staticdev = "lib${PN}6-staticdev"

BBCLASSEXTEND = "native nativesdk"
