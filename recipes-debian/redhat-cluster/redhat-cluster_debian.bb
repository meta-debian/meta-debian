SUMMARY = "Red Hat cluster suite"
DESCRIPTION = "RHCS is a cluster management infrastructure, for building\n\
high-availability multi-node clusters with service and IP failover on\n\
top of shared Fibre Channel/iSCSI storage devices.\n\
.\n\
The only scope for this package is to install the full Red Hat Cluster Suite\n\
in one operation. It is safe to remove it."
LICENSE = "GPL-2.0 & LGPL-2.1"
SECTION = "admin"
DEPENDS = "slang openais libxml2 openldap dbus"
# redhat-cluster depends on corosync-1.x, not corosync-2.x"
DEPENDS += "corosync"
LIC_FILES_CHKSUM = "file://doc/COPYING.applications;md5=751419260aa954499f7abaabaa882bbe \
file://doc/COPYING.libraries;md5=2d5025d4aa3495befef8f17206a5b0a1"

PR = "r0"
inherit debian-package
PV = "3.1.8"

inherit autotools-brokensep

CONFIGUREOPTS = " --prefix=${prefix} \
		  --sbindir=${sbindir} \
		  --libexecdir=${libexecdir} \
		  --libdir=${libdir} \
		  --incdir=${includedir} \
		  --mandir=${mandir}"

EXTRA_OECONF = " \
--corosyncincdir=${STAGING_INCDIR} \
--corosynclibdir=${STAGING_LIBDIR}/corosync \
--slangincdir=${STAGING_INCDIR} \
--slanglibdir=${STAGING_LIBDIR} \
--ncursesincdir=${STAGING_INCDIR} \
--ncurseslibdir=${STAGING_LIBDIR} \
--openaisincdir=${STAGING_INCDIR} \
--openaislibdir=${STAGING_LIBDIR}/openais \
--ldapincdir=${STAGING_INCDIR} \
--ldaplibdir=${STAGING_LIBDIR} \
--zlibincdir=${STAGING_INCDIR} \
--zliblibdir=${STAGING_LIBDIR} \
--disable_kernel_check"

inherit cpan-base perlnative

# follow cpan.bbclass
# Env var which tells perl if it should use host (no) or target (yes) settings
export PERLCONFIGTARGET = "${@is_target(d)}"

# Env var which tells perl where the perl include files are
export PERL_INC = "${STAGING_LIBDIR}${PERL_OWN_DIR}/perl/${@get_perl_version(d)}/CORE"
export PERL_LIB = "${STAGING_LIBDIR}${PERL_OWN_DIR}/perl/${@get_perl_version(d)}"
export PERL_ARCHLIB = "${STAGING_LIBDIR}${PERL_OWN_DIR}/perl/${@get_perl_version(d)}"
export PERLHOSTLIB = "${STAGING_LIBDIR_NATIVE}/perl-native/perl/${@get_perl_version(d)}/"

do_configure_append() {
	sed -i -e "s@^CC.*@CC = ${CC}@" make/defines.mk
	sed -i -e "s@^AR.*@AR = ${AR}@" make/defines.mk
	sed -i -e "s@^RANLIB.*@RANLIB = ${RANLIB}@" make/defines.mk
	sed -i -e "/{libdir}/d" $(find -name Makefile)
	sed -i -e "s@$"{libdir}"@${D}$"{libdir}"@g" make/install.mk
	sed -i -e "s@$"{sbindir}"@${D}$"{sbindir}"@g" make/install.mk
	sed -i -e "s@$"{libexecdir}"@${D}$"{libexecdir}"@g" make/install.mk
	sed -i -e "s@$"{docdir}"@${D}$"{docdir}"@g" make/install.mk
	sed -i -e "s@$"{mandir}"@${D}$"{mandir}"@g" make/install.mk

	sed -i -e "s@$"{incdir}"@${STAGING_INCDIR}@g" \
	    rgmanager/src/utils/Makefile \
	    rgmanager/src/clulib/Makefile \
	    rgmanager/src/daemons/Makefile \
	    fence/libfence/Makefile \
	    fence/fenced/Makefile \
	    fence/fence_node/Makefile \
	    fence/fence_tool/Makefile \
	    fence/libfenced/Makefile \
	    group/test/Makefile \
	    group/lib/Makefile \
	    group/dlm_controld/Makefile \
	    group/daemon/Makefile \
	    group/tool/Makefile \
	    common/liblogthread/Makefile \
	    cman/qdisk/Makefile \
	    cman/notifyd/Makefile \
	    cman/cman_tool/Makefile \
	    cman/tests/Makefile \
	    cman/lib/Makefile \
	    cman/daemon/Makefile \
	    dlm/tests/usertest/Makefile \
	    dlm/libdlmcontrol/Makefile \
	    dlm/tool/Makefile \
	    dlm/libdlm/Makefile \
	    contrib/libaislock/Makefile \
	    config/plugins/ldap/Makefile \
	    config/plugins/xml/Makefile \
	    config/tools/ccs_tool/Makefile \
	    config/tools/ldap/rng2ldif/Makefile \
	    config/tools/ldap/Makefile \
	    config/tools/xml/Makefile \
	    config/libs/libccsconfdb/Makefile \
	    bindings/perl/ccs/Makefile.bindings
	sed -i -e "s@$"{libdir}"@${STAGING_LIBDIR}@g" \
	    bindings/perl/ccs/Makefile.bindings
}

do_install_append() {
	# follow debian/rules

	# no need for upstream doc install
	rm -rf ${D}${datadir}/doc/cluster

	# manual craft
	install -d -m 0755 ${D}${sysconfdir}/cluster

	install -d -m 0755 ${D}${base_libdir}/udev/rules.d
	mv ${D}${base_libdir}/udev/rules.d/51-dlm.rules ${D}${base_libdir}/udev/rules.d/45-dlm.rules

	# follow debian/*.default
	install -d ${D}${sysconfdir}/default
	for f in cman rgmanager
	do
		install ${S}/debian/${f}.default ${D}${sysconfdir}/default/${f}
	done

	# follow debian/*.logrotate
	install -d ${D}${sysconfdir}/logrotate.d
	install ${S}/debian/cman.logrotate ${D}${sysconfdir}/logrotate.d/cman
}

PACKAGES =+ "cman \
libccs-dev \
libccs-staticdev \
libccs-perl \
libccs-perl-dbg \
libccs \
libcman-dev \
libcman-staticdev \
libcman \
libdlm-dev \
libdlm-staticdev \
libdlm \
libdlm_lt \
libdlmcontrol-dev \
libdlmcontrol-staticdev \
libdlmcontrol \
libfence-dev \
libfence-staticdev \
libfence \
libfenced \
liblogthread-dev \
liblogthread-staticdev \
liblogthread \
redhat-cluster-suite \
rgmanager"

FILES_cman = "${sysconfdir}/default/cman \
${sysconfdir}/init.d/cman \
${sysconfdir}/logrotate.d/cman \
${libdir}/lcrso/config_cmanpre.lcrso \
${libdir}/lcrso/config_ldap.lcrso \
${libdir}/lcrso/config_xml.lcrso \
${libdir}/lcrso/service_cman.lcrso \
${sbindir}/ccs_config_dump \
${sbindir}/ccs_config_validate \
${sbindir}/ccs_test \
${sbindir}/ccs_tool \
${sbindir}/ccs_update_schema \
${sbindir}/cman_notify \
${sbindir}/cman_tool \
${sbindir}/cmannotifyd \
${sbindir}/confdb2ldif \
${sbindir}/dlm_controld \
${sbindir}/dlm_tool \
${sbindir}/fence_node \
${sbindir}/fence_tool \
${sbindir}/fenced \
${sbindir}/group_tool \
${sbindir}/groupd \
${sbindir}/mkqdisk \
${sbindir}/qdiskd \
${datadir}/cluster/cluster.rng \
${datadir}/cluster/relaxng/cluster.rng.in.head \
${datadir}/cluster/relaxng/cluster.rng.in.tail \
${localstatedir} \
/run \
"
RDEPENDS_cman = "libccs libcman libconfdb libcpg dbus-lib \
libdlm libdlmcontrol libfence libldap liblogthread \
libnet-snmp-perl libnet-telnet-perl libsackpt libxml2 \
libxml2-utils openais openipmi openssh-client python \
python-openssl python-pexpect sg3-utils snmp inetutils-telnet libxslt-bin \
zlib"

FILES_libccs-dev = "${includedir}/ccs.h \
${libdir}/libccs.so \
${libdir}/pkgconfig/libccs.pc"
RDEPENDS_libccs-dev = "libccs"

FILES_libccs-staticdev = "${libdir}/libccs.a"

FILES_libccs-perl = "${libdir}/perl/vendor_perl/5.20.2/Cluster/CCS.pm \
${libdir}/perl/vendor_perl/5.20.2/auto/Cluster/CCS/CCS.so"
RDEPENDS_libccs-perl = "libccs libldap libxml2 perl perl-base"

FILES_libccs-perl-dbg = "${libdir}/perl/vendor_perl/5.20.2/auto/Cluster/CCS/.debug"

FILES_libccs = "${libdir}/libccs.so.3 \
${libdir}/libccs.so.3.0"
RDEPENDS_libccs = "libconfdb libldap libxml2"

FILES_libcman-dev = "${includedir}/libcman.h \
${libdir}/libcman.so \
${libdir}/pkgconfig/libcman.pc"
RDEPENDS_libcman-dev = "libcman"

FILES_libcman-staticdev = "${libdir}/libcman.a"

FILES_libcman = "${libdir}/libcman.so.3 \
${libdir}/libcman.so.3.0"
RDEPENDS_libcman = "libldap libxml2"

FILES_libdlm-dev = "${includedir}/libdlm.h \
${libdir}/libdlm.so \
${libdir}/libdlm_lt.so \
${libdir}/pkgconfig/libdlm.pc \
${libdir}/pkgconfig/libdlm_lt.pc"
RDEPENDS_libdlm-dev = "libdlm"

FILES_libdlm-staticdev = "${libdir}/libdlm.a \
${libdir}/libdlm_lt.a"

FILES_libdlm = "/lib/udev/rules.d/45-dlm.rules \
${libdir}/libdlm.so.3 \
${libdir}/libdlm.so.3.0"
RDEPENDS_libdlm = "libdlm_lt libldap libxml2"
FILES_libdlm_lt = "${libdir}/libdlm_lt.so.3 \
${libdir}/libdlm_lt.so.3.0"

FILES_libdlmcontrol-dev = "${includedir}/libdlmcontrol.h \
${libdir}/libdlmcontrol.so \
${libdir}/pkgconfig/libdlmcontrol.pc"
RDEPENDS_libdlmcontrol-dev = "libdlmcontrol"

FILES_libdlmcontrol-staticdev = "${libdir}/libdlmcontrol.a"

FILES_libdlmcontrol = "${libdir}/libdlmcontrol.so.3 \
${libdir}/libdlmcontrol.so.3.1"
RDEPENDS_libdlmcontrol = "libldap libxml2"

FILES_libfence-dev = "${includedir}/libfence.h \
${includedir}/libfenced.h \
${libdir}/libfence.so \
${libdir}/libfenced.so \
${libdir}/pkgconfig/libfence.pc \
${libdir}/pkgconfig/libfenced.pc"
RDEPENDS_libfence-dev = "libfence"

FILES_libfence-staticdev = "${libdir}/libfence.a \
${libdir}/libfenced.a"

FILES_libfence = "${libdir}/libfence.so.4 \
${libdir}/libfence.so.4.0"
RDEPDNS_libfence = "libfenced libccs libldap libxml2"
FILES_libfenced = "${libdir}/libfenced.so.3 \
${libdir}/libfenced.so.3.0"

FILES_liblogthread-dev = "${includedir}/liblogthread.h \
${libdir}/liblogthread.so \
${libdir}/pkgconfig/liblogthread.pc"
RDEPENDS_liblogthread-dev = "liblogthread"

FILES_liblogthread-staticdev = "${libdir}/liblogthread.a"

FILES_liblogthread = "${libdir}/liblogthread.so.3 \
${libdir}/liblogthread.so.3.0"
RDEPENDS_liblogthread = "libldap libxml2"

# metapackage
FILES_redhat-cluster-suite = ""
ALLOW_EMPTY_redhat-cluster-suite = "1"
RDEPENDS_redhat-cluster-suite = "cman fence-agents gfs2-utils \
resource-agents rgmanager"

FILES_rgmanager = "${sysconfdir}/default/rgmanager \
${sysconfdir}/init.d/rgmanager \
${sbindir}/clubufflush \
${sbindir}/clufindhostname \
${sbindir}/clulog \
${sbindir}/clunfslock \
${sbindir}/clurgmgrd \
${sbindir}/clustat \
${sbindir}/clusvcadm \
${sbindir}/rg_test \
${sbindir}/rgmanager \
${datadir}/cluster/checkquorum \
${datadir}/cluster/default_event_script.sl \
${datadir}/cluster/follow-service.sl"
RDPENDS_rgmanager = "cman gawk iproute iputils-arping \
iputils-ping libccs libcman dbus-lib libdlm libldap \
liblogthread libncurses libslang libtinfo libxml2 net-tools \
nfs-common nfs-kernel-server perl"
