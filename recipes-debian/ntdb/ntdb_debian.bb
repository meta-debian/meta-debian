SUMMARY = "New Trivial Database"
DESCRIPTION = "This is a simple database API. It is modelled after the structure\n\
of GDBM. TDB features, unlike GDBM, multiple writers support with\n\
appropriate locking and transactions.\n\
. \n\
ntdb uses a file format that is incompatible with tdb, but the API\n\
is similar. It improves performance, adds support for databases\n\
larger than 4 Gb, and improves integration with talloc."
HOMEPAGE = "http://tdb.samba.org/"

inherit debian-package

PV = "1.0"

LICENSE = "LGPLv3+ & LGPL-2.1+ & GPLv3+ & BSD-3-Clause & ISC & PostgreSQL & MIT"
LIC_FILES_CHKSUM = "\
	file://LICENSE;md5=b52f2d57d10c4f7ee67a7eb9615d5d24 \
	file://lib/ccan/htable/LICENSE;md5=2d5025d4aa3495befef8f17206a5b0a1 \
	file://tools/ntdbtool.c;endline=21;md5=2a02f56e75ec38b042d701621278a645 \
	file://lib/replace/timegm.c;endline=32;md5=e1ed216ea15ed1643a625c6725e00e40 \
	file://lib/replace/inet_pton.c;endline=16;md5=d202009b255fffe6953a0d61c2d6fab0 \
	file://lib/replace/getaddrinfo.c;endline=26;md5=2a9c0f540bb750338375017f470050c5 \
	file://lib/ccan/time/LICENSE;md5=838c366f69b72c5df05c96dff79b35f2 \
"

SRC_URI += " \
	file://0005-build-unify-and-fix-endian-tests.patch \
	file://17-execute-prog-by-qemu.patch \
	file://0004-build-make-wafsamba-CHECK_SIZEOF-cross-compile-frien.patch \
"
inherit waf-samba

# Empty DEBIAN_QUILT_PATCHES to avoid error "debian/patches not found"
DEBIAN_QUILT_PATCHES = ""

EXTRA_OECONF += " \
	--disable-rpath-install \
	--with-libiconv=${STAGING_DIR_HOST}${prefix} \
	--with-gettext=${STAGING_DIR_HOST}${prefix} \
"

PACKAGES =+ "libntdb ntdb-tools python-ntdb"

FILES_libntdb = "${libdir}/*${SOLIBS}"
FILES_ntdb-tools = "${bindir}/*"
FILES_python-ntdb = "${libdir}/python${PYTHON_BASEVERSION}/dist-packages/*"

DEBIANNAME_${PN}-dev = "libntdb-dev"
RPROVIDES_${PN}-dev = "libntdb-dev"
RPROVIDES_libntdb += "libntdb1"
RDEPENDS_python-tdb = "python libntdb"
