SUMMARY = "Fast ASN.1 parser and serializer"
DESCRIPTION = "asn1crypto focuses on: \
 - Delayed parsing of byte string values \
 - Persistence of original ASN.1 encoded data until a value is changed \
 - Lazy loading of child fields \
 - Utilization of high-level Python stdlib modules \
"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=52010cd3c7d7bd965b55721ef4d93ec2"

inherit debian-package
require recipes-debian/sources/asn1crypto.inc
DEBIAN_UNPACK_DIR = "${WORKDIR}/asn1crypto-${PV}"

# There is no patch
DEBIAN_QUILT_PATCHES = ""

do_install_append() {
	find ${D} -name *.pyc -delete
}
