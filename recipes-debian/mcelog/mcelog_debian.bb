SUMMARY = "x86 Machine Check Exceptions collector and decoder"
DESCRIPTION = "The Linux kernel for x86 CPUs no longer decodes and logs recoverable\n\
 Machine Check Exception (MCE) events to the kernel log on its own.\n\
 .\n\
 Instead, the MCE data is kept in a buffer which can be read from userspace\n\
 via the /dev/mcelog device node.\n\
 .\n\
 You need this tool to collect and decode those events; it will log the decoded\n\
 MCE events to syslog.\n\
 .\n\
 This tool supports only Intel processors and AMD processors with model number\n\
 16 (K8)."
HOMEPAGE = "http://mcelog.org/"

inherit debian-package
PV = "104"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://rbtree.c;endline=22;md5=bdee94c30b5c40033693bbbc71c444cb"

inherit autotools-brokensep

do_install_append() {
	install -D -m 0644 ${S}/debian/mcelog.default \
		${D}${sysconfdir}/default/mcelog
	install -D -m 0755 ${S}/debian/mcelog.init \
		${D}${sysconfdir}/init.d/mcelog
}

RDEPENDS_${PN} += "debconf udev"
