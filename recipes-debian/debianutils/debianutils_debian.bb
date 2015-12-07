# base recipe: meta/recipes-support/debianutils/debianutils_4.5.1.bb
# base branch: master

SUMMARY = "Miscellaneous utilities specific to Debian"
SECTION = "base"
LICENSE = "GPLv2+ & SMAIL_GPL"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=f01a5203d50512fc4830b4332b696a9f"

PR = "r0"
inherit debian-package

inherit autotools

do_install_append() {
	install -d ${D}${base_bindir}
	mv ${D}${bindir}/run-parts ${D}${base_bindir}/
	mv ${D}${bindir}/tempfile ${D}${base_bindir}/
	mv ${D}${bindir}/which ${D}${base_bindir}/

	# create link binary
	ln -s ${base_bindir}/which ${D}${bindir}/which
}
