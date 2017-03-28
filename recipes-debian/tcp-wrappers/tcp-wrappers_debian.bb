#
# base recipe: meta/recipes-extended/tcp-wrappers/tcp-wrappers_7.6.bb
# base branch: daisy
#

PR = "r0"

inherit debian-package
PV = "7.6.q"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://DISCLAIMER;md5=071bd69cb78b18888ea5e3da5c3127fa"

# rename_strings_variable.patch:
#	STRINGS name conflicts with variable for strings tools,
#	so change it to STRINGS_FLAGS.
SRC_URI += " \
	file://rename_strings_variable.patch \
"

PARALLEL_MAKE = ""
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
                'LIBS=-lnsl' \
                'ARFLAGS=rv' \
                'AUX_OBJ=weak_symbols.o' \
                'TLI=' \
                'COPTS=' \
                'EXTRA_CFLAGS=${CFLAGS} -DSYS_ERRLIST_DEFINED -DHAVE_STRERROR -DHAVE_WEAKSYMS -D_REENTRANT -DINET6=1 -Dss_family=__ss_family -Dss_len=__ss_len'"

EXTRA_OEMAKE_NETGROUP = "-DNETGROUP -DUSE_GETDOMAIN"
EXTRA_OEMAKE_NETGROUP_libc-uclibc = "-DUSE_GETDOMAIN"

do_compile () {
	oe_runmake 'TABLES=-DHOSTS_DENY=\"${sysconfdir}/hosts.deny\" -DHOSTS_ALLOW=\"${sysconfdir}/hosts.allow\"' \
                   all
}

do_install(){
	oe_libinstall -a libwrap ${D}${libdir}
	oe_libinstall -C shared -so libwrap ${D}${base_libdir}

	# Move libwrap.so to ${libdir}
	rel_lib_prefix=`echo ${libdir} | sed 's,\(^/\|\)[^/][^/]*,..,g'`
	libname=`readlink ${D}${base_libdir}/libwrap.so | xargs basename`
	ln -s ${rel_lib_prefix}${base_libdir}/${libname} ${D}${libdir}/libwrap.so
	rm -f ${D}${base_libdir}/libwrap.so

	# Install binary files for tcpd
	install -d ${D}${sbindir}
	BINS=$(cat ${S}/debian/tcpd.install | cut -d' ' -f1)
	for i in $BINS; do
		install -m 0755 $i ${D}${sbindir}/
	done

	# Install man3, man5, man8
	MANS="3 5 8"
	for i in $MANS; do
		if [ ! -d ${D}${mandir}/man$i ]; then
			install -d ${D}${mandir}/man$i;
		fi
		install -m 0644 *.$i ${D}${mandir}/man$i/
	done

	# Install header
	install -d ${D}${includedir}
	install -m 0644 tcpd.h ${D}${includedir}/
}

PACKAGES = "${PN}-dbg libwrap libwrap-doc libwrap-dev libwrap-staticdev ${PN} ${PN}-doc"
FILES_libwrap = "${base_libdir}/lib*${SOLIBS}"
FILES_libwrap-doc = "${mandir}/man3 ${mandir}/man5"
FILES_libwrap-dev = "${libdir}/lib*${SOLIBSDEV} ${includedir}"
FILES_libwrap-staticdev = "${libdir}/lib*.a"
FILES_${PN} = "${sbindir}"
FILES_${PN}-doc = "${mandir}/man8"

DEBIANNAME_${PN} = "tcpd"
RPROVIDES_${PN} += "tcpd"

# Base on libwrap0.postinst
pkg_postinst_libwrap() {
  if [ ! -e $D${sysconfdir}/hosts.allow ]; then
    cat > $D${sysconfdir}/hosts.allow <<EOF
# /etc/hosts.allow: list of hosts that are allowed to access the system.
#                   See the manual pages hosts_access(5) and hosts_options(5).
#
# Example:    ALL: LOCAL @some_netgroup
#             ALL: .foobar.edu EXCEPT terminalserver.foobar.edu
#
# If you're going to protect the portmapper use the name "rpcbind" for the
# daemon name. See rpcbind(8) and rpc.mountd(8) for further information.
#

EOF
  fi

  if [ ! -e $D${sysconfdir}/hosts.deny ]; then
    cat > $D${sysconfdir}/hosts.deny <<EOF
# /etc/hosts.deny: list of hosts that are _not_ allowed to access the system.
#                  See the manual pages hosts_access(5) and hosts_options(5).
#
# Example:    ALL: some.host.name, .some.domain
#             ALL EXCEPT in.fingerd: other.host.name, .other.domain
#
# If you're going to protect the portmapper use the name "rpcbind" for the
# daemon name. See rpcbind(8) and rpc.mountd(8) for further information.
#
# The PARANOID wildcard matches any host whose name does not match its
# address.
#
# You may wish to enable this to ensure any programs that don't
# validate looked up hostnames still leave understandable logs. In past
# versions of Debian this has been the default.
# ALL: PARANOID

EOF
  fi
}
