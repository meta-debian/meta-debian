DESCRIPTION = "The Simple Network Management Protocol (SNMP) provides a framework \
for the exchange of management information between agents (servers) \
and clients."
HOMEPAGE = "http://net-snmp.sourceforge.net/"

PR = "r3"

inherit debian-package
PV = "5.7.2.1+dfsg"

LICENSE = "BSD"
LIC_FILES_CHKSUM = " \
    file://README;beginline=3;endline=8;md5=7f7f00ba639ac8e8deb5a622ea24634e \
    file://COPYING;md5=1d364fa6e8a1b6d7e3f1594f36c18cc9 \
"

DEPENDS = "lm-sensors python-distribute-native"

# man-makefile-parallel.diff:
# 	Fix dependencies for auto-generated man pages.
# 	https://sourceforge.net/p/net-snmp/bugs/2540/
# correct-extramibs-filename.patch:
# 	Correct mibs filename in mibs/Makefile.in
SRC_URI += " \
    file://man-makefile-parallel.diff \
    file://correct-extramibs-filename.patch \
"

inherit autotools-brokensep cpan-base perlnative pythonnative distutils-base useradd

# Follow debian/snmpd.postinst
SNMPDIR = "${localstatedir}/lib/snmp"
USERADD_PACKAGES = "snmpd"
USERADD_PARAM_snmpd = "--system --user-group --home-dir ${SNMPDIR} \
                       --shell /usr/sbin/nologin snmp"

TARGET_CC_ARCH += "${LDFLAGS}"

PARALLEL_MAKE = ""
CCACHE = ""

# Configure follow debian/rules
OLD_MIBS_DIR ?="${datadir}/mibs/site:${datadir}/snmp/mibs:${datadir}/mibs/iana:${datadir}/mibs/ietf:${datadir}/mibs/netsnmp"
MIBS_DIR ?="${datadir}/snmp/mibs:${datadir}/snmp/mibs/iana:${datadir}/snmp/mibs/ietf"
MIB_MODULES ?= "smux ucd-snmp/dlmod mibII/mta_sendmail disman/event-mib \
                ucd-snmp/diskio ucd-snmp/lmsensorsMib etherlike-mib/dot3StatsTable"
EXCL_MIB_MODULES ?= ""

EXTRA_OECONF = " \
    --with-persistent-directory=${SNMPDIR} \
    --enable-ucd-snmp-compatibility \
    --enable-shared --with-cflags='${CFLAGS} -DNETSNMP_USE_INLINE' \
    --with-ldflags='${LDFLAGS}' \
    --with-perl-modules='INSTALLDIRS=vendor' --enable-as-needed \
    --enable-ipv6 --with-logfile=none --without-rpm \
    --without-dmalloc --without-efence --without-rsaref \
    --with-sys-contact='root' --with-sys-location='Unknown' \
    --with-mib-modules='${MIB_MODULES}' \
    --with-out-mib-modules='${EXCL_MIB_MODULES}' \
    --enable-mfd-rewrites \
    --with-mnttab=${sysconfdir}/mtab \
    --with-mibdirs='\$HOME/.snmp/mibs:${MIBS_DIR}:${OLD_MIBS_DIR}' \
    --with-defaults \
"
PACKAGECONFIG ?= "openssl libwrap mysql"
PACKAGECONFIG[libwrap] = "--with-libwrap=${STAGING_LIBDIR}/..,--without-libwrap,tcp-wrappers"
PACKAGECONFIG[openssl] = "--with-openssl=${STAGING_LIBDIR}/..,--without-openssl,openssl"
PACKAGECONFIG[mysql] = "--with-mysql,--without-mysql,mysql"

# Env var which tells perl if it should use host (no) or target (yes) settings
export PERLCONFIGTARGET = "${@is_target(d)}"

# Env var which tells perl where the perl include files are
export PERL_INC = "${STAGING_LIBDIR}${PERL_OWN_DIR}/perl/${@get_perl_version(d)}/CORE"
export PERL_LIB = "${STAGING_LIBDIR}${PERL_OWN_DIR}/perl/${@get_perl_version(d)}"
export PERL_ARCHLIB = "${STAGING_LIBDIR}${PERL_OWN_DIR}/perl/${@get_perl_version(d)}"
export PERLHOSTLIB = "${STAGING_LIBDIR_NATIVE}/perl-native/perl/${@get_perl_version(d)}/"

# Correct build flags for perl
do_configure_prepend() {
	sed -i -e "s:\(perlcflags=\).*:\1-I${PERL_INC}:g" \
	       -e "s:\(netsnmp_perlldopts=\).*:\1-L${PERL_INC}:g" \
	       -e "s:\(^\s*PERLCC=.*-=\\\w\\\s\):\1\\\.:g" \
	       ${S}/configure.d/config_project_perl_python
}

do_configure_append() {
	# Backup net-snmp-config
	cp ${S}/net-snmp-config ${S}/net-snmp-config.bak

	# Remove host path
	sed -i -e "s@-I/usr/include@@g" \
	       -e "s@^prefix=.*@prefix=${STAGING_DIR_HOST}@g" \
	       -e "s@^exec_prefix=.*@exec_prefix=${STAGING_DIR_HOST}@g" \
	       -e "s@^includedir=.*@includedir=${STAGING_INCDIR}@g" \
	       -e "s@^libdir=.*@libdir=${STAGING_LIBDIR}@g" \
	       ${S}/net-snmp-config
}

DISTUTILS_BUILD_ARGS = "--basedir=${S}"
DISTUTILS_INSTALL_ARGS = " \
    --root=${D} \
    --prefix=${prefix} \
    --install-lib=${PYTHON_SITEPACKAGES_DIR} \
    --install-data=${datadir} \
    --basedir=${S} \
"
export LDSHARED="${CCLD} -shared"
export HOST_SYS
export BUILD_SYS

do_compile_append() {
	olddir=`pwd`

	# Build python modules
	cd ${S}/python
	STAGING_INCDIR=${STAGING_INCDIR} \
	STAGING_LIBDIR=${STAGING_LIBDIR} \
	BUILD_SYS=${BUILD_SYS} HOST_SYS=${HOST_SYS} \
	${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} setup.py build ${DISTUTILS_BUILD_ARGS}

	cd $olddir
}

do_install_append() {
	olddir=`pwd`
	cd ${S}/python

	# Install python modules
	install -d ${D}${PYTHON_SITEPACKAGES_DIR}
	STAGING_INCDIR=${STAGING_INCDIR} \
		STAGING_LIBDIR=${STAGING_LIBDIR} \
		PYTHONPATH=${D}${PYTHON_SITEPACKAGES_DIR} \
		BUILD_SYS=${BUILD_SYS} HOST_SYS=${HOST_SYS} \
		${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} setup.py install ${DISTUTILS_INSTALL_ARGS}

	find ${D} -name *.pyc -delete
	cd $olddir

	# Install unmodified net-snmp-config
	install -m 0755 ${S}/net-snmp-config.bak ${D}${bindir}/net-snmp-config

	# Follow debian/libsnmp-base.dirs
	install -d ${D}${localstatedir}/lib/snmp
	# Follow debian/libsnmp-base.install
	cp ${S}/mibs/*.txt ${D}${datadir}/snmp/mibs

	# Follow debian/libsnmp-dev.dirs
	install -d ${D}${sysconfdir}/snmp
	# Follow debian/libsnmp-dev.install
	cp ${S}/local/mib2c*.conf               ${D}${sysconfdir}/snmp/
	cp ${S}/agent/mibgroup/struct.h         ${D}${includedir}/net-snmp/agent/
	cp ${S}/agent/mibgroup/util_funcs.h     ${D}${includedir}/net-snmp/
	cp ${S}/agent/mibgroup/mibincl.h        ${D}${includedir}/net-snmp/library/
	cp ${S}/agent/mibgroup/header_complex.h ${D}${includedir}/net-snmp/agent/

	# Install conf and init files
	install -D -m 0644 ${S}/debian/*.conf ${D}${sysconfdir}/snmp/
	install -D -m 0644 ${S}/debian/snmpd.default ${D}${sysconfdir}/default/snmpd
	install -D -m 0644 ${S}/debian/snmptrapd.default ${D}${sysconfdir}/default/snmptrapd
	install -D -m 0755 ${S}/debian/snmpd.init ${D}${sysconfdir}/init.d/snmpd
	install -D -m 0755 ${S}/debian/snmptrapd.init ${D}${sysconfdir}/init.d/snmptrapd

	# Follow debian/rules
	chmod -x ${D}${sysconfdir}/snmp/*.conf
	install -m 0600 ${S}/EXAMPLE.conf ${D}${sysconfdir}/snmp/snmpd.conf

	rm -f ${D}${datadir}/snmp/mib2c.*
}

SYSROOT_PREPROCESS_FUNCS += "net_snmp_sysroot_preprocess"

net_snmp_sysroot_preprocess () {
	if [ -e ${S}/net-snmp-config ]; then
		install -d ${SYSROOT_DESTDIR}${bindir_crossscripts}/
		install -m 755 ${S}/net-snmp-config ${SYSROOT_DESTDIR}${bindir_crossscripts}/
	fi
}

PACKAGES = "${PN}-dbg ${PN}-staticdev ${PN}-dev ${PN}-doc \
            libsnmp libsnmp-base libsnmp-perl python-netsnmp \
            snmpd snmptrapd tkmib ${PN}"

FILES_libsnmp = "${libdir}/lib*${SOLIBS}"
FILES_libsnmp-base = "${datadir}/snmp/mib2c-data \
                      ${datadir}/snmp/mibs \
                      "
FILES_libsnmp-perl = "${PERLLIBDIRS}/*"
FILES_python-netsnmp = "${PYTHON_SITEPACKAGES_DIR}/netsnmp*/*"
FILES_snmpd = "${sysconfdir}/default/snmpd \
               ${sysconfdir}/init.d/snmpd \
               ${sysconfdir}/snmp/snmpd.conf \
               ${sbindir}/snmpd \
               ${datadir}/snmp/snmpconf-data/snmpd-data \
               "
FILES_snmptrapd = "${sysconfdir}/default/snmptrapd \
                   ${sysconfdir}/init.d/snmptrapd \
                   ${sysconfdir}/snmp/snmptrapd.conf \
                   ${bindir}/traptoemail \
                   ${sbindir}/snmptrapd \
                   ${datadir}/snmp/snmp_perl_trapd.pl \
                   ${datadir}/snmp/snmpconf-data/snmptrapd-data \
                   "
FILES_tkmib = "${bindir}/tkmib"
FILES_${PN} += "${datadir}/snmp/snmpconf-data/snmp-data"
FILES_${PN}-dev += "${bindir}/mib2c* \
                    ${bindir}/net-snmp-config \
                    ${bindir}/net-snmp-create-v3-user \
                    ${sysconfdir}/snmp/mib2c.* \
                    "

PKG_libsnmp = "libsnmp30"

PKG_${PN} = "snmp"
RPROVIDES_${PN} += "snmp"
PKG_${PN}-dev = "libsnmp-dev"
RPROVIDES_${PN}-dev += "libsnmp-dev"

RDEPENDS_libsnmp += "libsnmp-base"
RDEPENDS_snmpd += "lsb-base libsnmp-base"
RDEPENDS_snmptrapd += "snmpd"
RDEPENDS_tkmib += "libsnmp-perl"
RDEPENDS_${PN} += "libsnmp-base"
