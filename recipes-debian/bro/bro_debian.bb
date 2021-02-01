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

DEBIAN_UNPACK_DIR = "${WORKDIR}/bro-${PV}-minimal"
S = "${WORKDIR}/bro-${PV}-minimal"

inherit autotools cmake

DEPENDS += " \
	bison-native binpac-native bifcl-native \
	binpac libpcap openssl zlib sqlite3 \
	"

# Normally, all required native binaries should be provided in
# native sysroot. However, the packages in ASSUME_PROVIDED such as
# sed-native are always "ignored" by bitbake, which causes that
# cmake (cmake.bbclass) cannot find required binaries in native sysroot,
# even though they exist in host actually.
# Setting OECMAKE_FIND_ROOT_PATH_MODE_PROGRAM to "BOTH" must be
# an only way to avoid that issue.
OECMAKE_FIND_ROOT_PATH_MODE_PROGRAM = "BOTH"

# To avoid the following configuration error:
# | CMake Error: TRY_RUN() invoked in cross-compiling mode, please set the following cache variables appropriately:
# |    OPENSSL_CORRECT_VERSION_NUMBER_EXITCODE (advanced)
# |    OPENSSL_CORRECT_VERSION_NUMBER_EXITCODE__TRYRUN_OUTPUT (advanced)
EXTRA_OECMAKE += "-DOPENSSL_CORRECT_VERSION_NUMBER=TRUE "

# To avaoid the following QA issue:
# ERROR: QA Issue: contains probably-redundant RPATH /usr/lib [useless-rpaths]
EXTRA_OECMAKE += "-DBINARY_PACKAGING_MODE=TRUE "

do_install_append() {
	# Move "site" to the same path as Debian package
	install -d ${D}/${sysconfdir}/bro
	mv ${D}/${datadir}/bro/site ${D}/${sysconfdir}/bro
}
