#
# base recipe: meta/recipes-extended/sed/sed_4.2.2.bb
# base branch: daisy
#

PR = "r0"

inherit debian-package

LICENSE = "GPLv2+ & GPLv3+"
LIC_FILES_CHKSUM = " \
	file://COPYING;md5=f27defe1e96c2e1ecd4e0c9be8967949 \
	file://sed/sed.h;beginline=1;endline=17;md5=767ab3a06d7584f6fd0469abaec4412f \
	file://debian/copyright;md5=27c0cc3a8e6b182f66de46caf568799a \
"

inherit autotools update-alternatives gettext

# Follow debian/rules
EXTRA_OECONF = " \
	--without-included-regex \
"

do_install () {
	autotools_do_install
	install -d ${D}${base_bindir}
	mv ${D}${bindir}/sed ${D}${base_bindir}/sed
	rmdir ${D}${bindir}/
}

ALTERNATIVE_${PN} = "sed"
ALTERNATIVE_LINK_NAME[sed] = "${base_bindir}/sed"
ALTERNATIVE_PRIORITY = "100"

BBCLASSEXTEND = "native"
