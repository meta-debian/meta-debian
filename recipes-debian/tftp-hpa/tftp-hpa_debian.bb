#
# base recipe: http://cgit.openembedded.org/cgit.cgi/meta-openembedded/tree/\
#		meta-networking/recipes-daemons/tftp-hpa/tftp-hpa_5.2.bb?
# base branch: master
# base commit: 98842e4e9f53f5f8614dd583dde9f5e898aadbb2
#

SUMMARY        = "Client for the Trivial File Transfer Protocol"
DESCRIPTION    = \
"The Trivial File Transfer Protocol (TFTP) is normally used only for 	\
booting diskless workstations.  The tftp package provides the user   	\
interface for TFTP, which allows users to transfer files to and from a 	\
remote machine.  This program and TFTP provide very little security, 	\
and should not be enabled unless it is expressly needed."
HOMEPAGE = "http://git.kernel.org/cgit/network/tftp/tftp-hpa.git"

PR = "r0"
inherit debian-package
PV = "5.2+20140608"

LICENSE = "BSD-4-Clause"
LIC_FILES_CHKSUM = "\
	file://MCONFIG.in;beginline=1;endline=9;md5=c28ba5adb43041fae4629db05c83cbdd \
	file://tftp/tftp.c;beginline=1;endline=32;md5=988c1cba99d70858a26cd877209857f4"

DEBIAN_PATCH_TYPE = "nopatch"

#Add tftpd-hpa package
PACKAGES =+ "tftpd-hpa"

inherit autotools-brokensep pkgconfig

do_configure() {
	${S}/autogen.sh
	oe_runconf
}
do_install() {
	oe_runmake install INSTALLROOT=${D}
	#Create new folders
	install -d ${D}${sysconfdir}/init.d
	install -d ${D}${sysconfdir}/init

	install -m 0644 ${S}/debian/tftpd-hpa.upstart 		\
			${D}${sysconfdir}/init/tftpd-hpa.conf

	install -m 0755 ${S}/debian/tftpd-hpa.init 		\
			${D}${sysconfdir}/init.d/tftpd-hpa
}

FILES_tftpd-hpa = "${sbindir}/in.tftpd 				\
		   ${sysconfdir}/* 				\
"
