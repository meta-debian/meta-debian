DESCRIPTION = "internationalized substitute of Text::Wrap"

PR = "r0"

inherit debian-package cpan
PV = "0.06"

LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://README;beginline=44;md5=e66988112aaf864a328fad61627fe75b"

DEBIAN_PATCH_TYPE = "quilt"

do_install_append() {
	install -d ${D}${datadir}/perl5
	mv ${D}${libdir}/perl/vendor_perl/5.20.2/Text \
			 ${D}${datadir}/perl5
	rm -rf ${D}${libdir}
}

FILES_${PN} = "${datadir}"
