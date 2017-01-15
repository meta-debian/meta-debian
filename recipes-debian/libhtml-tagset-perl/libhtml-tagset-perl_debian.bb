SUMMARY = "Data tables pertaining to HTML"
DESCRIPTION = "HTML-Tagset contains data tables useful in dealing with HTML.  For instance, \
it provides %HTML::Tagset::emptyElement, which lists all of the HTML elements \
which cannot have content.  It provides no functions or methods."
HOMEPAGE = "http://search.cpan.org/dist/HTML-Tagset/"

PR = "r0"

inherit debian-package
PV = "3.20"

LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://README;beginline=60;md5=16ddda2d845a5546f615e6b122d1dbad"

DEBIAN_PATCH_TYPE = "quilt"

inherit cpan

BBCLASSEXTEND = "native"
PACKAGE_ARCH = "all"
