#
# base recipe: meta/recipes-support/gdbm/gdbm_1.8.3.bb
# base branch: daisy
#

PR = "r0"

inherit debian-package
PV = "1.8.3"

SUMMARY = "Key/value database library with extensible hashing"
HOMEPAGE = "http://www.gnu.org/software/gdbm/"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d8e20eece214df8ef953ed5857862150"

inherit autotools

BBCLASSEXTEND = "native nativesdk"

SRC_URI += " \
	   file://makefile.patch \
           file://libtool-mode.patch \
           file://ldflags.patch"

do_configure_prepend() {
	sed -i -e "s/-lgdbm//g" ${S}/Makefile.in
}

# provide libgdbm-dev packages
RPROVIDES_${PN}-dev = "lib${PN}-dev"
