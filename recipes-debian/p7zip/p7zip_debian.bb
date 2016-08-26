DESCRIPTION = "7-zip is a commandline utility handling 7z archives."

PR = "r0"

LICENSE = "LGPL-2.1+"
LIC_FILES_CHKSUM = "file://DOCS/copying.txt;md5=ecfc54c9e37b63ac58900061ce2eab5a \
                    file://DOCS/License.txt;md5=87cd66bd5ea9bff75753c9fbbecc5073 \
                    "

inherit debian-package

DEBIAN_PATCH_TYPE = "quilt"

# all3: to build bin/7za, bin/7z (with its plugins), bin/7zr and bin/7zCon.sfx
EXTRA_OEMAKE = "all3"

do_install() {
        install -d ${D}${bindir}
        install -d ${D}${libdir}/p7zip

	install -m 0755 ${S}/bin/7zr ${D}${libdir}/p7zip
	install -m 0755 ${S}/debian/scripts/p7zip ${D}${bindir}
	install -m 0755 ${S}/debian/scripts/7zr ${D}${bindir}

        install -m 0755 ${S}/bin/7z ${D}${libdir}/p7zip
        install -m 0755 ${S}/bin/7zCon.sfx ${D}${libdir}/p7zip
        install -m 0755 ${S}/bin/7za ${D}${libdir}/p7zip
        install -m 0755 ${S}/bin/7z.so ${D}${libdir}/p7zip
        install -m 0755 ${S}/debian/scripts/7z ${D}${bindir}
        install -m 0755 ${S}/debian/scripts/7za ${D}${bindir}
}

PACKAGES =+ "${PN}-full"
FILES_${PN} += "${libdir}/p7zip/7zr"
FILES_${PN}-full = "${libdir}/p7zip/7z \
                    ${libdir}/p7zip/7zCon.sfx \
                    ${libdir}/p7zip/7za \
                    ${libdir}/p7zip/7z.so \
                    ${bindir}/7z \
                    ${bindir}/7za \
                    "

RRECOMMENDS_${PN} += "${PN}-full"

