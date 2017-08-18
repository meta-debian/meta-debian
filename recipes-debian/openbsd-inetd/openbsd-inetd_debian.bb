SUMMARY = "OpenBSD Internet Superserver"
DESCRIPTION = "The inetd server is a network daemon program that specializes in managing\n\
incoming network connections. Its configuration file tells it what\n\
program needs to be run when an incoming connection is received. Any\n\
service port may be configured for either of the tcp or udp protocols.\n\
.\n\
This is a port of the OpenBSD daemon with some debian-specific features.\n\
This package supports IPv6, built-in libwrap access control, binding to\n\
specific addresses, UNIX domain sockets and socket buffers tuning."

inherit debian-package
PV = "0.20140418"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://inetd.c;beginline=3;endline=30;md5=ccb4076c1b1c0fd44eea72b467e0d33c"

DEPENDS = "libbsd tcp-wrappers"

# Overwrite base_do_configure
do_configure() {
	# Original Makefile does not have target "clean" and is unusable,
	# use Makefile.debian instead
	if [ -n "${CONFIGURESTAMPFILE}" -a -e "${CONFIGURESTAMPFILE}" ]; then
		if [ "`cat ${CONFIGURESTAMPFILE}`" != "${BB_TASKHASH}" ]; then
			cd ${B}
			if [ "${CLEANBROKEN}" != "1" ]; then
				oe_runmake -f ${S}/Makefile.debian clean
			fi
			find ${B} -ignore_readdir_race -name \*.la -delete
		fi
	fi
	if [ -n "${CONFIGURESTAMPFILE}" ]; then
		mkdir -p `dirname ${CONFIGURESTAMPFILE}`
		echo ${BB_TASKHASH} > ${CONFIGURESTAMPFILE}
	fi
}

do_compile() {
	oe_runmake -f ${S}/Makefile.debian
}

do_install() {
	install -d ${D}${sbindir} ${D}${systemd_system_unitdir}

	install -m 0755 ${S}/inetd ${D}${sbindir}/
	cp ${S}/debian/inetd.service ${D}${systemd_system_unitdir}/
	install -D -m 0755 ${S}/debian/openbsd-inetd.init ${D}${sysconfdir}/init.d/openbsd-inetd

	# According to debian/openbsd-inetd.links
	ln -sf inetd.service ${D}${systemd_system_unitdir}/openbsd-inetd.service
}

FILES_${PN} += "${systemd_system_unitdir}"

RDEPENDS_${PN} += "lsb-base update-inetd tcpd"

# Base on debian/openbsd-inetd.preinst
pkg_preinst_${PN}() {
  # create a new /etc/inetd.conf file if it doesn't already exist
  create_inetd() {
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

  create_inetd
}
