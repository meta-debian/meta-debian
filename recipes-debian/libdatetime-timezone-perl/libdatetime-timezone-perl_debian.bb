SUMMARY = "framework exposing the Olson time zone database to Perl"
DESCRIPTION = "\
DateTime::TimeZone is a Perl module framework providing an interface to the \
Olson time zone database. It exposes the database as a set of modules, one \
for each time zone defined, allowing for various optimizations in doing time \
zone calculations. \
.\
The Olson time zone database is the best available source for worldwide time \
zone information and is available from <URL:ftp://ftp.iana.org/tz/releases/>. \
"
HOMEPAGE = "http://datetime.perl.org/"
PR = "r0"
inherit debian-package
PV = "1.75"

LICENSE = "Artistic-1.0 | GPL-1+"
LIC_FILES_CHKSUM = "\
	file://LICENSE;md5=8cb5990fa1f243cd63c97cfd08571fe7"
inherit cpan

do_install_append() {
	rm ${D}${PERLLIBDIRS}/vendor_perl/*/DateTime/TimeZone/Local/Win32.pm
}
RDEPENDS_${PN} += "libparams-validate-perl libclass-load-perl libclass-singleton-perl"
