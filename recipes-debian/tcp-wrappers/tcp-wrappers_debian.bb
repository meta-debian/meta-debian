#
# base recipe: meta/recipes-extended/tcp-wrappers_7.6.bb
# base branch: warrior
# base commit: de02e697963e1a86403b5438ee9de03d476d0b46
#

SUMMARY = "Security tool that is a wrapper for TCP daemons"
HOMEPAGE = "http://www.softpanorama.org/Net/Network_security/TCP_wrappers/"
DESCRIPTION = "Tools for monitoring and filtering incoming requests for tcp \
               services."
SECTION = "console/network"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://DISCLAIMER;md5=071bd69cb78b18888ea5e3da5c3127fa"

inherit debian-package
require recipes-debian/sources/tcp-wrappers.inc

python () {
   # When PV is 7.6.q, DEBIAN_UNPACK_PV should be 7.6
   import re
   d.setVar('DEBIAN_UNPACK_PV', re.sub("\.[a-z]$", "", d.getVar('PV')))
}

DEBIAN_UNPACK_DIR = "${WORKDIR}/tcp_wrappers_${DEBIAN_UNPACK_PV}"

DEPENDS += "libnsl2"

PACKAGES = "${PN}-dbg libwrap libwrap-doc libwrap-dev libwrap-staticdev ${PN} ${PN}-doc"
FILES_libwrap = "${base_libdir}/lib*${SOLIBS}"
FILES_libwrap-doc = "${mandir}/man3 ${mandir}/man5"
FILES_libwrap-dev = "${libdir}/lib*${SOLIBSDEV} ${includedir}"
FILES_libwrap-staticdev = "${libdir}/lib*.a"
FILES_${PN} = "${sbindir}"
FILES_${PN}-doc = "${mandir}/man8"

FILESPATH_append = ":${COREBASE}/meta/recipes-extended/tcp-wrappers/tcp-wrappers-7.6"

SRC_URI += " \
	   file://rename_strings_variable.patch \
	   file://try-from.8 \
	   file://safe_finger.8 \
	   file://makefile-fix-parallel.patch \
	   file://musl-decls.patch \
           "

EXTRA_OEMAKE = "'CC=${CC}' \
                'AR=${AR}' \
                'RANLIB=${RANLIB}' \
                'REAL_DAEMON_DIR=${sbindir}' \
                'STYLE=-DPROCESS_OPTIONS' \
                'FACILITY=LOG_DAEMON' \
                'SEVERITY=LOG_INFO' \
                'BUGS=' \
                'VSYSLOG=' \
                'RFC931_TIMEOUT=10' \
                'ACCESS=-DHOSTS_ACCESS' \
                'KILL_OPT=-DKILL_IP_OPTIONS' \
                'UMASK=-DDAEMON_UMASK=022' \
                'NETGROUP=${EXTRA_OEMAKE_NETGROUP}' \
                'ARFLAGS=rv' \
                'AUX_OBJ=weak_symbols.o' \
                'TLI=' \
                'COPTS=' \
                'LDOPTS=${LDFLAGS}' \
                'EXTRA_CFLAGS=${CFLAGS} -DSYS_ERRLIST_DEFINED -DHAVE_STRERROR -DHAVE_WEAKSYMS -D_REENTRANT -DINET6=1 -Dss_family=__ss_family -Dss_len=__ss_len'"

EXTRA_OEMAKE_NETGROUP = "-DNETGROUP -DUSE_GETDOMAIN"
EXTRA_OEMAKE_NETGROUP_libc-musl = "-DUSE_GETDOMAIN"

EXTRA_OEMAKE_append_libc-musl = " 'LIBS='"

do_compile () {
	oe_runmake 'TABLES=-DHOSTS_DENY=\"${sysconfdir}/hosts.deny\" -DHOSTS_ALLOW=\"${sysconfdir}/hosts.allow\"' \
		   all
}

BINS = "safe_finger tcpd tcpdchk try-from tcpdmatch"
MANS3 = "hosts_access"
MANS5 = "hosts_options"
MANS8 = "tcpd tcpdchk tcpdmatch"
do_install () {
	oe_libinstall -a libwrap ${D}${libdir}
	oe_libinstall -C shared -so libwrap ${D}${base_libdir}

	if [ "${libdir}" != "${base_libdir}" ] ; then
		rel_lib_prefix=`echo ${libdir} | sed 's,\(^/\|\)[^/][^/]*,..,g'`
		libname=`readlink ${D}${base_libdir}/libwrap.so | xargs basename`
		ln -s ${rel_lib_prefix}${base_libdir}/${libname} ${D}${libdir}/libwrap.so
		rm -f ${D}${base_libdir}/libwrap.so
	fi

	install -d ${D}${sbindir}
	for b in ${BINS}; do
		install -m 0755 $b ${D}${sbindir}/ || exit 1
	done

	install -d ${D}${mandir}/man3
	for m in ${MANS3}; do
		install -m 0644 $m.3 ${D}${mandir}/man3/ || exit 1
	done

	install -d ${D}${mandir}/man5
	for m in ${MANS5}; do
		install -m 0644 $m.5 ${D}${mandir}/man5/ || exit 1
	done

	install -d ${D}${mandir}/man8
	for m in ${MANS8}; do
		install -m 0644 $m.8 ${D}${mandir}/man8/ || exit 1
	done

	install -m 0644 ${WORKDIR}/try-from.8 ${D}${mandir}/man8/
	install -m 0644 ${WORKDIR}/safe_finger.8 ${D}${mandir}/man8/

	install -d ${D}${includedir}
	install -m 0644 tcpd.h ${D}${includedir}/

	install -d ${D}${sysconfdir}
	touch ${D}${sysconfdir}/hosts.allow
	touch ${D}${sysconfdir}/hosts.deny
}

FILES_${PN} += "${sysconfdir}/hosts.allow ${sysconfdir}/hosts.deny"
