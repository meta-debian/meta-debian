DESCRIPTION = "libintl-perl is an internationalization library for Perl"
SECTION = "libs"
LICENSE = "LGPLv2+"
DEPENDS = "perl" 
PR = "r0"

LIC_FILES_CHKSUM = "file://COPYING.LESSER;md5=d8045f3b8f929c1cb29a1e3fd737b499"

inherit cpan debian-package
PV = "1.23"

do_install_append () {
	install -d ${D}${libdir}/perl5/
	mv ${D}${libdir}/perl/vendor_perl/${PERLVERSION} ${D}${libdir}/perl5/
}

PACKAGES =+ "libintl-xs-perl "
FILES_libintl-xs-perl = " \
	${libdir}/perl5/${PERLVERSION}/Locale/gettext_xs.pm \
	${libdir}/perl5/${PERLVERSION}/auto/Locale/gettext_xs/gettext_xs.so \
    "

FILES_${PN} += " ${libdir}/perl5 "

FILES_${PN}-dbg += "${libdir}/perl5/${PERLVERSION}/auto/Locale/gettext_xs/.debug/"
