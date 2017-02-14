#
# base recipe: meta/recipes-support/apr/apr-util_1.5.2.bb
# base branch: daisy
#

SUMMARY = "Apache Portable Runtime Utility Library"
DESCRIPTION = "APR is Apache's Portable Runtime Library, designed to be a support library \
that provides a predictable and consistent interface to underlying \
platform-specific implementations."
HOMEPAGE = "http://apr.apache.org/"

PR = "r2"

inherit debian-package
PV = "1.5.4"

DEPENDS = "apr expat autotools-dev-native"

BBCLASSEXTEND = "native"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=519e0a18e03f7c023070568c14b077bb \
                    file://include/apu_version.h;endline=17;md5=806685a84e71f10c80144c48eb35df42"

SRC_URI += " \
file://configfix.patch \
file://configure_fixes.patch \
file://run-ptest \
"

# base on debian/rules
EXTRA_OECONF = " \
    LTFLAGS=--no-silent \
    --with-apr=${STAGING_BINDIR_CROSS}/apr-1-config \
    --enable-layout=Debian \
    --includedir=${includedir}/apr-1.0 \
    --with-expat=${STAGING_DIR_HOST}${prefix} \
"
CACHED_CONFIGUREVARS += "ac_cv_prog_AWK=mawk"
CACHED_CONFIGUREVARS += " \
    ${@base_conditional('DPKG_ARCH','i386',\
      'apr_lock_method=USE_PROC_PTHREAD_SERIALIZE',\
      'ac_cv_func_pthread_mutexattr_setpshared=no \
       ac_cv_func_sem_open=no'\
      ,d)}"

PACKAGECONFIG ??= "db53 ldap mysql odbc openssl pgsql sqlite3"
PACKAGECONFIG[db53] = " \
    --with-dbm=db53 --with-berkeley-db=${STAGING_DIR_HOST}${prefix}, \
    --without-berkeley-db,db"
PACKAGECONFIG[gdbm] = " \
    --with-dbm=gdbm --with-gdbm=${STAGING_DIR_HOST}${prefix}, \
    --without-gdbm,gdbm"
PACKAGECONFIG[ldap] = " \
    --with-ldap=yes --with-ldap-include=${STAGING_INCDIR}/ --with-ldap-lib=${STAGING_LIBDIR}, \
    --with-ldap=no,libldap openldap"
PACKAGECONFIG[mysql] = " \
    --with-mysql=${STAGING_DIR_HOST}${prefix} MYSQL_CONFIG=${STAGING_BINDIR_CROSS}/mysql_config, \
    --without-mysql,mysql"
PACKAGECONFIG[odbc] = "--with-odbc=${STAGING_DIR_HOST}${prefix},--without-odbc,unixodbc"
PACKAGECONFIG[openssl] = " \
    --with-crypto --with-openssl=${STAGING_DIR_HOST}${prefix}, \
    --without-openssl,openssl"
PACKAGECONFIG[pgsql] = "--with-pgsql=${STAGING_DIR_HOST}${prefix},--without-pgsql,postgresql"
PACKAGECONFIG[sqlite3] = "--with-sqlite3=${STAGING_DIR_HOST}${prefix},--without-sqlite3,sqlite3"
PACKAGECONFIG[sqlite2] = "--with-sqlite2=${STAGING_DIR_HOST}${prefix},--without-sqlite2,sqlite"
PACKAGECONFIG[freetds] = "--with-freetds=${STAGING_DIR_HOST}${prefix},--without-freetds,freetds"

inherit autotools-brokensep lib_package binconfig

OE_BINCONFIG_EXTRA_MANGLE = " -e 's:location=source:location=installed:'"

do_configure_prepend() {
	# use config.guess and config.sub in sysroot
	sed -i "s|\([[:space:]]\)/usr/share|\1${STAGING_DATADIR_NATIVE}|g" ${S}/buildconf
	./buildconf --with-apr=$(apr-1-config --srcdir)
}

do_configure_append_class-native() {
	sed -i "s#LIBTOOL=\$(SHELL) \$(apr_builddir)#LIBTOOL=\$(SHELL) ${STAGING_BINDIR_NATIVE}#" ${S}/build/rules.mk
	# sometimes there isn't SHELL
	sed -i "s#LIBTOOL=\$(apr_builddir)#LIBTOOL=${STAGING_BINDIR_NATIVE}#" ${S}/build/rules.mk
}

do_install_append() {
	install -D -m 0644 ${S}/build/find_apu.m4 ${D}${datadir}/apr-1.0/build/find_apu.m4
	ln -sf apu-1-config ${D}${bindir}/apu-config
}

# apu-config is a symlink, only apu-1-config should be modified
BINCONFIG_GLOB = "apu-1-config"

PACKAGES =+ "libaprutil1-ldap libaprutil1-dbd-mysql libaprutil1-dbd-sqlite3 \
             libaprutil1-dbd-odbc libaprutil1-dbd-pgsql"

FILES_libaprutil1-ldap = "${libdir}/apr-util-1/apr_ldap-1.so"
FILES_libaprutil1-dbd-mysql = "${libdir}/apr-util-1/apr_dbd_mysql-1.so"
FILES_libaprutil1-dbd-sqlite3 = "${libdir}/apr-util-1/apr_dbd_sqlite3-1.so"
FILES_libaprutil1-dbd-odbc = "${libdir}/apr-util-1/apr_dbd_odbc-1.so"
FILES_libaprutil1-dbd-pgsql = "${libdir}/apr-util-1/apr_dbd_pgsql-1.so"

FILES_${PN} += " \
    ${libdir}/apr-util-1/apr_dbm_db-1.so \
    ${libdir}/apr-util-1/apr_crypto_openssl-1.so \
"
FILES_${PN}-dev += " \
    ${libdir}/aprutil.exp \
    ${libdir}/apr-util-1/*.a \
    ${libdir}/apr-util-1/*.la \
    ${libdir}/apr-util-1/*[!-1].so \
    ${datadir}/apr-1.0/build/* \
"
FILES_${PN}-dbg += "${libdir}/apr-util-1/.debug/*"
FILES_${PN}-staticdev += "${libdir}/apr-util-1/*.a"

RDEPENDS_libaprutil1-ldap += "${PN}"
RDEPENDS_libaprutil1-dbd-mysql += "${PN}"
RDEPENDS_libaprutil1-dbd-sqlite3 += "${PN}"
RDEPENDS_libaprutil1-dbd-odbc += "${PN}"
RDEPENDS_libaprutil1-dbd-pgsql += "${PN}"

DEBIANNAME_${PN} = "libaprutil1"
DEBIANNAME_${PN}-dev = "libaprutil1-dev"

RPROVIDES_${PN} += "libaprutil1"
RPROVIDES_${PN}-dev += "libaprutil1-dev"

inherit ptest

do_compile_ptest() {
	cd ${S}/test
	oe_runmake
}

do_install_ptest() {
	t=${D}${PTEST_PATH}/test
	mkdir $t
	for i in testall data; do \
		cp -r ${S}/test/$i $t; \
	done
}
