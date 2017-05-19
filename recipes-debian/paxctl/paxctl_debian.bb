#
# base recipe: http://git.yoctoproject.org/cgit/cgit.cgi/meta-security/\
#	       tree/recipes-security/paxctl/paxctl_0.9.bb
# base branch: master
# base commit: 8eadad192719be3d2320effe6a27a83120d74a2f
#

SUMMARY = "new PaX control program for using the PT_PAX_FLAGS marking"
DESCRIPTION = "paxctl  is  a tool that allows PaX flags to be modified on a \
               per-binary basis. PaX is part of common  security-enhancing  \
               kernel  patches  and secure distributions, such as \
               GrSecurity or Adamantix and Hardened Gen-too, respectively."
HOMEPAGE = "https://pax.grsecurity.net/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://paxctl.c;beginline=1;endline=5;md5=0ddd065c61020dda79729e6bedaed2c7 \
		    file://paxctl-elf.c;beginline=1;endline=5;md5=99f453ce7f6d1687ee808982e2924813 \
		   "

PR = "r0"
inherit debian-package
PV = "0.9"

EXTRA_OEMAKE = "-e MAKEFLAGS="

do_install() {
	oe_runmake 'DESTDIR=${D}' install
}
