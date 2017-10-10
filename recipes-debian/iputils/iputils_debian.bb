#
# Base recipe: meta/recipes-extended/iputils/iputils_s20121221.bb
# Base branch: daisy
#

SUMMARY = "Network monitoring tools"
DESCRIPTION = "Utilities for the IP protocol, including traceroute6, \
tracepath, tracepath6, ping, ping6 and arping."

PR = "r1"

inherit debian-package
PV = "20121221"

LICENSE = "BSD & GPLv2+"
LIC_FILES_CHKSUM = " \
file://ping.c;beginline=1;endline=35;md5=f9ceb201733e9a6cf8f00766dd278d82 \
file://tracepath.c;beginline=1;endline=10;md5=0ecea2bf60bff2f3d840096d87647f3d \
file://arping.c;beginline=1;endline=11;md5=fe84301b5c2655c950f8b92a057fafa6 \
file://tftpd.c;beginline=1;endline=32;md5=28834bf8a91a5b8a92755dbee709ef96 "

DEPENDS = "libcap gnutls"

do_compile () {
	oe_runmake 'CC=${CC} -D_GNU_SOURCE' VPATH="${STAGING_LIBDIR}:${STAGING_DIR_HOST}/${base_libdir}" all
}

# Instal files follow debian/rules
do_install () {
	install -d ${D}${base_bindir} ${D}${bindir} ${D}${mandir}/man8
	install -m 0755 -o root -g root ${S}/ping ${D}${base_bindir}
	install -m 0755 -o root -g root ${S}/ping6 ${D}${base_bindir}
	install -m 0755 -o root -g root ${S}/traceroute6 ${D}${bindir}/traceroute6.iputils
	install -m 0755 -o root -g root ${S}/tracepath ${D}${bindir}
	install -m 0755 -o root -g root ${S}/tracepath6 ${D}${bindir}
	install -m 0755 -o root -g root ${S}/arping ${D}${bindir}
	install -m 0755 -o root -g root ${S}/clockdiff ${D}${bindir}
	# Manual pages for things we build packages for
	for i in tracepath.8 traceroute6.8 ping.8 arping.8; do
		install -m 0644 doc/$i ${D}${mandir}/man8/ || true
	done
}

# Ship packages follow Debian
PACKAGES =+ "${PN}-ping ${PN}-tracepath ${PN}-clockdiff ${PN}-arping"

FILES_${PN}-ping = "${base_bindir}/ping*"
FILES_${PN}-tracepath = "${bindir}/trace*"
FILES_${PN}-clockdiff = "${bindir}/clockdiff"
FILES_${PN}-arping = "${bindir}/arping*"

# Add update-alternatives definitions
inherit update-alternatives

ALTERNATIVE_PRIORITY = "100"

ALTERNATIVE_${PN}-ping = "ping ping6"
ALTERNATIVE_LINK_NAME[ping] = "${base_bindir}/ping"
ALTERNATIVE_LINK_NAME[ping6] = "${base_bindir}/ping6"

ALTERNATIVE_${PN}-arping = "arping"
ALTERNATIVE_LINK_NAME[arping] = "${bindir}/arping"

ALTERNATIVE_${PN}-tracepath = "traceroute6"
ALTERNATIVE_LINK_NAME[traceroute6] = "${bindir}/traceroute6"
ALTERNATIVE_TARGET[traceroute6] = "${bindir}/traceroute6.iputils"
