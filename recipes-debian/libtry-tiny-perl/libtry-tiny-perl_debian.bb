SUMMARY = "module providing minimalistic try/catch"
DESCRIPTION = "\
Try::Tiny is a Perl module that provides bare bones try/catch statements. It \
is designed to eliminate common mistakes with eval blocks, and NOTHING else. \
. \
The main focus of this module is to provide simple and reliable error \
handling for those having a hard time installing TryCatch, but who still want \
to write correct eval blocks without 5 lines of boilerplate each time. \
"
HOMEPAGE = "https://metacpan.org/release/Try-Tiny"
PR = "r0"
inherit debian-package
PV = "0.22"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "\
	file://LICENSE;md5=781485aae6e6d87e2262fc7baa08dcc7"
inherit cpan

FILES_${PN} += "${datadir}/*"
RDEPENDS_${PN} += "libparams-validate-perl"

# source format is 3.0 (quilt) but there is no debian patch
DEBIAN_QUILT_PATCHES = ""
