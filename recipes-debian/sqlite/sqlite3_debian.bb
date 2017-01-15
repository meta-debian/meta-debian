#
# base recipe: meta/recipes-support/sqlite/sqlite3_3.8.3.1.bb
# base branch: daisy
#

PR = "r1"

inherit debian-package
PV = "3.8.7.1"

LICENSE = "PD"
LIC_FILES_CHKSUM = " \
  file://src/sqlite.h.in;endline=11;md5=65f0a57ca6928710b418c094b3570bb0 \
"

DEPENDS = "readline ncurses tcl"
DEPENDS_class-native = "tcl-native"

# Required to avoid a compile error
SRC_URI += "file://fix-hardcode-libtool.patch"

inherit autotools pkgconfig

# Follow debian/rules
BUILD_CFLAGS += " \
	-O2 -fno-strict-aliasing \
	-DSQLITE_SECURE_DELETE -DSQLITE_ENABLE_COLUMN_METADATA \
	-DSQLITE_ENABLE_FTS3 -DSQLITE_ENABLE_RTREE=1 -DSQLITE_SOUNDEX=1 \
	-DSQLITE_ENABLE_UNLOCK_NOTIFY \
	-DSQLITE_OMIT_LOOKASIDE=1 \
	-DSQLITE_ENABLE_UPDATE_DELETE_LIMIT=1 \
	-DSQLITE_MAX_SCHEMA_RETRY=25 \
	-DSQLITE_MAX_VARIABLE_NUMBER=250000 \
"

TARGET_CFLAGS += " \
	-O2 -fno-strict-aliasing \
	-DSQLITE_SECURE_DELETE -DSQLITE_ENABLE_COLUMN_METADATA \
	-DSQLITE_ENABLE_FTS3 -DSQLITE_ENABLE_RTREE=1 -DSQLITE_SOUNDEX=1 \
	-DSQLITE_ENABLE_UNLOCK_NOTIFY \
	-DSQLITE_OMIT_LOOKASIDE=1 \
	-DSQLITE_ENABLE_UPDATE_DELETE_LIMIT=1 \
	-DSQLITE_MAX_SCHEMA_RETRY=25 \
	-DSQLITE_MAX_VARIABLE_NUMBER=250000 \
"
EXTRA_OECONF = " \
    --enable-shared --enable-threadsafe --enable-load-extension \
    --with-tcl=${STAGING_BINDIR_CROSS} TCLLIBDIR=${libdir}/tcltk/sqlite3 \
"
EXTRA_OECONF_class-native = " \
    --enable-shared --enable-threadsafe --disable-readline \
    --with-tcl=${STAGING_LIBDIR}/tcl8.6 TCLLIBDIR=${libdir}/tcltk/sqlite3 \
"

export config_BUILD_CC = "${BUILD_CC}"
export config_BUILD_CFLAGS = "${BUILD_CFLAGS}"
export config_BUILD_LIBS = "${BUILD_LDFLAGS}"
export config_TARGET_CC = "${CC}"
export config_TARGET_LINK = "${CCLD}"
export config_TARGET_CFLAGS = "${CFLAGS}"
export config_TARGET_LFLAGS = "${LDFLAGS}"

# pread() is in POSIX.1-2001 so any reasonable system must surely support it
BUILD_CFLAGS += "-DUSE_PREAD"
TARGET_CFLAGS += "-DUSE_PREAD"

do_compile_prepend_class-target () {
	export CROSS_BUILDING="yes"
}

do_install_prepend_class-target () {
	export CROSS_BUILDING="yes"
}

# Fix error: ./lemon: Command not found
# lemon need be built with $(BCC) instead of $(LTLINK)
do_compile_prepend_class-nativesdk(){
	export CROSS_BUILDING="yes"
}

do_install_prepend_class-nativesdk(){
	export CROSS_BUILDING="yes"
}

#
# Debian Native Test
#
inherit debian-test

SRC_URI_DEBIAN_TEST = "\
        file://sqlite3-native-test/run_native_test_sqlite3 \
        file://sqlite3-native-test/run_create_table \
        file://sqlite3-native-test/run_insert_record \
        file://sqlite3-native-test/run_view_record \
        file://sqlite3-native-test/run_alter_tablename \
        file://sqlite3-native-test/run_version_command \
        file://sqlite3-native-test/run_drop_table \
        file://sqlite3-native-test/insert-data.sql \
"

DEBIAN_NATIVE_TESTS = "run_native_test_sqlite3"
TEST_DIR = "${B}/native-test"

PACKAGES = "lib${DPN} lib${DPN}-dev lib${DPN}-doc ${PN}-dbg lib${DPN}-staticdev lib${PN}-tcl ${PN}"

FILES_${PN} = "${bindir}/*"
FILES_lib${DPN} = "${libdir}/*.so.*"
FILES_lib${DPN}-dev = "${libdir}/*.la ${libdir}/*.so \
			${libdir}/pkgconfig ${includedir}"
FILES_lib${DPN}-doc = "${docdir} ${mandir} ${infodir}"
FILES_lib${DPN}-staticdev = "${libdir}/lib*.a"
FILES_lib${PN}-tcl = "${libdir}/tcltk/sqlite3/*"
FILES_${PN}-dbg += "${libdir}/tcltk/sqlite3/.debug"

AUTO_LIBNAME_PKGS = "${MLPREFIX}lib${DPN}"

BBCLASSEXTEND = "native nativesdk"
