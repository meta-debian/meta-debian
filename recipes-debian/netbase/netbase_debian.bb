SUMMARY = "Basic TCP/IP networking support"
DESCRIPTION = "This package provides the necessary infrastructure for basic TCP/IP based networking"
HOMEPAGE = "http://packages.debian.org/netbase"
SECTION = "base"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=3dd6192d306f582dee7687da3d8748ab"
PE = "1"

inherit debian-package
require recipes-debian/sources/netbase.inc
FILESEXTRAPATHS =. "${FILE_DIRNAME}/netbase:${COREBASE}/meta/recipes-core/netbase/netbase:"

DEBIAN_PATCH_TYPE = "nopatch"

SRC_URI += " \
           file://netbase-add-rpcbind-as-an-alias-to-sunrpc.patch \
           "

UPSTREAM_CHECK_URI = "${DEBIAN_MIRROR}/main/n/netbase/"
do_install () {
	install -d ${D}/${mandir}/man8 ${D}${sysconfdir}
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
	127.0.0.1	localhost
	::1		localhost ip6-localhost ip6-loopback
	ff02::1		ip6-allnodes
	ff02::2		ip6-allrouters

EOF
    }

    create_networks_file() {
        if [ -e $D${sysconfdir}/networks ]; then return 0; fi

        cat > $D${sysconfdir}/networks <<-EOF
	default		0.0.0.0
	loopback	127.0.0.0
	link-local	169.254.0.0

EOF
    }

    create_hosts_file
    create_networks_file
}
