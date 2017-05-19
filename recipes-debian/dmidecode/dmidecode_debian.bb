#
# base recipe: meta/recipes-devtools/dmidecode/dmidecode_2.12.bb
# base branch: daisy
#

PR = "r0"

inherit debian-package
PV = "2.12"

SUMMARY = "DMI (Desktop Management Interface) table related utilities"
HOMEPAGE = "http://www.nongnu.org/dmidecode/"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=393a5ca445f6965873eca0259a17f833"

COMPATIBLE_HOST = "(i.86|x86_64|aarch64|arm|powerpc).*-linux"

EXTRA_OEMAKE = "-e MAKEFLAGS="

do_install() {
	oe_runmake DESTDIR="${D}" install
}

do_unpack_extra() {
	sed -i -e '/^prefix/s:/usr/local:${exec_prefix}:' ${S}/Makefile
}
addtask unpack_extra after do_unpack before do_patch
