SUMMARY = "LDAP-like embedded database"
DESCRIPTION = "ldb is a LDAP-like embedded database built on top of TDB.\n\
.\n\
It provides a fast database with an LDAP-like API designed\n\
to be used within an application. In some ways it can be seen as a\n\
intermediate solution between key-value pair databases and a real LDAP\n\
database."
HOMEPAGE = "http://ldb.samba.org/"

inherit debian-package

PV = "1.1.20"

LICENSE = "LGPLv3+ & GPLv3+ & BSD-3-Clause & ISC & PostgreSQL"
LIC_FILES_CHKSUM ="\
	file://lib/tevent/pytevent.c;endline=23;md5=2eb3e500e94a8828153c8e2a621857ba \
	file://tools/ldbdump.c;endline=20;md5=33a92029635cf79f078cd44d93222777 \
	file://lib/replace/timegm.c;endline=32;md5=e1ed216ea15ed1643a625c6725e00e40 \
	file://lib/replace/inet_pton.c;endline=16;md5=d202009b255fffe6953a0d61c2d6fab0 \
	file://lib/replace/getaddrinfo.c;endline=26;md5=2a9c0f540bb750338375017f470050c5 \
"
# Some modules such as dynamic library maybe can't imported while cross compile,
# we just check whether does the module exist.
SRC_URI += "file://do-not-import-target-module-while-cross-compile.patch"

inherit waf-samba pkgconfig

DEPENDS = "popt talloc tdb tevent libldap"

EXTRA_OECONF += "\
	--disable-rpath \
	--disable-rpath-install \
	--bundled-libraries=NONE,pytevent \
	--builtin-libraries=ccan,replace,tdb_compat \
	--minimum-library-version=${MIN_VER} \
	--with-modulesdir=${libdir}/ldb/modules \
	--with-libiconv=${STAGING_DIR_HOST}${prefix}"
do_configure_prepend() {
	# Get minimum-library-version follow debian/rules
	MIN_VER=$(./debian/autodeps.py --minimum-library-version .)
}

do_install_append() {
	# base on debian/rules
	rm -rf ${D}${libdir}/${PYTHON_DIR}/*-packages/*tevent.* \
	       ${D}${libdir}/${PYTHON_DIR}/*-packages/tdb.so \
	       ${D}${bindir}/tdb*
}
PACKAGES =+ "libldb ldb-tools python-ldb python-ldb-dev libldb-dev"

FILES_libldb = "${libdir}/ldb/modules/ldb/*.so ${libdir}/libldb${SOLIBS}"
FILES_ldb-tools = "${bindir}/ldb* ${libdir}/ldb/libldb-cmdline.so"
FILES_python-ldb = "${libdir}/libpyldb-util${SOLIBS} \
                    ${libdir}/${PYTHON_DIR}/dist-packages/*"
FILES_python-ldb-dev = "${includedir}/pyldb.h ${libdir}/libpyldb-util.so \
                        ${libdir}/pkgconfig/pyldb-util.pc"
FILES_libldb-dev = "${includedir}/* ${libdir}/libldb.so \
                    ${libdir}/pkgconfig/ldb.pc"

RPROVIDES_libldb += "libldb1"
DEBIAN_NOAUTONAME_python-ldb = "1"
DEBIAN_NOAUTONAME_python-ldb-dev = "1"
