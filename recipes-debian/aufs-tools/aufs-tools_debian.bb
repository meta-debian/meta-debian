#
# base recipe: http://cgit.openembedded.org/cgit.cgi/meta-openembedded/tree/\
#              meta-filesystems/recipes-utils/aufs-util/aufs-util_git.bb
# base branch: jethro
#

SUMMARY = "Tools to manage aufs filesystems"
DESCRIPTION = "The aufs driver provides a unification file system for the Linux kernel.\n\
 It allows one to virtually merge the contents of several directories and/or stack\n\
 them, so that apparent file changes in the aufs end in file changes in only one\n\
 of the source directories.\n\
 .\n\
 This package contains utilities needed to configure aufs containers on-the-fly."
HOMEPAGE = "http://aufs.sourceforge.net/"

PR = "r0"
inherit debian-package
PV = "3.2+20130722"

LICENSE = "GPL-2.0+"
LIC_FILES_CHKSUM = "file://COPYING;md5=892f569a555ba9c07a568a7c0c4fa63a"

# aufs_type.h:
#	aufs-tools build depends on aufs_type.h from linux-libc-dev,
#	which is not exist on kernel version 4.x
# aufs-tools-don-t-strip-executables_debian.patch:
#	avoid QA Issue: File '/usr/lib/libau.so.2.7' \
#	from aufs-tools was already stripped, this will prevent future debugging! [already-stripped]
SRC_URI += "file://aufs_type.h \
            file://aufs-tools-don-t-strip-executables_debian.patch"

inherit autotools-brokensep
do_configure_append () {
	install -d ${S}/include/linux/
	cp ${WORKDIR}/aufs_type.h ${S}/include/linux/
}

do_compile () {
	oe_runmake CPPFLAGS="-I${S}/include -I${S}/libau"
}
