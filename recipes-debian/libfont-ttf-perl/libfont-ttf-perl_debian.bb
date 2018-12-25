SUMMARY = "Perl module for TrueType font hacking"
DESCRIPTION = "Font::TTF module supports reading, processing and writing many different \
table formats for TrueType fonts. You can do almost anything with a \
TrueType font with this module."
HOMEPAGE = "https://metacpan.org/release/Font-TTF/"

inherit debian-package
PV = "1.04"

LICENSE = "Artistic-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ed576aa4482ed73ca38441879ec3e83e"

DEPENDS = "libio-string-perl"

inherit allarch cpan

RDEPENDS_${PN} += "libio-string-perl"

BBCLASSEXTEND = "native"
