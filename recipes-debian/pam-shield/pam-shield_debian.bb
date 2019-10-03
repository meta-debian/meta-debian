SUMMARY = "locks out remote attackers trying password guessing"
DESCRIPTION ="In certain situations it is not possible to use host based\n\
 authentication and here pam-shield comes in use.\n\
 .\n\
 It locks out brute-force password crackers using null-route or iptables rules."

HOMEPAGE = "http://github.com/jtniehof/pam_shield"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3"

inherit debian-package
require recipes-debian/sources/pam-shield.inc

inherit autotools-brokensep

DEPENDS += "gdbm libpam"

FILES_${PN} += "${base_libdir}/security/pam_shield.so \
                ${datadir}/pam-configs/pam_shield"

RSUGGESTS_${PN} += "iproute2 iptables"

# base on debian/postinst
pkg_postinst_${PN} () {
	if [ ! -d $D${localstatedir}/lib/pam_shield ]; then
		mkdir $D${localstatedir}/lib/pam_shield
	fi
	touch $D${localstatedir}/lib/pam_shield/db
}

PKG_${PN} = "lib${PN}"
RPROVIDES_${PN} = "lib${PN}"
