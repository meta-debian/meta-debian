SUMMARY = "Vera font family derivate with additional characters"
DESCRIPTION = "DejaVu provides an expanded version of the Vera font family aiming for\n\
quality and broader Unicode coverage while retaining the original Vera\n\
style. DejaVu currently works towards conformance with the Multilingual\n\
European Standards (MES-1 and MES-2) for Unicode coverage. The DejaVu\n\
fonts provide serif, sans and monospaced variants.\n\
.\n\
DejaVu fonts are intended for use on low-resolution devices (mainly\n\
computer screens) but can be used in printing as well."
HOMEPAGE = "http://dejavu-fonts.org/"

inherit debian-package
PV = "2.34"

LICENSE = "BitstreamVera"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9f867da7a73fad2715291348e80d0763"

# There are no debian/patches but debian/source/format is "3.0 (quilt)"
DEBIAN_QUILT_PATCHES = ""

DEPENDS = "fontforge-native libfont-ttf-perl-native"

inherit allarch perlnative

do_compile() {
	sed -i -e "s@^#!/usr/bin/perl.*@#!/usr/bin/env nativeperl@g" ${S}/scripts/*.pl
	oe_runmake full-ttf
}

do_install() {
	install -d ${D}${datadir}/fonts/truetype/dejavu \
	           ${D}${sysconfdir}/fonts/conf.avail

	install -m 0644 ${S}/build/*.ttf ${D}${datadir}/fonts/truetype/dejavu/
	install -m 0644 ${S}/fontconfig/*.conf ${D}${sysconfdir}/fonts/conf.avail/

	install -d ${D}${sysconfdir}/fonts/conf.d \
	           ${D}${datadir}/fonts/truetype/ttf-dejavu

	for i in `ls ${D}${sysconfdir}/fonts/conf.avail/`; do
		ln -s ${sysconfdir}/fonts/conf.avail/$i \
		      ${D}${sysconfdir}/fonts/conf.d/
	done
	for i in `ls ${D}${datadir}/fonts/truetype/dejavu/`; do
		ln -s ${datadir}/fonts/truetype/dejavu/$i \
		      ${D}${datadir}/fonts/truetype/ttf-dejavu/
	done
}

PACKAGES =+ "${PN}-core ${PN}-extra \
	     ttf-dejavu ttf-dejavu-core ttf-dejavu-extra"

FILES_${PN}-core = " \
    ${datadir}/fonts/truetype/dejavu/DejaVuSans-Bold.ttf \
    ${datadir}/fonts/truetype/dejavu/DejaVuSans.ttf \
    ${datadir}/fonts/truetype/dejavu/DejaVuSansMono-Bold.ttf \
    ${datadir}/fonts/truetype/dejavu/DejaVuSansMono.ttf \
    ${datadir}/fonts/truetype/dejavu/DejaVuSerif-Bold.ttf \
    ${datadir}/fonts/truetype/dejavu/DejaVuSerif.ttf \
    ${sysconfdir}/fonts/conf.avail \
    ${sysconfdir}/fonts/conf.d \
"
FILES_${PN}-extra = " \
    ${datadir}/fonts/truetype/dejavu/DejaVu*Condensed*.ttf \
    ${datadir}/fonts/truetype/dejavu/DejaVu*ExtraLight.ttf \
    ${datadir}/fonts/truetype/dejavu/DejaVu*Italic.ttf \
    ${datadir}/fonts/truetype/dejavu/DejaVu*Oblique.ttf \
"

FILES_ttf-dejavu-core = " \
    ${datadir}/fonts/truetype/ttf-dejavu/DejaVuSans-Bold.ttf \
    ${datadir}/fonts/truetype/ttf-dejavu/DejaVuSans.ttf \
    ${datadir}/fonts/truetype/ttf-dejavu/DejaVuSansMono-Bold.ttf \
    ${datadir}/fonts/truetype/ttf-dejavu/DejaVuSansMono.ttf \
    ${datadir}/fonts/truetype/ttf-dejavu/DejaVuSerif-Bold.ttf \
    ${datadir}/fonts/truetype/ttf-dejavu/DejaVuSerif.ttf \
"

FILES_ttf-dejavu-extra = " \
    ${datadir}/fonts/truetype/ttf-dejavu/DejaVu*Condensed*.ttf \
    ${datadir}/fonts/truetype/ttf-dejavu/DejaVu*ExtraLight.ttf \
    ${datadir}/fonts/truetype/ttf-dejavu/DejaVu*Italic.ttf \
    ${datadir}/fonts/truetype/ttf-dejavu/DejaVu*Oblique.ttf \
"

ALLOW_EMPTY_${PN} = "1"
ALLOW_EMPTY_ttf-dejavu = "1"

RDEPENDS_${PN} += "${PN}-core ${PN}-extra"
RDEPENDS_${PN}-extra += "${PN}-core"
RDEPENDS_ttf-dejavu += "${PN} ttf-dejavu-core ttf-dejavu-extra"
RDEPENDS_ttf-dejavu-core += "${PN}-core"
RDEPENDS_ttf-dejavu-extra += "${PN}-extra"
