SUMMARY = "module providing routines for manipulating stashes"
DESCRIPTION = "Package::Stash is a Perl module that provides an interface for manipulating \
 stashes (Perl's symbol tables). These operations are occasionally necessary,\
 but often very messy and easy to get wrong."
HOMEPAGE = "https://metacpan.org/release/Package-Stash"

PR = "r0"
inherit debian-package
PV = "0.37"

LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1c982bf619f6e6c6c2ccd554ff3b34e2"
inherit cpan

RDEPENDS_${PN} += "libmodule-implementation-perl"
