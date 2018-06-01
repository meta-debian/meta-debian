#
# base recipe: meta/recipes-extended/ed/ed_1.14.2.bb
# base branch: master
# base commit: 719d068bde55ef29a3468bc0779d4cb0c11e8c1d
#

SUMMARY = "Classic UNIX line editor"
HOMEPAGE = "http://www.gnu.org/software/ed/"

inherit debian-package
PV = "1.14.2"

LICENSE = "GPLv3+ & GPLv2+ & BSD-2-Clause"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=f27defe1e96c2e1ecd4e0c9be8967949 \
    file://ed.h;endline=20;md5=2b62ce887e37f828a04aa32f1ec23787 \
    file://main.c;endline=17;md5=002b306d8e804a6fceff144b26554253 \
    file://carg_parser.h;endline=18;md5=407288513b2b9492418fc61112d342de \
"

# source format is 3.0 (quilt) but there is no debian/patches
DEBIAN_QUILT_PATCHES = ""

# LSB states that ed should be in /bin/
bindir = "${base_bindir}"

EXTRA_OEMAKE = "-e MAKEFLAGS="

inherit texinfo update-alternatives

do_configure() {
	${S}/configure
}

do_install() {
	oe_runmake DESTDIR="${D}" install
}

# Add update-alternatives definitions to avoid confict with busybox
ALTERNATIVE_${PN} = "ed"
ALTERNATIVE_PRIORITY[ed] = "100"
ALTERNATIVE_LINK_NAME[ed] = "${base_bindir}/ed"

BBCLASSEXTEND = "native"
