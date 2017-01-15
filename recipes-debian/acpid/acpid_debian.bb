#
# base recipe: meta/recipes-bsp/acpid/acpid_1.0.10.bb
# base branch: daisy
#

PR = "r0"

inherit debian-package
PV = "2.0.23"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = " \
	file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b \
	file://acpid.h;endline=24;md5=324a9cf225ae69ddaad1bf9d942115b5 \
"

# init.d/acpid require lsb-base
RDEPENDS_${PN} += "lsb-base"

DEBIAN_PATCH_TYPE = "nopatch"

inherit autotools systemd

SYSTEMD_SERVICE_${PN} = "acpid.service"

do_compile () {
	oe_runmake 'CC=${CC} -D_GNU_SOURCE' 'CROSS=${HOST_PREFIX}'
}

# Use debian's init script instead of yocto's init script.
do_install_append () {
	# install /etc/init.d/acpid
	install -d ${D}${sysconfdir}/init.d
	install ${S}/debian/acpid.init ${D}${sysconfdir}/init.d/acpid

	# install /etc/default/acpid
	install -d ${D}${sysconfdir}/default
	install ${S}/debian/acpid.default ${D}${sysconfdir}/default/acpid

	# install /etc/systemd/system/
	install -d ${D}${systemd_unitdir}/system
	install ${S}/debian/acpid.path ${D}${systemd_unitdir}/system/
	install ${S}/debian/acpid.service ${D}${systemd_unitdir}/system/
	install ${S}/debian/acpid.socket ${D}${systemd_unitdir}/system/

	# install /etc/acpi/events/
	install -d ${D}${sysconfdir}/acpi
	install -d ${D}${sysconfdir}/acpi/events
}

PACKAGES += "kacpimon"
FILES_kacpimon = "${sbindir}/kacpimon"
FILES_${PN} = " \
	${bindir} \
	${sbindir}/acpid \
	${sysconfdir} \
	${systemd_unitdir} \
"
