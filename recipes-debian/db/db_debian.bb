#
# base recipe: meta/recipes-support/db/db_5.3.28.bb
# base branch: master
# base commit: b0f2f690a3513e4c9fa30fee1b8d7ac2d7140657
#
# Version 5 of the Berkeley DB from Sleepycat
#
# At present this package only installs the DB code
# itself (shared libraries, .a in the dev package),
# documentation and headers.
#
# The headers have the same names as those as v3
# of the DB, only one version can be used *for dev*
# at once - DB3 and DB5 can both be installed on the
# same system at the same time if really necessary.
SUMMARY = "Berkeley Database v5"
HOMEPAGE = "http://www.oracle.com/technology/products/berkeley-db/db/index.html"

inherit debian-package
require recipes-debian/sources/db5.3.inc
BPN = "db5.3"
DEBIAN_UNPACK_DIR = "${WORKDIR}/db-${PV}"

LICENSE = "Sleepycat"
LIC_FILES_CHKSUM = " \
file://LICENSE;md5=ed1158e31437f4f87cdd4ab2b8613955 \
"

VIRTUAL_NAME ?= "virtual/db"
RCONFLICTS_${PN} = "db3"

SRC_URI += "file://fix-parallel-build.patch \
            file://0001-configure-Add-explicit-tag-options-to-libtool-invoca.patch \
            file://sequence-type.patch \
           "
FILESEXTRAPATHS =. "${COREBASE}/meta/recipes-support/db/db:"

inherit autotools

# The executables go in a separate package - typically there
# is no need to install these unless doing real database
# management on the system.
inherit lib_package

PACKAGES =+ "${PN}-cxx"
FILES_${PN}-cxx = "${libdir}/*cxx*so"

# The dev package has the .so link (as in db3) and the .a's -
# it is therefore incompatible (cannot be installed at the
# same time) as the db3 package
# sort out the .so since they do version prior to the .so
SOLIBS = "-5*.so"
FILES_SOLIBSDEV = "${libdir}/libdb.so ${libdir}/libdb_cxx.so"

# Debian adds a patch that gets versioned symbols from object files,
# it will fail if disable static build.
# Fix error:
#   | .../tmp/hosttools/ld:Versions:3: syntax error in VERSION script
DISABLE_STATIC = ""

#configuration - set in local.conf to override
# All the --disable-* options replace --enable-smallbuild, which breaks a bunch of stuff (eg. postfix)
DB5_CONFIG ?= "--enable-o_direct --disable-cryptography --disable-queue --disable-replication --disable-verify --disable-compat185 --disable-sql"

EXTRA_OECONF = "${DB5_CONFIG} --enable-shared --enable-cxx --with-sysroot STRIP=true"

EXTRA_OEMAKE += "LIBTOOL='./${HOST_SYS}-libtool'"

EXTRA_AUTORECONF += "--exclude=autoheader  -I ${S}/dist/aclocal -I${S}/dist/aclocal_java"
AUTOTOOLS_SCRIPT_PATH = "${S}/dist"

# Cancel the site stuff - it's set for db3 and destroys the
# configure.
CONFIG_SITE = ""
oe_runconf_prepend() {
	. ${S}/dist/RELEASE
	# Edit version information we couldn't pre-compute.
	sed -i -e "s/__EDIT_DB_VERSION_FAMILY__/$DB_VERSION_FAMILY/g" \
            -e "s/__EDIT_DB_VERSION_RELEASE__/$DB_VERSION_RELEASE/g" \
            -e "s/__EDIT_DB_VERSION_MAJOR__/$DB_VERSION_MAJOR/g" \
            -e "s/__EDIT_DB_VERSION_MINOR__/$DB_VERSION_MINOR/g" \
            -e "s/__EDIT_DB_VERSION_PATCH__/$DB_VERSION_PATCH/g" \
            -e "s/__EDIT_DB_VERSION_STRING__/$DB_VERSION_STRING/g" \
            -e "s/__EDIT_DB_VERSION_FULL_STRING__/$DB_VERSION_FULL_STRING/g" \
            -e "s/__EDIT_DB_VERSION_UNIQUE_NAME__/$DB_VERSION_UNIQUE_NAME/g" \
            -e "s/__EDIT_DB_VERSION__/$DB_VERSION/g" ${S}/dist/configure
}

do_compile_prepend() {
	# Stop libtool adding RPATHs
	sed -i -e 's|hardcode_into_libs=yes|hardcode_into_libs=no|' ${B}/${HOST_SYS}-libtool
}

do_install_append() {
	mkdir -p ${D}${includedir}/db51
	mv ${D}${includedir}/db.h ${D}/${includedir}/db51/.
	mv ${D}${includedir}/db_cxx.h ${D}/${includedir}/db51/.
	ln -s db51/db.h ${D}/${includedir}/db.h
	ln -s db51/db_cxx.h ${D}/${includedir}/db_cxx.h

	# The docs end up in /usr/docs - not right.
	if test -d "${D}/${prefix}/docs"
	then
		mkdir -p "${D}${datadir}"
		test ! -d "${D}${docdir}" || rm -rf "${D}/${docdir}"
		mv "${D}${prefix}/docs" "${D}/${docdir}"
	fi

	chown -R root:root ${D}
}

INSANE_SKIP_${PN} = "dev-so"
INSANE_SKIP_${PN}-cxx = "dev-so"

BBCLASSEXTEND = "native nativesdk"
