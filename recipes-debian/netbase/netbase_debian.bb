#
# Base recipe: meta/recipes-core/netbase/netbase_5.2.bb
# Base branch: daisy
# Base commit: 9e4aad97c3b4395edeb9dc44bfad1092cdf30a47
#

SUMMARY = "Basic TCP/IP networking support"
DESCRIPTION = "This package provides the necessary infrastructure for basic TCP/IP based networking"
HOMEPAGE = "http://packages.debian.org/netbase"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=3dd6192d306f582dee7687da3d8748ab"

PR = "r0"

inherit debian-package

SRC_URI += " \
file://hosts \
"

# hosts file is config file from poky but can still be used
# because it contains only address of localhost.
do_install () {
	install -d ${D}${sysconfdir}
        install -m 0644 ${WORKDIR}/hosts ${D}${sysconfdir}/hosts
        install -m 0644 etc-rpc ${D}${sysconfdir}/rpc
        install -m 0644 etc-protocols ${D}${sysconfdir}/protocols
        install -m 0644 etc-services ${D}${sysconfdir}/services
}

CONFFILES_${PN} = "${sysconfdir}/hosts"
