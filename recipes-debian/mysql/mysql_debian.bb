SUMMARY = "MySQL database"
DESCRIPTION = "\
MySQL is a fast, stable and true multi-user, multi-threaded SQL database \
server. SQL (Structured Query Language) is the most popular database query \
language in the world. The main goals of MySQL are speed, robustness and \
ease of use \
"
HOMEPAGE = "http://dev.mysql.com/"
PR = "r1"
inherit debian-package

LICENSE = "GPLv2+ & BSD-4-Clause & BSD & Zlib & PD & ISC"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=751419260aa954499f7abaabaa882bbe \
	file://cmd-line-utils/libedit/eln.c;beginline=3;endline=34;md5=4bb47d8f3baf56e0e5895920250769d5 \
	file://cmd-line-utils/libedit/chared.c;beginline=4;endline=33;md5=c9d1d3d967ea0ac8eb7ebf0078c97f77 \
	file://storage/archive/azlib.h;beginline=6;endline=34;md5=7c27ae0384929249664da410d539a1dc \
	file://scripts/mysqld_safe.sh;beginline=2;endline=12;md5=03abbf638cce91047edbd7e3f62b8382 \
	file://cmd-line-utils/libedit/np/strlcat.c;beginline=4;endline=18;md5=0842004924db787e8550a43e205891a7 \
	"
inherit cmake
VER = "5.5"
PV = "5.5.53"
DPN = "mysql-${VER}"

EXTRA_OECMAKE += " \
        -DSTACK_DIRECTION=-1 -DCAT_EXECUTABLE=`which cat` -DAWK_EXECUTABLE=`which awk` \
        -DCMAKE_INSTALL_PREFIX=${prefix} \
        -DCMAKE_VERBOSE_MAKEFILE=ON \
        -DMYSQL_UNIX_ADDR=${localstatedir}/run/mysqld/mysqld.sock \
        -DMYSQL_USER=mysql \
        -DCMAKE_BUILD_TYPE=RelWithDebInfo \
        -DWITH_LIBWRAP=ON \
        -DWITH_READLINE=OFF \
        -DWITH_LIBEDIT=OFF \
        -DLINUX_NATIVE_AIO=ON \                
        -DWITH_SSL=bundled \
        -DWITH_ZLIB=system \
        -DINSTALL_LAYOUT=RPM \
        -DINSTALL_LIBDIR=lib \
        -DINSTALL_PLUGINDIR=lib/mysql/plugin \
        -DWITH_EMBEDDED_SERVER=ON \
        -DHAVE_EMBEDDED_PRIVILEGE_CONTROL=ON \
        -DWITH_ARCHIVE_STORAGE_ENGINE=ON \
        -DWITH_BLACKHOLE_STORAGE_ENGINE=ON \
        -DWITH_FEDERATED_STORAGE_ENGINE=ON \
        -DWITH_EXTRA_CHARSETS=all \
"
DEPENDS += "libaio libbsd mysql-native readline"
PACKAGES =+ "\
        libmysqlclient libmysqld-pic mysql-common \
        mysql-server mysql-server-core"

do_compile_prepend () {
	# These need to be in-tree or make will think they need to be built,
	# and since we're cross-compiling that is disabled
	cp ${STAGING_BINDIR_NATIVE}/comp_err ${S}/extra
	cp ${STAGING_BINDIR_NATIVE}/comp_sql ${S}/scripts
}

do_install_append() {
	install -D -m 0644 ${S}/debian/apparmor-profile \
		${D}${sysconfdir}/apparmor.d/usr.sbin.mysqld
	for file in paranoid server workstation;do
		install -D -m 0644 ${S}/debian/*.logcheck.ignore.$file \
			${D}${sysconfdir}/logcheck/ignore.d.$file/mysql-server-5_5
	done
	install -D -m 0644 ${S}/debian/*.mysql-server.logrotate \
		${D}${sysconfdir}/logrotate.d/mysql-server
	install -D -m 0644 ${S}/debian/additions/my.cnf \
		${D}${sysconfdir}/mysql/my.cnf
	install -D -m 0755 ${S}/debian/additions/debian-start \
		${D}${sysconfdir}/mysql/debian-start
	install -D -m 0644 ${S}/debian/additions/mysqld_safe_syslog.cnf \
		${D}${sysconfdir}/mysql/conf.d/mysqld_safe_syslog.cnf
	install -m 0755 ${S}/debian/additions/innotop/innotop \
		${D}${bindir}/innotop
	install -D -m 0755 ${S}/debian/mysql-server-${VER}.mysql.init \
		${D}${sysconfdir}/init.d/mysql

	install -m 0755 -o root -g root ${B}/scripts/mysql_config \
		${D}${bindir}/mysql_config_pic
	install -m 0755 ${S}/debian/additions/mysqlreport \
		${D}${bindir}

	ln -s mysqlcheck ${D}${bindir}/mysqlanalyze
	ln -s mysqlcheck ${D}${bindir}/mysqloptimize
	ln -s mysqlcheck ${D}${bindir}/mysqlrepair
	install -d ${D}${libdir}/mysql-testsuite
	mv ${D}${datadir}/mysql-test/* ${D}${libdir}/mysql-testsuite
	rm -r ${D}${datadir}/mysql-test
	#remove unwanted files
	rm ${D}${bindir}/mysqlaccess.conf ${D}${bindir}/mysql_embedded \
		${D}${bindir}/mysql_client_test_embedded \
		${D}${bindir}/mysqltest_embedded
}

PACKAGES += "mysql-testsuite"
FILES_libmysqlclient = "${libdir}/libmysqlclient.so.* ${libdir}/libmysqlclient_r.so.*"
FILES_libmysqld-pic = "${bindir}/mysql_config_pic"
FILES_mysql-common = "${sysconfdir}/${PN}/my.cnf"
FILES_mysql-server = "\
	${sysconfdir}/* ${bindir}/msql2mysql ${bindir}/myisamchk \
	${bindir}/myisamlog ${bindir}/myisampack \
	${bindir}/mysql_convert_table_format ${bindir}/resolveip \
	${bindir}/mysql_secure_installation ${bindir}/mysql_setpermission \
	${bindir}/mysql_tzinfo_to_sql ${bindir}/mysql_zap ${bindir}/mysqlbinlog \
	${bindir}/mysqld_* ${bindir}/mysqlhotcopy ${bindir}/mysqltest \
	${bindir}/perror ${bindir}/replace ${bindir}/resolve_stack_dump \
	${libdir}/mysql/plugin/*"
FILES_mysql-server-core = "\
	${bindir}/my_print_defaults ${bindir}/mysql_install_db \
	${bindir}/mysql_upgrade ${sbindir}/mysqld ${datadir}/mysql/*"
FILES_mysql-testsuite = "${libdir}/mysql-testsuite/*"
FILES_${PN}-dbg += "\
	${libdir}/mysql-testsuite/lib/My/SafeProcess/.debug \
	${libdir}/mysql/plugin/.debug \
"
FILES_${PN}-dev += "${bindir}/mysql_config"
PKG_${PN}-dev = "lib${PN}client-dev"
PKG_${PN} = "mysql-client-${VER}"
PKG_mysql-server = "mysql-server-${VER}"
PKG_mysql-server-core = "mysql-server-core-${VER}"
PKG_mysql-testsuite = "mysql-testsuite-${VER}"

RDEPENDS_libmysqlclient += "mysql-common"
RDEPENDS_${PN} += "\
	mysql-common debianutils "
RDEPENDS_mysql-server += "\
	passwd psmisc mysql-server-core ${PN} sysvinit-initscripts lsb-base"

inherit binconfig

BINCONFIG_GLOB = "mysql_config"
SYSROOT_PREPROCESS_FUNCS += "mysql_sysroot_preprocess"
mysql_sysroot_preprocess() {
	sed -i -e "s:='${libdir}:='${STAGING_LIBDIR}:g" \
	       -e "s:='${includedir}:='${STAGING_INCDIR}:g" \
	       ${SYSROOT_DESTDIR}${bindir_crossscripts}/mysql_config
}
