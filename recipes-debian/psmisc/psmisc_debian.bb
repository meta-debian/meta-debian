#
# base recipe: /meta/recipes-extended/psmisc/psmisc_22.21.bb
# base branch: master
# base commit: e335d9519c883165e70cda5c1fde348391686e31
#

SUMMARY = "Utilities for managing processes on your system"
DESCRIPTION = "The psmisc package contains utilities for managing processes \
on your system: pstree, killall and fuser.  The pstree command displays a \
tree structure of all of the running processes on your system.  The killall \
command sends a specified signal (SIGTERM if nothing is specified) to \
processes identified by name.  The fuser command identifies the PIDs \
of processes that are using specified files or filesystems."
HOMEPAGE = "http://psmisc.sf.net/"

PR = "r0"
inherit debian-package
PV = "22.21"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3"

DEPENDS =+ "ncurses"

inherit autotools gettext update-alternatives

#Move /usr/bin/fuser file to /bin/ folder
do_install_append () {
	install -d ${D}${base_bindir}
	mv ${D}${bindir}/fuser ${D}${base_bindir}/fuser
}
FILES_${PN} += "${base_bindir}/fuser"

# Add update-alternatives definitions
ALTERNATIVE_PRIORITY="100"
ALTERNATIVE_${PN} = "fuser"
ALTERNATIVE_LINK_NAME[fuser] = "${base_bindir}/fuser"
