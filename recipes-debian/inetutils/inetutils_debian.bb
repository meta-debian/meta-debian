#
# Base recipe: meta-openembedded/meta-networking/recipes-connectivity/inetutils/inetutils_1.9.4.bb
# Base branch: jethro
#

DESCRIPTION = "The GNU inetutils are a collection of common \
networking utilities and servers including ftp, ftpd, rcp, \
rexec, rlogin, rlogind, rsh, rshd, syslog, syslogd, talk, \
talkd, telnet, telnetd, tftp, tftpd, and uucpd."
HOMEPAGE = "http://www.gnu.org/software/inetutils"

PR = "r1"

inherit debian-package
PV = "1.9.2.39.3a460"

#inetutils-1.9-PATH_PROCNET_DEV.patch: 
#    The patch file for define PATH_PROCNET_DEV if not already defined
#    this prevents the following compilation error :
#    system/linux.c:401:15: error: 'PATH_PROCNET_DEV' undeclared
SRC_URI += "file://inetutils-1.9-PATH_PROCNET_DEV.patch"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=0c7051aef9219dc7237f206c5c4179a7"

DEPENDS = "ncurses netbase readline update-inetd-native"

inherit autotools gettext update-alternatives texinfo

PACKAGECONFIG ??= "ftp tcp-wrappers \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'pam', '', d)} \
                   "
PACKAGECONFIG[ftp] = "--enable-ftp,--disable-ftp,readline"
PACKAGECONFIG[uucpd] = "--enable-uucpd,--disable-uucpd,readline"
PACKAGECONFIG[pam] = "--with-pam,--without-pam,libpam"
PACKAGECONFIG[shishi] = "--with-shishi,--without-shishi,shishi"
PACKAGECONFIG[tcp-wrappers] = "--with-wrap,--without-wrap,tcp-wrappers"

EXTRA_OECONF = "inetutils_cv_path_login=${base_bindir}/login --libexecdir=${sbindir} \
		--disable-dnsdomainname --disable-hostname --disable-logger \
		--disable-rcp --disable-rexec --disable-rlogin --disable-rsh \
		--disable-tftp --disable-whois --disable-rexecd --disable-rshd \
		--disable-rlogind --disable-tftpd"

EXTRA_OEMAKE_append_task-install = " SUIDMODE="-o root -m 4755""

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
	install -m 0755 -d ${D}${localstatedir}/log/news
	
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
			${sysconfdir}/syslog.d \
			${localstatedir}/log/news \
                        "
FILES_${BPN}-talk = "${bindir}/inetutils-talk"
FILES_${BPN}-talkd = "${sbindir}/talkd"
FILES_${BPN}-telnet = "${bindir}/inetutils-telnet"
FILES_${BPN}-telnetd = "${sbindir}/telnetd"
FILES_${BPN}-tools = "${bindir}/inetutils-ifconfig"
FILES_${BPN}-traceroute = "${bindir}/inetutils-traceroute"

ALTERNATIVE_PRIORITY = "100"
ALTERNATIVE_${BPN}-ftp = "ftp"
ALTERNATIVE_LINK_NAME[ftp] = "${bindir}/ftp"
ALTERNATIVE_TARGET[ftp] = "${bindir}/inetutils-ftp"

ALTERNATIVE_${BPN}-traceroute = "traceroute"
ALTERNATIVE_LINK_NAME[traceroute] = "${bindir}/traceroute"
ALTERNATIVE_TARGET[traceroute] = "${bindir}/inetutils-traceroute"

ALTERNATIVE_${BPN}-talk = "talk"
ALTERNATIVE_LINK_NAME[talk] = "${bindir}/talk"
ALTERNATIVE_TARGET[talk] = "${bindir}/inetutils-talk"

ALTERNATIVE_${BPN}-telnet = "telnet"
ALTERNATIVE_LINK_NAME[telnet] = "${bindir}/telnet"
ALTERNATIVE_TARGET[telnet] = "${bindir}/inetutils-telnet"

RDEPENDS_${BPN}-ftp += "netbase"
RDEPENDS_${BPN}-ftpd += "netbase"
RDEPENDS_${BPN}-inetd += "tcpd lsb-base"
RDEPENDS_${BPN}-ping += "netbase"
RDEPENDS_${BPN}-traceroute += "netbase"
RDEPENDS_${BPN}-syslogd += "netbase lsb-base"
RDEPENDS_${BPN}-talk += "netbase"
RDEPENDS_${BPN}-talkd += "netbase"
RDEPENDS_${BPN}-telnet += "netbase"
RDEPENDS_${BPN}-telnetd += "netbase"

# Base on debian/inetutils-inetd.preinst
pkg_preinst_${BPN}-inetd() {
    create_inetd_conf() {
        [ -e $D${sysconfdir}/inetd.conf ] && return 0

        cat <<EOF > $D${sysconfdir}/inetd.conf
# /etc/inetd.conf:  see inetd(8) for further informations.
#
# Internet superserver configuration database
#
#
# Lines starting with "#:LABEL:" or "#<off>#" should not
# be changed unless you know what you are doing!
#
# If you want to disable an entry so it isn't touched during
# package updates just comment it out with a single '#' character.
#
# Packages should modify this file by using update-inetd(8)
#
# <service_name> <sock_type> <proto> <flags> <user> <server_path> <args>
#
#:INTERNAL: Internal services
#discard                stream  tcp     nowait  root    internal
#discard                dgram   udp     wait    root    internal
#daytime                stream  tcp     nowait  root    internal
#time           stream  tcp     nowait  root    internal

#:STANDARD: These are standard services.

#:BSD: Shell, login, exec and talk are BSD protocols.

#:MAIL: Mail, news and uucp services.

#:INFO: Info services

#:BOOT: TFTP service is provided primarily for booting.  Most sites
#       run this only on machines acting as "boot servers."

#:RPC: RPC based services

#:HAM-RADIO: amateur-radio services

#:OTHER: Other services

EOF

        chmod 644 $D${sysconfdir}/inetd.conf
    }

    create_inetd_conf
}

# Base on debian/inetutils-talkd.postinst
pkg_postinst_${BPN}-talkd() {
    # Fix broken talk entry, and replace with correct one
    update-inetd --group BSD --pattern '.*/talkd' --remove talk --file $D${sysconfdir}/inetd.conf
    update-inetd --group BSD --file $D${sysconfdir}/inetd.conf \
      --add "#<off># ntalk\tdgram\tudp4\twait\troot\t/usr/sbin/tcpd\t/usr/sbin/talkd"
}

# Base on debian/inetutils-telnetd.postinst
pkg_postinst_${BPN}-telnetd() {
    update-inetd --group STANDARD --file $D${sysconfdir}/inetd.conf \
      --add "#<off># telnet\tstream\ttcp\tnowait\troot\t/usr/sbin/tcpd\t/usr/sbin/telnetd"
}
