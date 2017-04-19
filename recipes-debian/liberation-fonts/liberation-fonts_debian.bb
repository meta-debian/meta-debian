SUMMARY = "Fonts with the same metrics as Times, Arial and Courier"
DESCRIPTION = "A set of serif, sans-serif and monospaced fonts from Red Hat with \
exactly the same metrics as the (non-free) Microsoft Times, Arial \
and Courier fonts, which implies those fonts can serve as a drop-in \
replacement. The font family is named Liberation."
HOMEPAGE = "https://fedorahosted.org/liberation-fonts/"

PR = "r0"

inherit debian-package
PV = "1.07.4"
DPN = "fonts-liberation"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "fontforge-native"

inherit allarch fontcache

# source format is 3.0 (quilt) but there is no debian patch
DEBIAN_QUILT_PATCHES = ""

do_install() {
	# Follow debian/install
	install -d ${D}${datadir}/fonts/truetype/liberation
	cp ${S}/liberation-fonts-ttf-*/*.ttf ${D}${datadir}/fonts/truetype/liberation/

	# Follow debian/ttf-liberation.links
	install -d ${D}${datadir}/fonts/truetype/ttf-liberation/
	for i in ${D}${datadir}/fonts/truetype/liberation/*.ttf; do
		fname=`basename $i`
		ln -sf ../liberation/$fname ${D}${datadir}/fonts/truetype/ttf-liberation/
	done
}

PACKAGES =+ "ttf-liberation"
FILES_${PN} += "${datadir}/fonts/truetype/liberation"
FILES_ttf-liberation = "${datadir}/fonts/truetype/ttf-liberation"

PKG_${PN} = "fonts-liberation"
RPROVIDES_${PN} = "fonts-liberation"
