#
# base recipe:
#   http://cgit.openembedded.org/meta-openembedded/tree/meta-networking/recipes-support/tcpdump/tcpdump_4.9.3.bb
# base commit: a24acf94d48d635eca668ea34598c6e5c857e3f8
#

PR = "r0"

inherit debian-package
PV = "4.9.3"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1d4b0366557951c84a94fabe3529f867"

DEPENDS = "libpcap libpcap-native"

RDEPENDS_${PN}-ptest += " make perl \
	perl-module-file-basename \
	perl-module-posix \
	perl-module-carp"

SRC_URI += " \
    file://unnecessary-to-check-libpcap.patch \
    file://avoid-absolute-path-when-searching-for-libdlpi.patch \
    file://add-ptest.patch \
    file://run-ptest \
"

inherit autotools-brokensep ptest

PACKAGECONFIG ?= "openssl"

PACKAGECONFIG[libcap-ng] = "--with-cap-ng,--without-cap-ng,libcap-ng"
PACKAGECONFIG[openssl] = "--with-crypto,--without-crypto,openssl"
PACKAGECONFIG[smi] = "--with-smi,--without-smi,libsmi"
# Note: CVE-2018-10103 (SMB - partially fixed, but SMB printing disabled)
PACKAGECONFIG[smb] = "--enable-smb,--disable-smb"

EXTRA_AUTORECONF += "-I m4"

do_configure_prepend() {
	mkdir -p ${S}/m4
	if [ -f aclocal.m4 ]; then
		mv aclocal.m4 ${S}/m4
	fi
}

do_compile_ptest() {
	oe_runmake buildtest-TESTS
}
