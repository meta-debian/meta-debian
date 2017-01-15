# base recipe: meta/recipes-support/debianutils/debianutils_4.5.1.bb
# base branch: master

SUMMARY = "Miscellaneous utilities specific to Debian"
SECTION = "base"
LICENSE = "GPLv2+ & SMAIL_GPL"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=f01a5203d50512fc4830b4332b696a9f"

PR = "r0"
inherit debian-package
PV = "4.4"

inherit autotools update-alternatives

do_install_append() {
	install -d ${D}${base_bindir}
	mv ${D}${bindir}/run-parts ${D}${base_bindir}/
	mv ${D}${bindir}/tempfile ${D}${base_bindir}/
	mv ${D}${bindir}/which ${D}${base_bindir}/

	# create link binary
	ln -s ${base_bindir}/which ${D}${bindir}/which
}

ALTERNATIVE_PRIORITY="100"
ALTERNATIVE_${PN} = "run-parts which"
ALTERNATIVE_LINK_NAME[run-parts] = "${base_bindir}/run-parts"
ALTERNATIVE_LINK_NAME[which] = "${base_bindir}/which"
