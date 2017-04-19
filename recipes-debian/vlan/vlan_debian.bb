SUMMARY = "user mode programs to enable VLANs on your ethernet devices"
DESCRIPTION = "This package contains the user mode programs you need to add and remove\n\
VLAN devices from your ethernet devices.\n\
.\n\
A typical application for a VLAN enabled box is a single wire firewall,\n\
router or load balancer.\n\
.\n\
You need a VLAN Linux kernel for this.  Linux kernel versions >= 2.4.14\n\
have VLAN support."

inherit debian-package
PV = "1.9"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = " \
    file://README;endline=5;md5=86ddb7e7b4e5b798703cd7c8252661ac \
    file://macvlan_config.c;endline=21;md5=fcdc253b2554a839155a4205674d5b99 \
"

# There is no debian patches
DEBIAN_PATCH_TYPE = "nopatch"

# Avoid strip
EXTRA_OEMAKE += "STRIP=true"

do_compile() {
	oe_runmake
}

do_install() {
	# Base on debian/vlan.install
	install -d ${D}${sysconfdir} \
	           ${D}${base_sbindir}
	cp -r ${S}/debian/network ${D}${sysconfdir}/
	install -m 755 ${B}/vconfig ${D}${base_sbindir}/
}
