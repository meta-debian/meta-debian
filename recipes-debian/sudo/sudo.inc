#
# base recipe: ./meta/recipes-extended/sudo/sudo_1.8.14p3.bb
# base branch: master
# base commit: a27da70b3a58e5232b683f055d425cde99499f7e
#

DESCRIPTION = " Sudo (superuser do) allows a system administrator to give \
		certain users (or groups of users) the ability to run some \
		commands as root while logging all commands and arguments."
HOMEPAGE = "http://www.sudo.ws"

LICENSE = "ISC & BSD & Zlib"
LIC_FILES_CHKSUM = "\
	file://doc/LICENSE;md5=69e337d679950e304953813158595256 \
	file://plugins/sudoers/redblack.c;beginline=1;endline=41;md5=e2dbb155fc49beea947515300bab99e0 \
	file://include/queue.h;beginline=5;endline=27;md5=449af4cc57fc7d46f42090608ba3e681 \
"
INC_PR = "r1"

inherit debian-package
PV = "1.8.10p3"
DPN = "sudo"

DEPENDS += "libpam audit"
inherit autotools

# Explicitly create ${localstatedir}/lib before do_install to ensure
# the directory is accessible by all users. Otherwise the mkinstalldirs
# script (from sudo) will recursively create ${localstatedir}/lib/sudo
# and then chmod each directory with 0700 permissions, which isn't what
# we want (i.e, users would not be able to access /var/lib).
do_install_prepend (){
        mkdir -p ${D}/${localstatedir}/lib
}
#follow debian/control
RDEPENDS_${PN} += "libpam-modules"

# Avoid a parallel build problem
PARALLEL_MAKE = ""
