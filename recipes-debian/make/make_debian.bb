#
# Base recipe: meta/recipes-devtools/make/make.inc
# Base branch: daisy
# Base commit: 9e4aad97c3b4395edeb9dc44bfad1092cdf30a47
# 
SUMMARY = "GNU tool that controls the build process"
DESCRIPTION = "Make is a tool which controls the generation of executables and other non-source files of a \
program from the program's source files.  Make gets its knowledge of how to build your program from a file \
called the makefile, which lists each of the non-source files and how to compute it from other files."
HOMEPAGE = "http://www.gnu.org/software/make/"

PR = "r0"
DPN = "make-dfsg"

inherit autotools gettext pkgconfig debian-package
PV = "4.0"

LICENSE = "GPLv3 & LGPLv2"
LIC_FILES_CHKSUM = " \
file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
file://tests/COPYING;md5=d32239bcb673463ab874e80d47fae504 \
file://glob/COPYING.LIB;md5=4a770b67e6be0f60da244beb2de0fce4 \
"

# Install script make-first-existing-target based on
# list of file make package from Debian
do_install_append() {
	install -m 0644 ${S}/debian/make-first-existing-target ${D}${bindir}
}

# Change location of gnumake.h file from make-dev to make package
# due to list of file in make package from Debian.
FILES_${PN}-dev = ""
FILES_${PN} = "${includedir}/* ${bindir}/*"

EXTRA_OECONF += "--without-guile"

BBCLASSEXTEND = "native nativesdk"
