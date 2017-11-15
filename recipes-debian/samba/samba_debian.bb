SUMMARY = "SMB/CIFS file, print, and login server for Unix"
DESCRIPTION = "Samba is an implementation of the SMB/CIFS protocol for Unix systems, \
providing support for cross-platform file and printer sharing with \
Microsoft Windows, OS X, and other Unix systems.  Samba can also function \
as an NT4-style domain controller, and can integrate with both NT4 domains \
and Active Directory realms as a member server"
HOMEPAGE = "http://www.samba.org"

inherit debian-package

PV = "4.2.14+dfsg"

LICENSE = "GPLv3+ & LGPLv3+ & BSD-3-Clause & ISC & MIT & LGPLv2.1+ \
           & (Apache-2.0 | BSD-3-Clause) & PD & BSL-1.0 & PostgreSQL"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
	file://lib/ccan/compiler/LICENSE;md5=b52f2d57d10c4f7ee67a7eb9615d5d24 \
	file://lib/dnspython/LICENSE;md5=397eddfcb4bc7e2ece2fc79724a7cca2 \
	file://lib/ccan/time/LICENSE;md5=838c366f69b72c5df05c96dff79b35f2 \
	file://lib/ccan/asearch/LICENSE;md5=2caced0b25dfefd4c601d92bd15116de \
	file://lib/subunit/Apache-2.0;md5=3b83ef96387f14655fc854ddc3c6bd57 \
	file://lib/subunit/BSD;md5=87e4d67ebb33a18a9c97f05899f8b3ee \
	file://source4/heimdal/lib/hcrypto/libtommath/LICENSE;md5=4f6fbdd737299a6d5dac1428f38422c8 \
	file://third_party/zlib/contrib/dotzlib/LICENSE_1_0.txt;md5=81543b22c36f10d20ac9712f8d80ef8d \
	file://lib/replace/getaddrinfo.c;endline=26;md5=2a9c0f540bb750338375017f470050c5 \
"

inherit waf-samba perlnative pkgconfig useradd

DEPENDS = "readline zlib ntdb ldb libaio heimdal libarchive libpam libldap lsb tevent"
SRC_URI += " \
	file://20-do-not-import-target-module-while-cross-compile.patch \
	file://0006-avoid-using-colon-in-the-checking-msg.patch \
	file://libreplace-disable-libbsd-support.patch \
"
EXTRA_OECONF += "\
	--disable-cups \
	--enable-fhs \
	--with-privatedir=${localstatedir}/lib/samba/private \
	--with-smbpasswd-file=${sysconfdir}/samba/smbpasswd \
	--with-piddir=${localstatedir}/run/samba \
	--with-pammodulesdir=${base_libdir}/security \
	--with-pam \
	--with-syslog \
	--with-automount \
	--with-utmp \
	--with-pam_smbpass \
	--with-winbind \
	--with-shared-modules=idmap_rid,idmap_ad,idmap_adex,idmap_hash,idmap_ldap,idmap_tdb2,vfs_dfs_samba4,auth_samba4 \
	--with-ldap \
	--with-ads \
	--with-dnsupdate \
	--with-modulesdir=${libdir}/samba \
	--with-lockdir=${localstatedir}/run/samba \
	--with-statedir=${localsatedir}/lib/samba \
	--with-cachedir=${localstatedir}/cache/samba \
	--disable-avahi \
	--disable-rpath \
	--disable-rpath-install \
	--bundled-libraries=NONE,pytevent,iniparser,roken,wind,hx509,asn1,heimbase,hcrypto,krb5,gssapi,heimntlm,hdb,kdc,com_err,subunit \
	--builtin-libraries=ccan \
	--minimum-library-version=${MIN_VER} \
	--with-cluster-support \
	--with-socketpath=${localstatedir}/run/ctdb/ctdbd.socket \
	--with-logdir=${localstatedir}/log/ctdb \
	--with-libiconv=${STAGING_DIR_HOST}${prefix} \
"

do_configure_prepend() {
	# Get minimum-library-version follow debian/rules
	MIN_VER=$(./debian/autodeps.py --minimum-library-version .)
}

do_install_append() {
	#MIT plugin not required, we are using Heimdal
	rm -rf ${D}/${libdir}/mit_samba.so

	# Included in python-tevent
	rm ${D}/${libdir}/${PYTHON_DIR}/*-packages/_tevent.so
	rm ${D}/${libdir}/${PYTHON_DIR}/*-packages/tevent.py

	#Already included in various system packages
	rm -rf ${D}/${libdir}/${PYTHON_DIR}/*-packages/samba/external

	# System ldb loads its modules from a different path
	mkdir -p ${D}${libdir}/ldb/modules/ldb
	ln -s ../../../samba/ldb ${D}${libdir}/ldb/modules/ldb/samba

	# pam stuff
	mkdir -p ${D}/${datadir}/pam-configs
	install -m 0644 debian/libpam-smbpass.pam-config \
		${D}/${datadir}/pam-configs/smbpasswd-migrate
	install -m 0644 debian/winbind.pam-config \
		${D}/${datadir}/pam-configs/winbind
	mv ${D}/${libdir}/libnss_* ${D}/${base_libdir}

	# we don't ship the symlinks
	rm -rf ${D}/${base_libdir}/libnss_*.so

	#Remove unused vfstest manpage as there is no more vfstest apparently
	rm -rf ${D}/${datadir}/man/man1/vfstest.1

	mkdir -p ${D}/${libdir}/plugin/krb5
	mv ${D}/${libdir}/winbind_krb5_locator.so \
	   ${D}/${libdir}/plugin/krb5

	install -m 0755 debian/setoption.py ${D}/${datadir}/samba
	install -m 0755 debian/addshare.py ${D}/${datadir}/samba
	mkdir -p ${D}/${libdir}/tmpfiles.d
	echo "d /run/samba 0755 root root -" > ${D}/${libdir}/tmpfiles.d/samba.conf
	cp debian/smb.conf* ${D}/${datadir}/samba
	install -m755 debian/panic-action ${D}/${datadir}/samba/panic-action
	cp debian/gdbcommands ${D}/${sysconfdir}/samba/

	mkdir -p ${D}/${sysconfdir}/dhcp/dhclient-enter-hooks.d
	install -m755 debian/samba-common.dhcp \
		${D}/${sysconfdir}/dhcp/dhclient-enter-hooks.d/samba

	# Install mksmbpasswd
	install -m 0755 ${S}/debian/mksmbpasswd.awk ${D}/${sbindir}/mksmbpasswd

	# Remove ctdb dev files
	rm -rf ${D}/${includedir}/samba-4.0/ctdb*.h \
	       ${D}/${libdir}/pkgconfig/ctdb.pc

	# Remove ctdb tests
	rm -rf ${D}/${bindir}/ctdb_run_tests \
	       ${D}/${bindir}/ctdb_run_cluster_tests \
	       ${D}/${libdir}/ctdb-tests \
	       ${D}/${datadir}/ctdb-tests

	# Install /etc/default/ctdb
	install -d ${D}${sysconfdir}/default \
	           ${D}${sysconfdir}/init.d
	install -m644 ctdb/config/ctdb.sysconfig ${D}${sysconfdir}/default/ctdb

	# Install /etc/init.d/ctdb
	install -m755 ctdb/config/ctdb.init ${D}${sysconfdir}/init.d/ctdb

	# Install logrotate config files
	mkdir -p ${D}${sysconfdir}/logrotate.d
	install -m 0644 ${S}/debian/ctdb.logrotate \
		${D}${sysconfdir}/logrotate.d/ctdb
	install -m 0644 ${S}/debian/samba.logrotate \
		${D}${sysconfdir}/logrotate.d/samba
	install -m 0644 ${S}/debian/winbind.logrotate \
		${D}${sysconfdir}/logrotate.d/winbind

	# Install logcheck ignore server
	install -d ${D}${sysconfdir}/logcheck/ignore.d.server
	install -m 0644 ${S}/debian/libpam-smbpass.logcheck.ignore.server \
			${D}/${sysconfdir}/logcheck/ignore.d.server/libpam-smbpass

	# Install systemd files
	install -d ${D}${systemd_system_unitdir}
	install -m 0644 ${S}/ctdb/config/ctdb.service \
		${D}${systemd_system_unitdir}

	# Base on debian/samba.links
	ln -sf /dev/null ${D}${systemd_system_unitdir}/samba.service

	# Initscripts
	install -D -m 0755 ${S}/debian/samba.init \
			${D}${sysconfdir}/init.d/samba
	install -m 0755 ${S}/debian/samba.nmbd.init \
			${D}${sysconfdir}/init.d/nmbd
	install -m 0755 ${S}/debian/samba.smbd.init \
			${D}${sysconfdir}/init.d/smbd
	install -m 0755 ${S}/debian/samba.samba-ad-dc.init \
			${D}${sysconfdir}/init.d/samba-ad-dc
	install -m 0755 ${S}/debian/winbind.init \
			${D}/${sysconfdir}/init.d/winbind

	install -D -m 0644 ${S}/debian/samba.nmbd.upstart \
			${D}${sysconfdir}/init/nmbd.conf
	install -m 0644 ${S}/debian/samba.smbd.upstart \
			${D}${sysconfdir}/init/smbd.conf
	install -m 0644 ${S}/debian/samba.reload-smbd.upstart \
			${D}${sysconfdir}/init/reload-smbd.conf
	install -m 0644 ${S}/debian/samba.upstart.in \
			${D}${sysconfdir}/init/samba-ad-dc.conf
	install -m 0644 ${S}/debian/winbind.upstart \
			${D}${sysconfdir}/init/winbind.conf

	install -D -m 0644 ${S}/debian/winbind.default \
			${D}${sysconfdir}/default/winbind

	install -D -m 0755 ${S}/debian/samba.cron.daily \
			${D}${sysconfdir}/cron.daily/samba

	install -D -m 0644 ${S}/debian/samba-common.samba.pam \
			${D}${sysconfdir}/pam.d/samba

	# Remove unused files
	rm -rf ${D}${base_libdir}/samba \
	       ${D}${libdir}/samba/share \
	       ${D}${libdir}/samba/libsubunit.so.* \
	       ${D}${libdir}/samba/vfs/snapper.so \
	       ${D}${datadir}/perl5/Parse/Yapp/Driver.pm \
	       ${D}${datadir}/samba/setup/ad-schema/licence.txt

	# Base on debian/samba.dirs
	install -d ${D}${localstatedir}/lib/samba/printers/COLOR
	install -d ${D}${localstatedir}/lib/samba/printers/IA64
	install -d ${D}${localstatedir}/lib/samba/printers/W32ALPHA
	install -d ${D}${localstatedir}/lib/samba/printers/W32MIPS
	install -d ${D}${localstatedir}/lib/samba/printers/W32PPC
	install -d ${D}${localstatedir}/lib/samba/printers/W32X86
	install -d ${D}${localstatedir}/lib/samba/printers/WIN40
	install -d ${D}${localstatedir}/lib/samba/printers/x64
	install -d ${D}${localstatedir}/spool/samba
}

# Base on debian/samba.postinst and debian/winbind.postinst
USERADD_PACKAGES = "${PN} winbind"
GROUPADD_PARAM_${PN} = "-r sambashare"
GROUPADD_PARAM_winbind = "-r winbindd_priv"

pkg_postinst_${PN}() {
	if [ ! -e $D${localstatedir}/lib/samba/usershares ]
	then
		install -d -m 1770 -g sambashare $D${localstatedir}/lib/samba/usershares
	fi
}

pkg_postinst_${PN}-common() {
	mkdir -p $D${localstatedir}/run/samba/upgrades
	cp $D${datadir}/samba/smb.conf $D${localstatedir}/run/samba/upgrades/smb.conf
	cp $D${localstatedir}/run/samba/upgrades/smb.conf $D${sysconfdir}/samba/smb.conf
}

PACKAGES =+ "\
	ctdb libnss-winbind libpam-smbpass libpam-winbind libparse-pidl-perl \
	libsmbclient-dev libsmbclient libwbclient libwbclient-dev \
	registry-tools ${PN}-common-bin ${PN}-common ${PN}-dsdb-modules \
	${PN}-testsuite winbind ${PN}-vfs-modules smbclient ${PN}-libs"
PACKAGES += "python-samba"

FILES_${PN}-dsdb-modules = "\
	${libdir}/samba/ldb/* \
	${libdir}/ldb/modules/ldb/samba"
FILES_${PN}-testsuite = "\
	${bindir}/gentest \
	${bindir}/locktest \
	${bindir}/masktest \
	${bindir}/ndrdump \
	${bindir}/smbtorture \
	${libdir}/libtorture${SOLIBS} \
	${libdir}/samba/libdlz-bind9-for-torture${SOLIBS}"
FILES_${PN}-vfs-modules = "${libdir}/samba/vfs/*.so"
FILES_smbclient = "\
	${bindir}/cifsdd \
	${bindir}/rpcclient \
	${bindir}/smbcacls \
	${bindir}/smbclient \
	${bindir}/smbcquotas \
	${bindir}/smbget \
	${bindir}/smbspool \
	${bindir}/smbspool_krb5_wrapper \
	${bindir}/smbtar \
	${bindir}/smbtree"
FILES_ctdb = "\
	${sysconfdir}/ctdb \
	${sysconfdir}/default/ctdb \
	${sysconfdir}/sudoers.d/ctdb \
	${sysconfdir}/init.d/ctdb \
	${sysconfdir}/logrotate.d/ctdb \
	${systemd_system_unitdir}/ctdb.service \
	${bindir}/ctdb* \
	${bindir}/ltdbtool \
	${bindir}/onnode \
	${bindir}/ping_pong \
	${bindir}/smnotify \
	${sbindir}/ctdbd* \
	${localstatedir}/log/ctdb \
	${localstatedir}/lib/ctdb"
FILES_libnss-winbind = "\
	${base_libdir}/libnss_*"
FILES_libpam-smbpass = "\
	${base_libdir}/security/pam_smbpass.so \
	${datadir}/pam-configs/smbpasswd-migrate \
	${sysconfdir}/logcheck/ignore.d.server/libpam-smbpass"
FILES_libpam-winbind = "\
	${base_libdir}/security/pam_winbind.so \
	${datadir}/pam-configs/winbind"
FILES_libparse-pidl-perl = "\
	${bindir}/pidl ${datadir}/perl5"
FILES_libsmbclient-dev = "\
	${includedir}/samba-4.0/libsmbclient.h \
	${libdir}/libsmbclient.so \
	${libdir}/pkgconfig/smbclient.pc"
FILES_libsmbclient = "\
	${libdir}/libsmbclient${SOLIBS}"
FILES_libwbclient-dev = "\
	${includedir}/samba-4.0/wbclient.h \
	${libdir}/libwbclient.so \
	${libdir}/pkgconfig/wbclient.pc"
FILES_libwbclient = "\
	${libdir}/libwbclient${SOLIBS} \
	${libdir}/samba/libwinbind-client${SOLIBS}"
FILES_registry-tools = "\
	${bindir}/regdiff \
	${bindir}/regpatch \
	${bindir}/regshell \
	${bindir}/regtree"
FILES_${PN}-common-bin = "\
	${bindir}/dbwrap_tool \
	${bindir}/net \
	${bindir}/nmblookup \
	${bindir}/samba-regedit \
	${bindir}/samba-tool \
	${bindir}/smbpasswd \
	${bindir}/testparm \
	${libdir}/tmpfiles.d/samba.conf \
	${sbindir}/samba_kcc \
	${datadir}/samba/addshare.py \
	${datadir}/samba/setoption.py \
	${localstatedir}/cache/samba"
FILES_${PN}-common = "\
	${sysconfdir}/dhcp/dhclient-enter-hooks.d/samba \
	${sysconfdir}/pam.d/samba \
	${sysconfdir}/samba/gdbcommands \
	${datadir}/samba/codepages/*.dat \
	${datadir}/samba/panic-action \
	${datadir}/samba/smb.conf \
	${localstatedir}/cache/samba \
	${localstatedir}/log/samba \
	${localstatedir}/lib/samba/private \
	${localstatedir}/run \
	/run"
FILES_python-samba = "\
	${libdir}/${PYTHON_DIR}/*"
FILES_winbind = "\
	${bindir}/ntlm_auth \
	${bindir}/wbinfo \
	${libdir}/plugin/krb5/winbind_krb5_locator.so \
	${libdir}/samba/idmap/*.so \
	${libdir}/samba/libidmap${SOLIBS} \
	${sbindir}/winbindd \
	${libdir}/samba/nss_info/*.so \
	${sysconfdir}/default/winbind \
	${sysconfdir}/init.d/winbind \
	${sysconfdir}/init/winbind.conf \
	${sysconfdir}/logrotate.d/winbind"
FILES_${PN}-libs = "\
	${libdir}/*${SOLIBS} \
	${libdir}/samba/libgssapi*${SOLIBS} \
	${libdir}/samba/libhcrypto*${SOLIBS} \
	${libdir}/samba/libheimbase*${SOLIBS} \
	${libdir}/samba/libheimntlm*${SOLIBS} \
	${libdir}/samba/libhx509*${SOLIBS} \
	${libdir}/samba/libroken*${SOLIBS} \
	${libdir}/samba/libwind*${SOLIBS} \
	${libdir}/samba/libad*${SOLIBS} \
	${libdir}/samba/libasn1*${SOLIBS} \
	${libdir}/samba/libauth*${SOLIBS} \
	${libdir}/samba/libCHARSET3${SOLIBS} \
	${libdir}/samba/libcli*${SOLIBS} \
	${libdir}/samba/libcluster${SOLIBS} \
	${libdir}/samba/libcmdline-credentials${SOLIBS} \
	${libdir}/samba/libcom_err-samba4* \
	${libdir}/samba/libdbwrap${SOLIBS} \
	${libdir}/samba/libdcerpc-*${SOLIBS} \
	${libdir}/samba/libdfs-server-ad${SOLIBS} \
	${libdir}/samba/libdnsserver-common${SOLIBS} \
	${libdir}/samba/libdsdb-module${SOLIBS} \
	${libdir}/samba/liberrors${SOLIBS} \
	${libdir}/samba/libevents${SOLIBS} \
	${libdir}/samba/libflag-mapping${SOLIBS} \
	${libdir}/samba/libgpo${SOLIBS} \
	${libdir}/samba/libgse${SOLIBS} \
	${libdir}/samba/libhttp${SOLIBS} \
	${libdir}/samba/libinterfaces${SOLIBS} \
	${libdir}/samba/libkrb5*${SOLIBS} \
	${libdir}/samba/libldbsamba${SOLIBS} \
	${libdir}/samba/liblibcli-*${SOLIBS} \
	${libdir}/samba/liblibsmb${SOLIBS} \
	${libdir}/samba/libLIBWBCLIENT-OLD${SOLIBS} \
	${libdir}/samba/libMESSAGING${SOLIBS} \
	${libdir}/samba/libmsrpc3${SOLIBS} \
	${libdir}/samba/libndr*${SOLIBS} \
	${libdir}/samba/libnetif${SOLIBS} \
	${libdir}/samba/libnet-keytab${SOLIBS} \
	${libdir}/samba/libnon-posix-acls${SOLIBS} \
	${libdir}/samba/libnpa-tstream${SOLIBS} \
	${libdir}/samba/libnss-info${SOLIBS} \
	${libdir}/samba/libntvfs${SOLIBS} \
	${libdir}/samba/libpopt-samba3${SOLIBS} \
	${libdir}/samba/libposix-eadb${SOLIBS} \
	${libdir}/samba/libprinting-migrate${SOLIBS} \
	${libdir}/samba/libprocess-model${SOLIBS} \
	${libdir}/samba/libreplace${SOLIBS} \
	${libdir}/samba/libsamba*${SOLIBS} \
	${libdir}/samba/libsamdb-common${SOLIBS} \
	${libdir}/samba/libsecrets3${SOLIBS} \
	${libdir}/samba/libserver-role${SOLIBS} \
	${libdir}/samba/libservice${SOLIBS} \
	${libdir}/samba/libshares${SOLIBS} \
	${libdir}/samba/libsmb*${SOLIBS} \
	${libdir}/samba/libsocket-blocking${SOLIBS} \
	${libdir}/samba/libsubunit${SOLIBS} \
	${libdir}/samba/libtdb*${SOLIBS} \
	${libdir}/samba/libtrusts-util${SOLIBS} \
	${libdir}/samba/libutil*${SOLIBS} \
	${libdir}/samba/libutil-tdb${SOLIBS} \
	${libdir}/samba/libxattr-tdb${SOLIBS} \
	${libdir}/samba/auth/*.so \
	${libdir}/samba/bind9/*.so \
	${libdir}/samba/gensec/krb5.so \
	${libdir}/samba/process_model/*.so"
FILES_${PN} += "\
	${systemd_system_unitdir}/samba.service \
	${libdir}/${PYTHON_DIR}/*-packages/samba/dckeytab.so"

DEBIAN_NOAUTONAME_libsmbclient = "1"
DEBIANNAME_libwbclient = "libwbclient0"
RPROVIDES_libwbclient += "libwbclient0"

RDEPENDS_${PN} += "libpam-modules procps python python-ntdb python-samba lsb-base \
                   ${PN}-common ${PN}-common-bin ${PN}-dsdb-modules tdb-tools"
RDEPENDS_python-samba += "python-tdb python-ntdb python-ldb"
RDEPENDS_${PN}-common-bin += "${PN}-common python-samba"
RDEPENDS_smbclient += "${PN}-common"
RPROVIDES_smbclient += "samba-client"
RDEPENDS_${PN}-testsuite += "${PN}-common-bin"
RDEPENDS_libpam-smbpass += "${PN}-common libpam-runtime"
RDEPENDS_winbind += "${PN}"
RDEPENDS_libpam-winbind += "libpam-runtime ${PN}-common winbind"
RDEPENDS_libnss-winbind += "${PN}-common winbind"
