SUMMARY = "file-finding iterator"
DESCRIPTION = "\
 File::Next is an iterator-based module for finding files.  It's\
 lightweight, has no dependencies, runs under taint mode, and puts your\
 program more directly in control of file selection.\
 .\
 It's taken heavily from Mark Jason Dominus' excellent book \"Higher\
 Order Perl\".  http://hop.perl.plover.com/\
"
SECTION = "perl"
PR = "r0"
LICENSE = "Artistic-2.0"
LIC_FILES_CHKSUM = "file://README.md;beginline=43;md5=2e0692f3ad64251d3d8e0c34dfa0346b"

inherit debian-package cpan allarch
PV = "1.12"

DEBIAN_QUILT_PATCHES = ""

RDEPENDS_${PN} += "perl"
