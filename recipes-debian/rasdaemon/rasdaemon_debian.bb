SUMMARY = "utility to receive RAS error tracings"
DESCRIPTION = "rasdaemon is a RAS (Reliability, Availability and Serviceability) logging \
 tool.  It currently records memory errors, using the EDAC tracing events. \
 EDAC are drivers in the Linux kernel that handle detection of ECC errors \
 from memory controllers for most chipsets on x86 and ARM architectures. \
 This userspace component consists of an init script which makes sure EDAC \
 drivers and DIMM labels are loaded at system startup, as well as a utility \
 for reporting current error counts from the EDAC sysfs files."
HOMEPAGE = "https://apps.fedorahosted.org/packages/rasdaemon"

# Override value of DEBIAN_GIT_BRANCH variable in debian-package class
DEBIAN_GIT_BRANCH = "stretch-master"

inherit debian-package
PV = "0.5.8"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d3070efe0afa3dc41608bd82c00bb0dc"

inherit autotools

EXTRA_OECONF += "--enable-mce --enable-aer --enable-sqlite3 --enable-extlog \
                 --enable-abrt-report"
DEPENDS += "sqlite3"

do_install_append() {
	# Base on debian/rules and debian/rasdaemon.install
	rm -rf ${D}${includedir}
	install -d ${D}${systemd_system_unitdir}
	install -m 0644 ${B}/misc/*.service ${D}${systemd_system_unitdir}
}

FILES_${PN} += "${systemd_system_unitdir}"

RDEPENDS_${PN} += "sqlite3 systemd init-system-helpers"
