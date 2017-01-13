#
# base recipe: https://github.com/joelagnel/meta-openembedded/blob/master/\
#	       meta-oe/recipes-support/rng-tools/rng-tools_2.bb
# base branch: master
# base commit: 7fd47b69000367319886af45151581c0ecd88310
#

SUMMARY = "Random number generator daemon"
DESCRIPTION = " rngd is a daemon that runs conditioning tests (from FIPS 140-2,\
		edition of 2002-10-10) on a source of random data, and if that \
		data passes the FIPS test,feeds it back as trusted entropy to the\
		in-kernel entropy pool.  Thus, it increases the amount of true \
		random data the kernel has available."

PR = "r0"
inherit debian-package
PV = "2-unofficial-mt.14"

LICENSE = "GPL-2.0+"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

# source format is 3.0 (quilt) but there is no debian patch
DEBIAN_QUILT_PATCHES = ""

inherit autotools

do_install_append() {
	#Create new folders
	install -d ${D}${sysconfdir}
	install -d ${D}${sysconfdir}/default
	install -d ${D}${sysconfdir}/init.d
	install -d ${D}${sysconfdir}/logcheck
	install -d ${D}${sysconfdir}/logcheck/ignore.d.server
	install -d ${D}${sysconfdir}/logcheck/violations.ignore.d

	install -m 0644 ${S}/debian/rng-tools.default 			\
			${D}${sysconfdir}/default/rng-tools

	install -m 0755 ${S}/debian/rng-tools.init 			\
			${D}${sysconfdir}/init.d/rng-tools

	install -m 0644 ${S}/debian/logcheck.ignore 			\
			${D}${sysconfdir}/logcheck/ignore.d.server/rng-tools

	cp ${D}${sysconfdir}/logcheck/ignore.d.server/rng-tools 	\
		${D}${sysconfdir}/logcheck/violations.ignore.d/rng-tools
}
