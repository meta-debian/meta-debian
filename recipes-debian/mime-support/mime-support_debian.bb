#
# base recipe: http://cgit.openembedded.org/cgit.cgi/meta-openembedded/tree\
#/meta-oe/recipes-support/mime-support/mime-support_3.48.bb?h=master
# base branch: master
# base commit: ea319464b673cbf9a416b582dc4766faeb998430
#

SUMMARY = "MIME files 'mime.types' & 'mailcap', and support programs"
LICENSE = "PD"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=ea156a63df8948d1bb28270e223e93ba"

RRECOMMENDS_${PN} = "file"

PR = "r0"
inherit debian-package
PV = "3.58"

DEBIAN_PATCH_TYPE = "nopatch"

FILES_${PN} += " ${libdir}/mime"

#install follow Debian jessie
do_install () {
	install -d ${D}${sysconfdir}
	install -d ${D}${libdir}/mime/packages
	install -d ${D}${sbindir}
	install -d ${D}${bindir}
	install -m 644 mime.types ${D}${sysconfdir}/
	install -m 644 mailcap ${D}${libdir}/mime/
	install -m 644 mailcap.order ${D}${sysconfdir}/
	install -m 755 update-mime ${D}${sbindir}/
	install -m 755 run-mailcap ${D}${bindir}/
	install -m 755 debian-view ${D}${libdir}/mime/
	install -m 644 mailcap.entries ${D}${libdir}/mime/packages/mime-support
	ln -s run-mailcap ${D}${bindir}/see
	ln -s run-mailcap ${D}${bindir}/edit
	ln -s run-mailcap ${D}${bindir}/compose
	ln -s run-mailcap ${D}${bindir}/print
}
BBCLASSEXTEND = "nativesdk"
