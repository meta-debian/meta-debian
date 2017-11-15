#
# skerlet
#
SUMMARY = "PPS tools"
DESCRIPTION = "User-space tools for LinuxPPS"
HOMEPAGE = "https://github.com/ago/pps-tools"

PR = "r0"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

inherit debian-package
PV = "0.2"

FILES_${PN} += "/usr/include/sys/*"

do_install() {
        install -d ${D}${bindir} ${D}${includedir} \
                   ${D}${includedir}/sys
        oe_runmake 'DESTDIR=${D}' install
}

