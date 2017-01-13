#
# base recipe: meta-cgl/tree/meta-cgl-common/recipes-cgl/makedumpfile/makedumpfile_1.5.8.bb
# base commit: da9c6ac761cab4001086922a6bf3bffd8db0e429
#

SUMMARY = "VMcore extraction tool"
DESCRIPTION = "This program is used to extract a subset of the memory available either \
via /dev/mem or /proc/vmcore (for crashdumps). It is used to get memory \
images without extra uneeded information (zero pages, userspace programs, etc)."

PR = "r0"

inherit debian-package
PV = "1.5.3"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "zlib elfutils bzip2"

# alias-powerpc-powerpc32.patch:
# 	Create alias for target for powerpc as powerpc32
SRC_URI += "file://alias-powerpc-powerpc32.patch"

inherit binconfig

EXTRA_OEMAKE = "TARGET=${TARGET_ARCH}"

do_compile() {
	# Follow debian/rules
	oe_runmake LINKTYPE=dynamic
}

do_install() {
	install -d ${D}${bindir} ${D}${sbindir} \
	           ${D}${sysconfdir}/default \
	           ${D}${sysconfdir}/init.d \
	           ${D}${systemd_system_unitdir}

	install -c -m 755 ${S}/makedumpfile ${D}${bindir}/
	install -c -m 755 ${S}/makedumpfile-R.pl ${D}${bindir}/

	# Follow debian/kdump-tools.install
	cp ${S}/debian/kdump-config ${D}${sbindir}/

	cp ${S}/debian/kdump-tools.default ${D}${sysconfdir}/default/kdump-tools
	cp ${S}/debian/kdump-tools.init  ${D}${sysconfdir}/init.d/kdump-tools
	cp ${S}/debian/kdump-tools.service ${D}${systemd_system_unitdir}/
}

PACKAGES =+ "kdump-tools"
FILES_kdump-tools = " \
    ${sbindir}/kdump-config \
    ${sysconfdir}/default/kdump-tools \
    ${sysconfdir}/init.d/kdump-tools \
    ${systemd_system_unitdir}/kdump-tools.service \
"
