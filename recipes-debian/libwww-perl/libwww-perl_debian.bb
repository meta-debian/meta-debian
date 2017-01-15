#
# base recipe: http://cgit.openembedded.org/openembedded/tree/recipes/perl/libwww-perl_5.834.bb
# base commit: c15142c5e3c5556e7a9d534e8b0a5aa175b79a3a
#

SUMMARY = "simple and consistent interface to the world-wide web"
DESCRIPTION = "libwww-perl (also known as LWP) is a collection of Perl modules that provide \
a simple and consistent programming interface (API) to the World-Wide Web. \
The main focus of the library is to provide classes and functions that allow \
you to write WWW clients. It also contains general purpose modules, as well \
as a simple HTTP/1.1-compatible server implementation."
HOMEPAGE = "https://metacpan.org/release/libwww-perl"

PR = "r0"

inherit debian-package
PV = "6.08"

LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://README;beginline=92;endline=98;md5=3da13bc02f8f17ed35ac5d192cae6fe4"

DEPENDS = "liburi-perl-native libhtml-parser-perl-native libhtml-tagset-perl-native"
RDEPENDS_${PN} = " \
    libhtml-parser-perl \
    libhtml-tagset-perl \
    liburi-perl \
    perl-module-digest-md5 \
    perl-module-net-ftp \
"

inherit cpan

do_install_append() {
	# Follow debian/libwww-perl.links
	ln -s lwp-request ${D}${bindir}/GET
	ln -s lwp-request ${D}${bindir}/POST
	ln -s lwp-request ${D}${bindir}/HEAD
}

BBCLASSEXTEND = "native"
PACKAGE_ARCH = "all"
