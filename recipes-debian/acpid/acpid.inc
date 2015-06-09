#
# Base on meta/recipes-bsp/acpid/acpid.inc
#

SUMMARY = "A daemon for delivering ACPI events"
HOMEPAGE = "http://sourceforge.net/projects/acpid/"
BUGTRACKER = "http://sourceforge.net/tracker/?group_id=33140&atid=407341"
SECTION = "base"
LICENSE = "GPLv2+"

#SRC_URI = "${SOURCEFORGE_MIRROR}/acpid/acpid-${PV}.tar.gz \
#           file://init \
#           file://set_socket_noblock.patch "

#inherit update-rc.d

#INITSCRIPT_NAME = "acpid"
#INITSCRIPT_PARAMS = "defaults"

# Makefile ignores our CFLAGS, so override it.
#
EXTRA_OEMAKE = "CFLAGS='-W -Wall -Werror -Wundef -Wshadow ${CFLAGS} $(DEFS)'"

do_compile () {
	oe_runmake 'CC=${CC} -D_GNU_SOURCE' 'CROSS=${HOST_PREFIX}'
}

#do_install () {
#	install -d ${D}${bindir} 
#	oe_runmake 'INSTPREFIX=${D}' install
#	install -d ${D}${sysconfdir}/init.d
#	cat ${WORKDIR}/init | sed -e's,/usr/sbin,${sbindir},g' > ${D}${sysconfdir}/init.d/acpid
#	chmod 755 ${D}${sysconfdir}/init.d/acpid
#
#	install -d ${D}${sysconfdir}/acpi
#	install -d ${D}${sysconfdir}/acpi/events
#}

#
# debian
#

inherit debian-package autotools

DEBIAN_SECTION = "admin"
DEBIAN_PATCH_TYPE = "nopatch"

# Use DESTDIR instead of INSTPREFIX.
# Use debian's init script instead of yocto's init script.
do_install () {
	oe_runmake 'DESTDIR=${D}' install
	
	# install /etc/init.d/acpid
	install -d ${D}${sysconfdir}/init.d
	install ${S}/debian/acpid.init ${D}${sysconfdir}/init.d/acpid
	
	# install /etc/default/acpid
	install -d ${D}${sysconfdir}/default
        install ${S}/debian/acpid.default ${D}${sysconfdir}/default/
	
	# install /etc/systemd/system/
	install -d ${D}${systemd_unitdir}/system
	install ${S}/debian/acpid.path ${D}${systemd_unitdir}/system/
	install ${S}/debian/acpid.service ${D}${systemd_unitdir}/system/
	install ${S}/debian/acpid.socket ${D}${systemd_unitdir}/system/
	
	# install /etc/acpi/events/
	install -d ${D}${sysconfdir}/acpi
	install -d ${D}${sysconfdir}/acpi/events
}

FILES_${PN} += "${systemd_unitdir}"
