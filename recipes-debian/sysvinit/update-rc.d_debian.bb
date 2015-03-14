DESCRIPTION = "update-rc.d is a utilities that allows the management of \
symlinks to the initscripts in the /etc/rcN.d directory structure."

inherit allarch debian-package
DEBIAN_SECTION = "admin"
DPR = "0"
DPN = "sysvinit"

LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS += "perl"

do_compile() {
	:
}

do_install() {
	install -d ${D}${sbindir}
	install -m 0755 ${S}/debian/src/sysv-rc/sbin/update-rc.d ${D}${sbindir}

	# rc$level.d folders should be created for update-rc.d to run
	install -d ${D}${sysconfdir} \
		${D}${sysconfdir}/default \
		${D}${sysconfdir}/init.d
	for level in S 0 1 2 3 4 5 6; do
		install -d ${D}${sysconfdir}/rc$level.d
	done
}

BBCLASSEXTEND = "native"
