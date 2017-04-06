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

PR = "r1"

inherit debian-package
PV = "5.3"

# hosts file is config file from poky but can still be used
# because it contains only address of localhost.
do_install () {
	install -d ${D}${sysconfdir}
        install -m 0644 etc-rpc ${D}${sysconfdir}/rpc
        install -m 0644 etc-protocols ${D}${sysconfdir}/protocols
        install -m 0644 etc-services ${D}${sysconfdir}/services
}

CONFFILES_${PN} = "${sysconfdir}/hosts"

# Base on debian/netbase.postinst
pkg_postinst_${PN}() {
    create_hosts_file() {
        if [ -e $D${sysconfdir}/hosts ]; then return 0; fi

        cat > $D${sysconfdir}/hosts <<-EOF
	127.0.0.1       localhost
	::1             localhost ip6-localhost ip6-loopback
	ff02::1         ip6-allnodes
	ff02::2         ip6-allrouters

EOF
    }

    create_networks_file() {
        if [ -e $D${sysconfdir}/networks ]; then return 0; fi

        cat > $D${sysconfdir}/networks <<-EOF
	default         0.0.0.0
	loopback        127.0.0.0
	link-local      169.254.0.0

EOF
    }

    create_hosts_file
    create_networks_file
}
