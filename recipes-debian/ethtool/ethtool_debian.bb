#
# base recipe: meta/recipes-extended/ethtool/ethtool_3.13.bb
# base branch: daisy
#

PR = "r0"

inherit debian-package
PV = "3.16"

# source format is 3.0 but there is no patch
DEBIAN_QUILT_PATCHES = ""

SUMMARY = "Display or change ethernet card settings"
DESCRIPTION = "A small utility for examining and tuning the settings of your ethernet-based network interfaces."
HOMEPAGE = "http://www.kernel.org/pub/software/network/ethtool/"
SECTION = "console/network"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = " \
	file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
	file://ethtool.c;beginline=4;endline=17;md5=c19b30548c582577fc6b443626fc1216 \
"

SRC_URI += "\
	file://run-ptest \
"

inherit autotools ptest

# Follow debian/rules
EXTRA_OECONF += "--sbindir=${base_sbindir}"

do_install_prepend() {
	install -d ${D}${sysconfdir}/network/if-up.d
	install -d ${D}${sysconfdir}/network/if-pre-up.d
	install ${S}/debian/ethtool.if-up ${D}${sysconfdir}/network/if-up.d/ethtool
	install ${S}/debian/ethtool.if-pre-up ${D}${sysconfdir}/network/if-pre-up.d/ethtool
}

RDEPENDS_${PN}-ptest += "make"

do_compile_ptest() {
	oe_runmake test-cmdline test-features
}

do_install_ptest () {
	cp ${B}/Makefile                 ${D}${PTEST_PATH}
	install ${B}/test-cmdline        ${D}${PTEST_PATH}
	install ${B}/test-features       ${D}${PTEST_PATH}
	install ${B}/ethtool             ${D}${PTEST_PATH}/ethtool
	sed -i 's/^Makefile/_Makefile/'  ${D}${PTEST_PATH}/Makefile
}
