#
# base recipe: meta/recipes-support/sqlite/sqlite3_3.27.2.bb
# base branch: warrior
#
SUMMARY = "Command line interface for SQLite 3"
DESCRIPTION = "Command line interface for SQLite 3 \
SQLite is a C library that implements an SQL database engine. \
Programs that link with the SQLite library can have SQL database \
access without running a separate RDBMS process. \
"
require recipes-support/sqlite/sqlite3.inc

inherit debian-package
require recipes-debian/sources/sqlite3.inc

LICENSE = "PD"
LIC_FILES_CHKSUM = "file://src/sqlite.h.in;endline=11;md5=786d3dc581eff03f4fd9e4a77ed00c66"

# Add dependency because debian source require tclsh command to generate shell.c
# This file is already available in meta source
DEPENDS = "tcl-native"

# Base on meta-debian branch morty, required to avoid a compile error
SRC_URI += "file://fix-hardcode-libtool.patch"

# Remove config of dynamic extension and disable-static-shell
# because these features are not available in configure file
PACKAGECONFIG_remove = "dyn_ext"
PACKAGECONFIG_class-native_remove = "dyn_ext"
PACKAGECONFIG[dyn_ext] = ""

EXTRA_OECONF_remove = "--disable-static-shell"

PACKAGECONFIG ??= ""
PACKAGECONFIG[tcl] = "--with-tcl=${STAGING_BINDIR_CROSS}, --disable-tcl, tcl"

EXTRA_OEMAKE_class-target = "CROSS_BUILDING=yes"
EXTRA_OEMAKE_class-nativesdk = "CROSS_BUILDING=yes"

PACKAGES =+ "lib${PN}-tcl"
FILES_lib${PN}-tcl += "${libdir}/tcltk/sqlite3/*"
