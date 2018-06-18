#
# base recipe: meta/recipes-extended/ed/ed_1.14.2.bb
# base branch: master
# base commit: 719d068bde55ef29a3468bc0779d4cb0c11e8c1d
#

SUMMARY = "Classic UNIX line editor"
HOMEPAGE = "http://www.gnu.org/software/ed/"

inherit debian-package
PV = "1.14.2"
DPR = "-2"
DSC_URI = "${DEBIAN_MIRROR}/main/e/${BPN}/${BPN}_${PV}${DPR}.dsc;md5sum=4463aa49154d025ff00ce32cc88235d8"

LICENSE = "GPLv3+ & GPLv2+ & BSD-2-Clause"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=0c7051aef9219dc7237f206c5c4179a7 \
    file://ed.h;endline=18;md5=732c441d6778edd95456153ea8c00ea6 \
    file://main.c;endline=16;md5=0c4723efd750563ae7d6ae847b50cd41 \
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
