SUMMARY = "Perl extension providing localization support for DateTime"
DESCRIPTION = "\
DateTime::Locale extends DateTime by providing localization support. It also \
provides some functions for getting information on available locales and is \
easily customizable through the addition of new locales. \
"
HOMEPAGE = "http://datetime.perl.org/"
PR = "r0"
inherit debian-package
PV = "0.45"

LICENSE = "Artistic-1.0 | GPL-1+"
LIC_FILES_CHKSUM = "\
	file://LICENSE;md5=a89fc6431f978476bd49e3f7a26a1a1e"
inherit cpan_build

RDEPENDS_${PN} += "libparams-validate-perl"
