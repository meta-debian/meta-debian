SUMMARY = "Trivial Database"
DESCRIPTION = "This is a simple database API. It is modelled after the structure \
of GDBM. TDB features, unlike GDBM, multiple writers support with \
appropriate locking and transactions."
HOMEPAGE = "http://tdb.samba.org/"

inherit debian-package

PV="1.3.6"

LICENSE = "GPLv3+ & LGPLv3+ & BSD-3-Clause & ISC & PostgreSQL"
LIC_FILES_CHKSUM = "\
	file://tools/tdbdump.c;endline=18;md5=b59cd45aa8624578126a8c98f48018c4 \
	file://include/tdb.h;endline=27;md5=f5bb544641d3081821bcc1dd58310be6 \
	file://lib/replace/timegm.c;endline=32;md5=e1ed216ea15ed1643a625c6725e00e40 \
	file://lib/replace/inet_pton.c;endline=16;md5=d202009b255fffe6953a0d61c2d6fab0 \
	file://lib/replace/getaddrinfo.c;endline=26;md5=2a9c0f540bb750338375017f470050c5 \
"
inherit waf-samba update-alternatives

EXTRA_OECONF += " \
	--disable-rpath-install \
	--with-libiconv=${STAGING_DIR_HOST}${prefix} \
"
do_install_append() {
	mv ${D}${bindir}/tdbbackup ${D}${bindir}/tdbbackup.tdbtools
}

PACKAGES =+ "libtdb tdb-tools python-tdb"

FILES_libtdb = "${libdir}/*${SOLIBS}"
FILES_tdb-tools = "${bindir}/*"
FILES_python-tdb = "${libdir}/${PYTHON_DIR}/dist-packages/*"

DEBIANNAME_${PN}-dev = "libtdb-dev"
RPROVIDES_${PN}-dev += "libtdb-dev"
RPROVIDES_libtdb += "libtdb1"

RDEPENDS_python-tdb = "python libtdb"

# Add update-alternatives definitions
ALTERNATIVE_PRIORITY = "10"
ALTERNATIVE_tdb-tools = "tdbbackup"
ALTERNATIVE_LINK_NAME[tdbbackup] = "${bindir}/tdbbackup"
ALTERNATIVE_TARGET[tdbbackup] = "${bindir}/tdbbackup.tdbtools"
