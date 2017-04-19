SUMMARY = "C library for encoding, decoding and manipulating JSON data"
DESCRIPTION = " Jansson is a C library for encoding, decoding and manipulating JSON data.\n\
 .\n\
 It features:\n\
  * Simple and intuitive API and data model\n\
  * Comprehensive documentation\n\
  * No dependencies on other libraries\n\
  * Full Unicode support (UTF-8)\n\
  * Extensive test suite"
HOMEPAGE = "http://www.digip.org/jansson/"

PR = "r0"
inherit debian-package
PV = "2.7"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=517b9b5519c82636e516e4969c5ce393"

inherit autotools
