# Bro recipe
SUMMARY = "Passive network traffic analyzer"

DESCRIPTION = "Bro is primarily a security monitor that inspects all traffic on a link in \
depth for signs of suspicious activity. More generally, however, Bro supports a wide range \
of traffic analysis tasks even outside of the security domain, including performance \
measurements and helping with trouble-shooting.\
Bro comes with built-in functionality for a range of analysis and detection tasks, \
including detecting malware by interfacing to external registries, reporting vulnerable \
versions of software seen on the network, identifying popular web applications, detecting \
SSH brute-forcing, validating SSL certificate chains, among others."
  
HOMEPAGE = "http://www.bro.org"

LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=7ffedb422684eb346c1fb5bb8fc5fe45"

inherit debian-package
require recipes-debian/sources/bro.inc
inherit autotools cmake pythonnative 
DEBIAN_UNPACK_DIR = "${WORKDIR}/bro-${PV}-minimal"
S = "${WORKDIR}/bro-${PV}-minimal"

#It will look for and find the binary in sysroot bin directory.
OECMAKE_FIND_ROOT_PATH_MODE_PROGRAM = "BOTH"

DEPENDS += "bind binpac-native openssl-native libpcap-native swig-native  bison-native flex-native zlib-native sed-native"

EXTRA_OECMAKE += "-DOPENSSL_CORRECT_VERSION_NUMBER=TRUE "

SYSROOT_DIRS += "${B}/src"

RDEPENDS_${PN} += "bash bind libpcap openssl zlib python"

BBCLASSEXTEND = "native nativesdk"
