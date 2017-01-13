SUMMARY = "Perl module for encoding and decoding ASN.1 data structures"
DESCRIPTION = "Convert::ASN1 will parse ASN.1 descriptions and will encode from and decode to \
perl data structures using a hierarchy of references."
HOMEPAGE = "https://metacpan.org/release/Convert-ASN1"

PR = "r0"

inherit debian-package
PV = "0.27"

LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5dcf3d33a7661af01c7deb0bf01f2c1a"

# source format is 3.0 (quilt) but there is no debian patch
DEBIAN_QUILT_PATCHES = ""

inherit cpan

EXTRA_PERLFLAGS = "-I ${PERLHOSTLIB}"

BBCLASSEXTEND = "native"
