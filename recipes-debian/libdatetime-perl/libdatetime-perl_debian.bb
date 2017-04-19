SUMMARY = "module for manipulating dates, times and timestamps"
DESCRIPTION = "\
DateTime is a Perl module which aims to provide a complete, correct, and easy \
to use date/time object implementation. It provides an easy way to manipulate \
dates and times, including date calculations (even addition and subtraction) \
and provides convenient methods for extracting or modifying portions of any \
date or time. \
. \
This module supports the Olson time zone database, meaning that historical \
time zone information, and more importantly, daylight saving time rules, can \
be handled transparently, simply by setting the correct time zone. This is \
done by using the DateTime::TimeZone module. \
"
HOMEPAGE = "http://datetime.perl.org/"
PR = "r0"
inherit debian-package
PV = "1.12"

LICENSE = "Artistic-2.0"
LIC_FILES_CHKSUM = "\
	file://LICENSE;md5=1d2e1c522435425bc51123e3d4782081"
inherit cpan_build

RDEPENDS_${PN} += "\
	libparams-validate-perl libdatetime-locale-perl \
	libdatetime-timezone-perl libtry-tiny-perl"

# source format is 3.0 (quilt) but there is no debian patch
DEBIAN_QUILT_PATCHES = ""

FILES_${PN} += "${libdir}/*"
FILES_${PN}-dbg += "${libdir}/perl5/*/auto/DateTime/.debug"
