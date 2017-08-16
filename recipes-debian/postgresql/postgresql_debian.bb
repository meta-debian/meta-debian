#
# No base recipe
#

SUMMARY = "PostgreSQL is a powerful, open source relational database system."
DESCRIPTION = "\
    PostgreSQL is an advanced Object-Relational database management system \
    (DBMS) that supports almost all SQL constructs (including \
    transactions, subselects and user-defined types and functions). The \
    postgresql package includes the client programs and libraries that \
    you'll need to access a PostgreSQL DBMS server.  These PostgreSQL \
    client programs are programs that directly manipulate the internal \
    structure of PostgreSQL databases on a PostgreSQL server. These client \
    programs can be located on the same machine with the PostgreSQL \
    server, or may be on a remote machine which accesses a PostgreSQL \
    server over a network connection. This package contains the docs \
    in HTML for the whole package, as well as command-line utilities for \
    managing PostgreSQL databases on a PostgreSQL server. \
    \
    If you want to manipulate a PostgreSQL database on a local or remote \
    PostgreSQL server, you need this package. You also need to install \
    this package if you're installing the postgresql-server package. \
"
HOMEPAGE = "http://www.postgresql.com"

inherit debian-package
PV = "9.4.13"

DPN = "postgresql-9.4"

LICENSE = "PostgreSQL"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=81b69ddb31a8be66baafd14a90146ee2"

#fix error looked at host include and/or library paths: remove -L/usr/local/lib
SRC_URI += "file://fix-using-host-library_debian.patch"

DEPENDS_class-target = "\
	zlib readline krb5 tcl libxml2 libxslt python3 python perl"
DEPENDS_class-native = "\
	tcl-native libxml2-native libxslt-native python-native python3-native perl-native"
inherit autotools-brokensep pkgconfig perlnative pythonnative systemd cpan-base

export BUILD_SYS
export HOST_SYS
export STAGING_INCDIR
export STAGING_LIBDIR

MAJOR_VER = "9.4"
TCL_VER = "8.6"

# --without-selinux: Don't use selinux support
COMMON_CONFIGURE_FLAGS = " \
	--mandir=${datadir}/${PN}/${MAJOR_VER}/man \
	--docdir=${datadir}/doc/${PN}-doc-${MAJOR_VER} \
	--sysconfdir=${sysconfdir}/${PN}-common \
	--datarootdir=${datadir} \
	--datadir=${datadir}/${PN}/${MAJOR_VER} \
	--bindir=${libdir}/${PN}/${MAJOR_VER}/${base_bindir} \
	--libdir=${libdir} \
	--libexecdir=${libdir}/${PN}/ \
	--includedir=${includedir}/${PN} \
	--enable-nls \
	--enable-integer-datetimes \
	--enable-thread-safety \
	--enable-debug \
	--disable-rpath \
	--with-uuid=e2fs \
	--with-gnu-ld \
	--with-pgport=5432 \
	--with-system-tzdata=${STAGING_DATADIR}/zoneinfo \
	--without-selinux \
"

EXTRA_OECONF_class-target = " \
    --with-tclconfig=${STAGING_BINDIR_CROSS} \
    --with-includes=${STAGING_INCDIR}/tcl${TCL_VER} \
"
EXTRA_OECONF_class-native = " \
    --with-tclconfig=${STAGING_LIBDIR_NATIVE}/tcl${TCL_VER} \
    --with-includes=${STAGING_INCDIR_NATIVE}/tcl${TCL_VER} \
"

EXTRA_OECONF_append = " \
	--with-tcl --with-openssl --with-perl \
	--with-python --with-libxml --with-libxslt \
	${COMMON_CONFIGURE_FLAGS} \
	"
PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'pam', '', d)}"
PACKAGECONFIG[pam] = "--with-pam,--without-pam,libpam"

PARALLEL_MAKE = ""
LDFLAGS =+ " -L${STAGING_LIBDIR}${PERL_OWN_DIR}/perl/${@get_perl_version(d)}/CORE "
do_configure_append() {
	test -d build_py3 || mkdir build_py3
	cd build_py3
	../configure --host=${HOST_SYS} \
		--build=${BUILD_SYS} \
		--target=${TARGET_SYS} \
		--with-python \
		PYTHON=${STAGING_BINDIR_NATIVE}/python3-native/python3 \
		${COMMON_CONFIGURE_FLAGS}
	cd ${S}
}
#
# Fix error the install log indicates that host include and/or library paths were used
#
do_compile_prepend() {
	sed -i -e "s:-L/usr/lib:-L${STAGING_LIBDIR}:g" ${S}/src/Makefile.global
	sed -i -e "s:-L/usr/include:-L${STAGING_INCDIR}:g" ${S}/src/Makefile.global
}

do_compile_append() {
	oe_runmake -C ${S}/contrib
	cd build_py3
	cp ${S}/src/pl/plpython/*.o ${S}/build_py3/src/pl/plpython
	oe_runmake -C src/backend/ submake-errcodes
	oe_runmake -C src/pl/plpython
}
do_install_append() {
	# Follow Deian, some files belong to /usr/bin
	install -d ${D}${bindir}
	cp ${D}${libdir}/${PN}/${MAJOR_VER}${base_bindir}/pg_config ${D}${bindir}
	mv ${D}${libdir}/${PN}/${MAJOR_VER}${base_bindir}/ecpg ${D}${bindir}
	oe_runmake -C ${S}/contrib install DESTDIR=${D}
	install -m 0644 ${S}/src/pl/plpython/plpython3u* \
		${D}${datadir}/${PN}/${MAJOR_VER}/extension/
	install -m 0755 ${S}/build_py3/src/pl/plpython/plpython3.so \
		${D}${libdir}/${PN}/${MAJOR_VER}/lib

	# Remove the the absolute path to sysroot
	sed -i -e "s|${STAGING_LIBDIR}|${libdir}|" \
		${D}${libdir}/pkgconfig/*.pc
}

PACKAGES =+ "libecpg-compat libecpg-dev libecpg libpgtypes libpq-dev libpq \
             ${PN}-client-${MAJOR_VER} ${PN}-pltcl-${MAJOR_VER} \
             ${PN}-plperl-${MAJOR_VER} \
	     ${PN}-plpython3-${MAJOR_VER} ${PN}-plpython-${MAJOR_VER} \
             ${PN}-server-dev-${MAJOR_VER} ${PN}-contrib-${MAJOR_VER}"

FILES_libecpg-compat += " \
                ${libdir}/libecpg_compat.so.*"
FILES_libecpg-dev += " \
                ${bindir}/ecpg ${libdir}/libecpg*.so \
                ${libdir}/libpgtypes.so \
                ${libdir}/pkgconfig/libpgtypes.pc \
                ${libdir}/pkgconfig/libecpg*.pc \
                ${includedir}/${PN}/sqlda*.h \
                ${includedir}/${PN}/sql3types.h \
                ${includedir}/${PN}/pgtypes*.h \
                ${includedir}/${PN}/ecpg*.h \
                ${includedir}/${PN}/sqlca.h \
                ${includedir}/${PN}/informix \
                "
FILES_libecpg += "${libdir}/libecpg.so.*"
FILES_libpgtypes += "${libdir}/libpgtypes.so.*"
FILES_libpq-dev += " \
                ${bindir}/pg_config \
                ${libdir}/pkgconfig/libpq.pc \
                ${includedir}/${PN}/pg_config*.h \
                ${includedir}/${PN}/postgres_ext.h \
                ${includedir}/${PN}/libpq/* \
                ${includedir}/${PN}/libpq-* \
                ${includedir}/${PN}/internal/* \
                ${libdir}/libpq.so"
FILES_libpq += "${libdir}/libpq.so.*"
FILES_${PN}-client-${MAJOR_VER} += " \
                ${libdir}/${PN}/${MAJOR_VER}/bin/clusterdb \
                ${libdir}/${PN}/${MAJOR_VER}/bin/pg_dumpall \
                ${libdir}/${PN}/${MAJOR_VER}/bin/pg_dump \
                ${libdir}/${PN}/${MAJOR_VER}/bin/pg_basebackup \
                ${libdir}/${PN}/${MAJOR_VER}/bin/pg_isready \
                ${libdir}/${PN}/${MAJOR_VER}/bin/pg_recvlogical \
                ${libdir}/${PN}/${MAJOR_VER}/bin/pg_receivexlog \
                ${libdir}/${PN}/${MAJOR_VER}/bin/createdb \
                ${libdir}/${PN}/${MAJOR_VER}/bin/createlang \
                ${libdir}/${PN}/${MAJOR_VER}/bin/createuser \
                ${libdir}/${PN}/${MAJOR_VER}/bin/dropdb \
                ${libdir}/${PN}/${MAJOR_VER}/bin/droplang \
                ${libdir}/${PN}/${MAJOR_VER}/bin/dropuser \
                ${libdir}/${PN}/${MAJOR_VER}/bin/reindexdb \
                ${libdir}/${PN}/${MAJOR_VER}/bin/pg_restore \
                ${libdir}/${PN}/${MAJOR_VER}/bin/psql \
                ${libdir}/${PN}/${MAJOR_VER}/bin/vacuumdb \
                ${datadir}/${PN}/${MAJOR_VER}/psqlrc.sample \
		"
FILES_${PN}-contrib-${MAJOR_VER} += " \
                ${libdir}/${PN}/${MAJOR_VER}/bin/pg_archivecleanup \
                ${libdir}/${PN}/${MAJOR_VER}/bin/oid2name \
                ${libdir}/${PN}/${MAJOR_VER}/bin/pgbench \
                ${libdir}/${PN}/${MAJOR_VER}/bin/vacuumlo \
                ${libdir}/${PN}/${MAJOR_VER}/bin/pg_standby \
                ${libdir}/${PN}/${MAJOR_VER}/bin/pg_test_fsync \
                ${libdir}/${PN}/${MAJOR_VER}/bin/pg_test_timing \
                ${libdir}/${PN}/${MAJOR_VER}/lib/*.so \
                ${datadir}/${PN}/${MAJOR_VER}/contrib \
                ${datadir}/${PN}/${MAJOR_VER}/extension/* \
                "
FILES_${PN}-plperl-${MAJOR_VER} += " \
                ${libdir}/${PN}/${MAJOR_VER}/lib/plperl.so \
                ${datadir}/${PN}/${MAJOR_VER}/extension/plperl* \
                "

FILES_${PN}-plpython-${MAJOR_VER} += " \
                ${libdir}/${PN}/${MAJOR_VER}/lib/plpython2.so \
                ${datadir}/${PN}/${MAJOR_VER}/extension/plpython* \
               "
FILES_${PN}-plpython3-${MAJOR_VER} += " \
                ${libdir}/${PN}/${MAJOR_VER}/lib/plpython3.so \
                ${datadir}/${PN}/${MAJOR_VER}/extension/plpython3* \
               "
FILES_${PN}-pltcl-${MAJOR_VER} += " \
                ${libdir}/${PN}/${MAJOR_VER}/bin/pltcl_* \
                ${libdir}/${PN}/${MAJOR_VER}/lib/pltcl.so \
                ${datadir}/${PN}/${MAJOR_VER}/extension/pltcl* \
                ${datadir}/${PN}/${MAJOR_VER}/unknown.pltcl \
                "
FILES_${PN}-server-dev-${MAJOR_VER} += " \
                ${libdir}/${PN}/${MAJOR_VER}/bin/pg_config \
                ${libdir}/${PN}/${MAJOR_VER}/lib/pgxs/config \
                ${libdir}/${PN}/${MAJOR_VER}/lib/pgxs/src/Makefile* \
                ${libdir}/${PN}/${MAJOR_VER}/lib/pgxs/src/makefiles \
                ${libdir}/${PN}/${MAJOR_VER}/lib/pgxs/src/nls-global.mk \
                ${libdir}/${PN}/${MAJOR_VER}/lib/pgxs/src/test/regress/* \
                ${includedir}/* \
                "

FILES_${PN}-dbg += "${libdir}/${PN}/${MAJOR_VER}/bin/.debug/* \
                ${libdir}/${PN}/${MAJOR_VER}/lib/.debug/* \
                ${libdir}/${PN}/${MAJOR_VER}/lib/pgxs/src/test/regress/.debug/*"

DEBIANNAME_${PN} = "${DPN}"
DEBIANNAME_${PN}-dbg = "${DPN}-dbg"
DEBIANNAME_${PN}-doc = "${PN}-doc-${MAJOR_VER}"

BBCLASSEXTEND = "native"
