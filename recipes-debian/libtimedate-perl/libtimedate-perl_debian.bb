#
# base recipe:
#	http://cgit.openembedded.org/cgit.cgi/openembedded-core/tree/meta/recipes-extended/perl/libtimedate-perl_2.30.bb
#

PR = "r0"

inherit debian-package
PV = "2.3000"

LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://README;beginline=21;md5=576b7cb41e5e821501a01ed66f0f9d9e"

inherit cpan

RDEPENDS_${PN}_class-native = ""
RDEPENDS_${PN} += "perl-module-carp perl-module-exporter perl-module-strict perl-module-time-local"

BBCLASSEXTEND = "native"
