SUMMARY = "Japanese OpenType font set, IPAfont"
DESCRIPTION = "IPAfont is a Japanese OpenType, a scalable font which was developed to \n\
supersede both the TrueType and the Type 1 ("PostScript") font formats, \n\
font set that is provided by Information-technology Promotion Agency, \n\
Japan (IPA). \n\
. \n\
IPAfont is an outline font set suitable for both display and printing."
HOMEPAGE = "http://ossipedia.ipa.go.jp/ipafont/index.html"

inherit debian-package
PV = "00303"

LICENSE = "IPA"
LIC_FILES_CHKSUM = "file://IPA_Font_License_Agreement_v1.0.txt;md5=6cd3351ba979cf9db1fad644e8221276"

# There are no debian/patches but debian/source/format is "3.0 (quilt)"
DEBIAN_QUILT_PATCHES = ""

inherit allarch

do_install() {
	install -d ${D}${datadir}/fonts/opentype/ipafont-gothic \
	           ${D}${datadir}/fonts/opentype/ipafont-mincho
	# According to debian/fonts-ipafont-gothic.install
	install -m 0644 ${S}/ipag*.ttf ${D}${datadir}/fonts/opentype/ipafont-gothic/
	# According to debian/fonts-ipafont-mincho.install
	install -m 0644 ${S}/ipam*.ttf ${D}${datadir}/fonts/opentype/ipafont-mincho/
}

# fonts-ipafont is metapackage that depends on gothic and mincho font packages
ALLOW_EMPTY_${PN} = "1"

PACKAGES =+ "${PN}-gothic ${PN}-mincho"

FILES_${PN}-gothic = "${datadir}/fonts/opentype/ipafont-gothic"
FILES_${PN}-mincho = "${datadir}/fonts/opentype/ipafont-mincho"

RDEPENDS_${PN} += "${PN}-gothic ${PN}-mincho"
RRECOMMENDS_${PN}-gothic += "${PN}-mincho"
RRECOMMENDS_${PN}-mincho += "${PN}-gothic"

inherit update-alternatives
ALTERNATIVE_PRIORITY = "100"

ALT_LINK_DIR = "${datadir}/fonts/truetype"

# According to debian/fonts-ipafont-gothic.postinst
ALTERNATIVE_${PN}-gothic = "fonts-japanese-gothic.ttf"
ALTERNATIVE_LINK_NAME[fonts-japanese-gothic.ttf] = "${ALT_LINK_DIR}/fonts-japanese-gothic.ttf"
ALTERNATIVE_TARGET[fonts-japanese-gothic.ttf] = "${datadir}/fonts/opentype/ipafont-gothic/ipag.ttf"

# According to debian/fonts-ipafont-mincho.postinst
ALTERNATIVE_${PN}-mincho = "fonts-japanese-mincho.ttf"
ALTERNATIVE_LINK_NAME[fonts-japanese-mincho.ttf] = "${ALT_LINK_DIR}/fonts-japanese-mincho.ttf"
ALTERNATIVE_TARGET[fonts-japanese-mincho.ttf] = "${datadir}/fonts/opentype/ipafont-mincho/ipam.ttf"

pkg_postinst_${PN}-gothic() {
  test -d $D${ALT_LINK_DIR} || install -d $D${ALT_LINK_DIR}
}
pkg_postinst_${PN}-mincho() {
  test -d $D${ALT_LINK_DIR} || install -d $D${ALT_LINK_DIR}
}
