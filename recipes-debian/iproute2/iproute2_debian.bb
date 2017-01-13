#
# base recipe: poky/meta/recipes-connectivity/iproute2/iproute2_4.1.1.bb
# base branch: jethro
#

SUMMARY = "TCP / IP networking and traffic control utilities"
DESCRIPTION = "Iproute2 is a collection of utilities for controlling \
TCP / IP networking and traffic control in Linux.  Of the utilities ip \
and tc are the most important.  ip controls IPv4 and IPv6 \
configuration and tc stands for traffic control."
HOMEPAGE = "http://www.linuxfoundation.org/collaborate/workgroups/networking/iproute2"
SECTION = "base"

PR = "r0"
inherit debian-package update-alternatives
PV = "3.16.0"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=eb723b61539feef013de476e68b5c50a \
                    file://ip/ip.c;beginline=3;endline=8;md5=689d691d0410a4b64d3899f8d6e31817"

# CFLAGS are computed in Makefile and reference CCOPTS
EXTRA_OEMAKE_append = " CCOPTS='${CFLAGS}'"

DEPENDS = "flex-native db bison-native iptables elfutils"

EXTRA_OEMAKE = "CC='${CC}' KERNEL_INCLUDE=${STAGING_INCDIR} DOCDIR=${docdir}/iproute2 \
SUBDIRS='lib ip tc bridge misc netem genl' SBINDIR='${base_sbindir}' LIBDIR='${libdir}'"

do_compile () {
	oe_runmake 'ROOTDIR=${STAGING_DIR}'
}

do_install () {
	oe_runmake DESTDIR=${D} install
}

# The .so files in iproute2 are modules, not traditional libraries
INSANE_SKIP_${PN} = "dev-so"

FILES_${PN} += " ${libdir}/tc/*"

FILES_${PN}-dbg += "${libdir}/tc/.debug"

# Add update-alternatives definitions
ALTERNATIVE_PRIORITY="100"
ALTERNATIVE_${PN} = "ip"
ALTERNATIVE_LINK_NAME[ip] = "${base_sbindir}/ip"
