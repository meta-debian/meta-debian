#
# base recipe: http://git.yoctoproject.org/cgit/cgit.cgi/meta-cgl/
# tree/meta-cgl-common/recipes-perl/perl/libmailtools-perl_2.13.bb
# base branch: master
#

DESCRIPTION = "MailTools is a set of Perl modules related to mail applications"

HOMEPAGE = "http://search.cpan.org/dist/MailTools/"

inherit debian-package
PV = "2.13"

PR = "r0"

LICENSE = "Artistic-1.0 & GPLv2"
LIC_FILES_CHKSUM = " \
	file://debian/copyright;md5=01bd72ab113f3f60f175fa1821e6462c \
"

DEPENDS = " \
	libtimedate-perl-native \
	"
RDEPENDS_${PN} += " \
	libtimedate-perl \
	perl-module-io-handle \
	perl-module-net-smtp \
	perl-module-test-more \
	"
BBCLASSEXTEND = "native"

inherit cpan
