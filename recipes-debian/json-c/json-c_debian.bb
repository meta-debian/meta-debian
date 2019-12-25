SUMMARY = "JSON manipulation library"
DESCRIPTION = "This library allows you to easily construct JSON objects in C, \
output them as JSON formatted strings and parse JSON formatted \
strings back into the C representation of JSON objects."
HOMEPAGE = "https://github.com/json-c/json-c/wiki"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=de54b60fbbc35123ba193fea8ee216f2"

inherit debian-package
require recipes-debian/sources/json-c.inc
DEBIAN_UNPACK_DIR = "${WORKDIR}/${BPN}-${REPACK_PV}"

inherit autotools

RPROVIDES_${PN} = "libjson"

BBCLASSEXTEND = "native nativesdk"
