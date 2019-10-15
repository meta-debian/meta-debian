# base recipe: meta-openembedded/meta-oe/recipes-support/pps-tools/pps-tools_1.0.2.bb
# base branch: warrior

SUMMARY = "User-space tools for LinuxPPS"
HOMEPAGE = "http://linuxpps.org"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

DEBIAN_QUILT_PATCHES = ""

inherit debian-package
require recipes-debian/sources/pps-tools.inc

RDEPENDS_${PN} = "bash"

do_install() {
        install -d ${D}${bindir} ${D}${includedir} \
                   ${D}${includedir}/sys
        oe_runmake 'DESTDIR=${D}' install
}
