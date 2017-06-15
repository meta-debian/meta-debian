SUMMARY = "Update Configuration File(s): preserve user changes to config files"
DESCRIPTION = " Debian policy mandates that user changes to configuration files must be \
 preserved during package upgrades. The easy way to achieve this behavior \
 is to make the configuration file a 'conffile', in which case dpkg \
 handles the file specially during upgrades, prompting the user as \
 needed."

inherit debian-package
PV = "3.0030"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=361b6b837cad26c6900a926b62aada5f"

inherit autotools-brokensep

DEPENDS += "coreutils-native"

do_install() {
	install -d ${D}${bindir} \
	           ${D}${sysconfdir} \
	           ${D}${localstatedir}/lib/ucf
	touch ${D}${localstatedir}/lib/ucf/hashfile
	touch ${D}${localstatedir}/lib/ucf/registry

	for file in ucf ucfr ucfq lcf; do
		install -p -m 755 $file ${D}${bindir}
	done
	install -p -m 644 ${S}/ucf.conf ${D}${sysconfdir}
}
BBCLASSEXTEND += "native"

RDEPENDS_${PN} += "debconf coreutils"
