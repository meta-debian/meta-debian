PR = "r0"

inherit debian-package
PV = "8.11+urwcyr1.0.7~pre44"

DESCRIPTION = "Fonts for the Ghostscript interpreter"
HOMEPAGE = "http://www.ghostscript.com/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

DEBIAN_SECTION = "fonts"
DEBIAN_PATCH_TYPE = "nopatch"

do_install() {
        install -d ${D}/${datadir}/fonts/type1/gsfonts
        install -d ${D}/${sysconfdir}/ghostscript/fontmap.d/

        install -m 0644 ${S}/*.afm ${D}/${datadir}/fonts/type1/gsfonts/
        install -m 0644 ${S}/*.pfb ${D}/${datadir}/fonts/type1/gsfonts/
        install -m 0644 ${S}/*.pfm ${D}/${datadir}/fonts/type1/gsfonts/
        install -m 0644 ${S}/debian/10gsfonts.conf ${D}/${sysconfdir}/ghostscript/fontmap.d/
}
FILES_${PN} += "/usr/share/fonts/"
