include mysql.inc

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
