#
# Base recipe: meta-openembedded/meta-networking/recipes-connectivity/inetutils/inetutils_1.9.4.bb
# Base branch: jethro
#

DESCRIPTION = "The GNU inetutils are a collection of common \
networking utilities and servers including ftp, ftpd, rcp, \
rexec, rlogin, rlogind, rsh, rshd, syslog, syslogd, talk, \
talkd, telnet, telnetd, tftp, tftpd, and uucpd."
HOMEPAGE = "http://www.gnu.org/software/inetutils"

PR = "r0"

inherit debian-package

#inetutils-1.9-PATH_PROCNET_DEV.patch: 
#    The patch file for define PATH_PROCNET_DEV if not already defined
#    this prevents the following compilation error :
#    system/linux.c:401:15: error: 'PATH_PROCNET_DEV' undeclared
SRC_URI += "file://inetutils-1.9-PATH_PROCNET_DEV.patch"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=0c7051aef9219dc7237f206c5c4179a7"

DEPENDS = "ncurses netbase readline"

inherit autotools gettext update-alternatives texinfo

PACKAGECONFIG ??= " ftp ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'pam', '', d)}"
PACKAGECONFIG[ftp] = "--enable-ftp,--disable-ftp,readline"
PACKAGECONFIG[uucpd] = "--enable-uucpd,--disable-uucpd,readline"
PACKAGECONFIG[pam] = "--with-pam,--without-pam,libpam"

EXTRA_OECONF = "--with-shishi --with-wrap --libexecdir=${sbindir} \
		--disable-dnsdomainname --disable-hostname --disable-logger \
		--disable-rcp --disable-rexec --disable-rlogin --disable-rsh \
		--disable-tftp --disable-whois --disable-rexecd --disable-rshd \
		--disable-rlogind --disable-tftpd"

do_configure_prepend () {
	export HELP2MAN='true'
	cp ${STAGING_DATADIR_NATIVE}/gettext/config.rpath ${S}/build-aux/config.rpath
	rm -f ${S}/glob/configure*
}

do_install_append () {
	install -m 0755 -d ${D}${base_bindir}
	install -m 0755 -d ${D}${sbindir}
	install -m 0755 -d ${D}${sysconfdir}/default
	install -m 0755 -d ${D}${sysconfdir}/inetd.d
	install -m 0755 -d ${D}${sysconfdir}/init.d
	install -m 0755 -d ${D}${sysconfdir}/logrotate.d
	install -m 0755 -d ${D}${sysconfdir}/pam.d
	install -m 0755 -d ${D}${sysconfdir}/syslog.d
	
	# Move ping to /bin
	mv ${D}${bindir}/ping ${D}${base_bindir}/
	mv ${D}${bindir}/ping6 ${D}${base_bindir}/

	# Rename ifconfig to not break existing systems using net-tools
	mv ${D}${bindir}/ifconfig ${D}${bindir}/inetutils-ifconfig
	
	# Rename inetd to be able to coexist with not purged netkit-inetd
	mv ${D}${sbindir}/inetd ${D}${sbindir}/inetutils-inetd

	# Needed to enable alternatives
	mv ${D}${bindir}/telnet ${D}${bindir}/inetutils-telnet
	mv ${D}${bindir}/ftp ${D}${bindir}/inetutils-ftp
	mv ${D}${bindir}/talk ${D}${bindir}/inetutils-talk
	mv ${D}${bindir}/traceroute ${D}${bindir}/inetutils-traceroute

	install -m 0644 ${S}/debian/inetutils-inetd.default \
			${D}${sysconfdir}/default/inetutils-inetd
	install -m 0644 ${S}/debian/inetutils-syslogd.default \
			${D}${sysconfdir}/default/inetutils-syslogd
	install -m 0755 ${S}/debian/inetutils-inetd.init \
			${D}${sysconfdir}/init.d/inetutils-inetd	
	install -m 0755 ${S}/debian/inetutils-syslogd.init \
			${D}${sysconfdir}/init.d/inetutils-syslogd	
	install -m 0644 ${S}/debian/inetutils-syslogd.logrotate \
			${D}${sysconfdir}/logrotate.d/inetutils-syslogd	
	install -m 0644 ${S}/debian/inetutils-ftpd.ftp.pam \
			${D}${sysconfdir}/pam.d/ftp

	rm -rf ${D}${libexecdir}
	rm -rf ${D}${base_sbindir}
}

PACKAGES =+ "${BPN}-ftp ${BPN}-ftpd ${BPN}-inetd ${BPN}-ping ${BPN}-syslogd ${BPN}-talk \
	     ${BPN}-talkd ${BPN}-telnet ${BPN}-telnetd ${BPN}-tools ${BPN}-traceroute"

FILES_${BPN}-ftp = "${bindir}/inetutils-ftp"
FILES_${BPN}-ftpd = "${sysconfdir}/pam.d ${sbindir}/ftpd"
FILES_${BPN}-inetd = "${sbindir}/inetutils-inetd ${sysconfdir}/init.d/inetutils-inetd \
		      ${sysconfdir}/default/inetutils-inetd ${sysconfdir}/inetd.d"
FILES_${BPN}-ping = "${base_bindir}/*"
FILES_${BPN}-syslogd = "${sysconfdir}/default/inetutils-syslogd \
			${sysconfdir}/init.d/inetutils-syslogd \
			${sysconfdir}/logrotate.d/inetutils-syslogd \
			${sysconfdir}/syslog.conf \
			${sbindir}/syslogd \
			${sysconfdir}/syslog.d"
FILES_${BPN}-talk = "${bindir}/inetutils-talk"
FILES_${BPN}-talkd = "${sbindir}/talkd"
FILES_${BPN}-telnet = "${bindir}/inetutils-telnet"
FILES_${BPN}-telnetd = "${sbindir}/telnetd"
FILES_${BPN}-tools = "${bindir}/inetutils-ifconfig"
FILES_${BPN}-traceroute = "${bindir}/inetutils-traceroute"

ALTERNATIVE_PRIORITY = "100"
ALTERNATIVE_${PN}-ftp = "ftp"
ALTERNATIVE_LINK_NAME[ftp] = "${bindir}/ftp"
ALTERNATIVE_TARGET[ftp] = "${bindir}/inetutils-ftp"

ALTERNATIVE_${PN}-traceroute = "traceroute"
ALTERNATIVE_LINK_NAME[traceroute] = "${bindir}/traceroute"
ALTERNATIVE_TARGET[traceroute] = "${bindir}/inetutils-traceroute"
