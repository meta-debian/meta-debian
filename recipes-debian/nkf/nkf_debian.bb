PR = "r0"

inherit debian-package
PV = "2.13"

LICENSE = "Zlib"
LIC_FILES_CHKSUM = "file://nkf.c;endline=22;md5=a30d8d09c708efb8fa61f6bedb1d6677"

# Use environment variable
EXTRA_OEMAKE = "-e"

do_install(){
	install -d ${D}${bindir} ${D}${mandir}/man1 ${D}${mandir}/ja/man1
	install -m 0755 ${S}/nkf ${D}${bindir}
	install -m 0444 ${S}/nkf.1 ${D}${mandir}/man1/
	install -m 0444 ${S}/nkf.1j ${D}${mandir}/ja/man1/nkf.1
}

BBCLASSEXTEND = "native"
