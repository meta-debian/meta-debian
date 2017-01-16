SUMMARY = "module to parse and validate simple name/value option pairs"
DESCRIPTION = "Data::OptList is a Perl module useful for working with simple name-and-value \
 option pairs. It assumes any defined scalar is a name and any reference after \
 it is its value."
HOMEPAGE = "https://metacpan.org/release/Data-OptList/"

PR = "r0"
inherit debian-package
PV = "0.109"

LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://README;beginline=8;endline=11;md5=8eeda032abcdf8d7dfcfdc95f6957e92 \
                    file://LICENSE;md5=7cc106d2d05c138ff9f18ccfd6e18b08"
inherit cpan
# source format is 3.0 (quilt) but there is no debian patch
DEBIAN_QUILT_PATCHES = ""

RDEPENDS_${PN} += "libparams-util-perl libsub-install-perl"
