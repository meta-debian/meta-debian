SUMMARY = "Program to create dependencies in makefiles"
DESCRIPTION = "The gccmakedep program calls 'gcc -M' to output makefile \
rules describing the dependencies of each sourcefile, so that make knows \
which object files must be recompiled when a dependency has changed."

require xorg-util-common.inc

PR = "${INC_PR}.0"

LIC_FILES_CHKSUM = "file://Makefile.am;endline=20;md5=23c277396d690413245ebb89b18c5d4d"

DEPENDS = "util-macros"

do_install_append() {
	sed -i "s,--sysroot=${STAGING_DIR_TARGET},," ${D}${bindir}/gccmakedep
}

RDEPENDS_${PN} = "gcc gcc-symlinks"
