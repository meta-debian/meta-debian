SUMMARY = "Japanese TrueType font set, Takao Fonts"
DESCRIPTION = "Takao Fonts are Japanese gothic and mincho scalable fonts. They are\n\
suitable for both display and printing. This package provides them in\n\
TrueType format.\n\
.\n\
Takao Fonts are based on IPA Fonts and IPAex Fonts. Takao's purpose is to\n\
make it possible to maintain and release the fonts by the community with\n\
changing their names."
HOMEPAGE = "https://launchpad.net/takao-fonts"

inherit debian-package
PV = "003.02.01"

LICENSE = "IPA"
LIC_FILES_CHKSUM = " \
    file://README;beginline=27;endline=34;md5=36e12f02daabbf84a20f0eb0e857eab3 \
    file://IPA_Font_License_Agreement_v1.0.txt;md5=6cd3351ba979cf9db1fad644e8221276 \
"

# There are no debian/patches but debian/source/format is "3.0 (quilt)"
DEBIAN_QUILT_PATCHES = ""

inherit allarch update-alternatives

do_install() {
	install -d ${D}${datadir}/fonts/truetype/takao-gothic \
	           ${D}${datadir}/fonts/truetype/takao-mincho
	# According to debian/fonts-takao-gothic.install
	install -m 0644 ${S}/*Gothic.ttf ${D}${datadir}/fonts/truetype/takao-gothic/
	# According to debian/fonts-takao-mincho.install
	install -m 0644 ${S}/*Mincho.ttf ${D}${datadir}/fonts/truetype/takao-mincho/
}

PACKAGES =+ "${PN}-gothic ${PN}-mincho"

FILES_${PN}-gothic = "${datadir}/fonts/truetype/takao-gothic"
FILES_${PN}-mincho = "${datadir}/fonts/truetype/takao-mincho"

# fonts-takap is metapackage, depends on fonts-takao-{mincho,gothic} packages.
ALLOW_EMPTY_${PN} = "1"
RDEPENDS_${PN} += "${PN}-gothic ${PN}-mincho"

RPROVIDES_${PN}-gothic += "fonts-japanese-gothic"
RPROVIDES_${PN}-mincho += "fonts-japanese-mincho"

ALTERNATIVE_PRIORITY = "130"
ALT_LINK_DIR = "${datadir}/fonts/truetype"

# According to debian/fonts-takao-gothic.postinst
ALTERNATIVE_${PN}-gothic = "fonts-japanese-gothic.ttf"
ALTERNATIVE_LINK_NAME[fonts-japanese-gothic.ttf] = "${ALT_LINK_DIR}/fonts-japanese-gothic.ttf"
ALTERNATIVE_TARGET[fonts-japanese-gothic.ttf] = "${ALT_LINK_DIR}/takao-gothic/TakaoPGothic.ttf"

# According to debian/fonts-takao-mincho.postinst
ALTERNATIVE_${PN}-mincho = "fonts-japanese-mincho.ttf"
ALTERNATIVE_LINK_NAME[fonts-japanese-mincho.ttf] = "${ALT_LINK_DIR}/fonts-japanese-mincho.ttf"
ALTERNATIVE_TARGET[fonts-japanese-mincho.ttf] = "${ALT_LINK_DIR}/takao-mincho/TakaoPMincho.ttf"
