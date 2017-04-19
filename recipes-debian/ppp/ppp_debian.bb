#
# Base recipe: ecipes-connectivity/ppp/ppp_2.4.7.bb
# Base branch: jethro
#

SUMMARY = "Point-to-Point Protocol (PPP) support"
DESCRIPTION = "ppp (Paul's PPP Package) is an open source package which implements \
the Point-to-Point Protocol (PPP) on Linux and Solaris systems."
HOMEPAGE = "http://samba.org/ppp/"
BUGTRACKER = "http://ppp.samba.org/cgi-bin/ppp-bugs"

PR = "r0"

inherit debian-package
PV = "2.4.6"

DEPENDS = "libpcap"
# Set depend on pam if pam feature is enabled in DISTOR_FEATURES.
DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'libpam', '', d)}"

LICENSE = "BSD & GPLv2+ & LGPLv2+ & PD"
LIC_FILES_CHKSUM = "file://pppd/ccp.c;beginline=1;endline=29;md5=e2c43fe6e81ff77d87dc9c290a424dea \
                    file://pppd/plugins/passprompt.c;beginline=1;endline=10;md5=3bcbcdbf0e369c9a3e0b8c8275b065d8 \
                    file://pppd/tdb.c;beginline=1;endline=27;md5=4ca3a9991b011038d085d6675ae7c4e6 \
                    file://chat/chat.c;beginline=1;endline=15;md5=0d374b8545ee5c62d7aff1acbd38add2"


inherit autotools-brokensep systemd

TARGET_CC_ARCH += " ${LDFLAGS}"
EXTRA_OEMAKE = "STRIPPROG=${STRIP} MANDIR=${D}${datadir}/man/man8 INCDIR=${D}${includedir} BINDIR=${D}${sbindir}"

# By default, USE_PAM value always = y, so need to set this value
# automatically changed based on DISTRO_FEATURES.
EXTRA_OEMAKE += "${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'USE_PAM=y', 'USE_PAM=', d)}"

EXTRA_OECONF = "--disable-strip"

# Package Makefile computes CFLAGS, referencing COPTS.
# Typically hard-coded to '-O2 -g' in the Makefile's.
#
EXTRA_OEMAKE += ' COPTS="${CFLAGS} -I${S}/include"'

do_configure () {
	oe_runconf
}

do_install () {
	VERSION=$(awk -F '"' '/VERSION/ { print $$2; }' ${S}/pppd/patchlevel.h)
	oe_runmake install LIBDIR="${D}${libdir}/pppd/${VERSION}"
}

# Install files follow debian/rules
do_install_append () {
	mkdir -p ${D}${sysconfdir}/${BPN}
	mkdir -p ${D}${bindir}/ ${D}${sysconfdir}/init.d
	mkdir -p ${D}${sysconfdir}/${BPN}/ip-up.d/
	mkdir -p ${D}${sysconfdir}/${BPN}/ip-down.d/
	mkdir -p ${D}${sysconfdir}/chatscripts
	mkdir -p ${D}${datadir}/${BPN}
	mkdir -p ${D}${sysconfdir}/bash_completion.d

	install -m 0755 ${S}/debian/extra/pon ${S}/debian/extra/plog \
			${S}/debian/extra/poff ${D}${bindir}
	cp ${S}/debian/extra/pon.completion ${D}${sysconfdir}/bash_completion.d/pon

	cp ${S}/debian/extra/options ${D}${sysconfdir}/${BPN}
	install -m 0755 ${S}/debian/extra/ip-up ${S}/debian/extra/ip-down \
			${S}/debian/extra/ipv6-up ${S}/debian/extra/ipv6-down \
			${D}${sysconfdir}/${BPN}
	install -m 0755 ${S}/debian/extra/0000usepeerdns-up \
			${D}${sysconfdir}/${BPN}/ip-up.d/0000usepeerdns
	install -m 0755 ${S}/debian/extra/0000usepeerdns-down \
			${D}${sysconfdir}/${BPN}/ip-down.d/0000usepeerdns
	install -m 0644 ${S}/debian/extra/pap-secrets ${S}/debian/extra/chap-secrets \
			${S}/debian/extra/provider.peer ${S}/debian/extra/provider.chatscript \
			${D}${datadir}/${BPN}/
	install -m 0644 ${S}/debian/extra/chatscript.pap ${D}${sysconfdir}/chatscripts/pap
	install -m 0644 ${S}/debian/extra/chatscript.gprs ${D}${sysconfdir}/chatscripts/gprs

	# Install init script
	mkdir -p ${D}${sysconfdir}/init.d
	install -m 0755 ${S}/debian/ppp.pppd-dns.init ${D}${sysconfdir}/init.d/pppd-dns

	# Install logrotate config files
	mkdir -p ${D}${sysconfdir}/logrotate.d
	install -m 0644 ${S}/debian/ppp.logrotate ${D}${sysconfdir}/logrotate.d/ppp

	# Install pam support files
	if [ "${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'pam', '', d)}" = "pam" ]; then
		mkdir -p ${D}${sysconfdir}/pam.d
		install -m 0644 ${S}/debian/ppp.pam ${D}${sysconfdir}/pam.d/ppp
	fi

	# Install systemd service
	mkdir -p ${D}${systemd_unitdir}/system
	install -m 0644 ${S}/debian/ppp.pppd-dns.service ${D}${systemd_unitdir}/system/pppd-dns.service
}

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "pppd-dns.service"
SYSTEMD_AUTO_ENABLE = "enable"

CONFFILES_${PN} = "${sysconfdir}/ppp/pap-secrets ${sysconfdir}/ppp/chap-secrets ${sysconfdir}/ppp/options"
FILES_${PN} += "${datadir}/${BPN} ${libdir}/* ${systemd_unitdir}"
FILES_${PN}-dbg += "${libdir}/pppd/*/.debug"
