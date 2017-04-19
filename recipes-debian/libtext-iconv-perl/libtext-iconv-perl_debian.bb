# base recipe: http://cgit.openembedded.org/cgit.cgi/meta-openembedded/\
#tree/meta-perl/recipes-perl/libtext/libtext-iconv-perl_1.7.bb?h=master 
# base branch: master

SUMMARY = "Perl interface to iconv() codeset conversion function"
DESCRIPTION = "\
The Text::Iconv module provides a Perl interface to the iconv() \
function as defined by the Single UNIX Specification. The convert() \
method converts the encoding of characters in the input string from \
the fromcode codeset to the tocode codeset, and returns the result. \
Settings of fromcode and tocode and their permitted combinations are \
implementation-dependent. Valid values are specified in the system \
documentation."

PR = "r0"
inherit debian-package
PV = "1.7"

LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://README;beginline=3;endline=6;md5=fadf2919c7128e887d26b4d905f90649"

# source format is 3.0 (quilt) but there is no debian patch
DEBIAN_QUILT_PATCHES = ""

inherit cpan

FILES_${PN}-dbg += "${libdir}/perl/vendor_perl/*/auto/Text/Iconv/.debug/"
