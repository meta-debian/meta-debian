#
# base recipe:
#   http://cgit.openembedded.org/meta-openembedded/tree/meta-networking/recipes-support/tcpdump/tcpdump_4.6.1.bb
# base commit: 90880880066981071f14a983c2da9f450f244192
#

PR = "r0"

inherit debian-package
PV = "4.6.2"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1d4b0366557951c84a94fabe3529f867"

DEPENDS = "libcap libcap-native libpcap libpcap-native"

# tcpdump-cross-getaddrinfo.patch:
#	Fix getaddinfo check when cross compiling
SRC_URI += " \
    file://unnecessary-to-check-libpcap.patch \
    file://tcpdump-cross-getaddrinfo.patch \
    file://add-ptest.patch \
    file://run-ptest \
"

export LIBS = " -lpcap"

inherit autotools-brokensep ptest
CACHED_CONFIGUREVARS = "ac_cv_linux_vers=${ac_cv_linux_vers=2}"

PACKAGECONFIG ??= "openssl ipv6"
PACKAGECONFIG[openssl] = "--with-crypto=yes,--without-crypto,openssl"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
PACKAGECONFIG[smi] = "--with-smi,--without-smi,libsmi"

EXTRA_AUTORECONF += " -I m4"

do_configure_prepend() {
	mkdir -p ${S}/m4
	if [ -f aclocal.m4 ]; then
		mv aclocal.m4 ${S}/m4
	fi

	# AC_CHECK_LIB(dlpi.. was looking to host /lib
	sed -i 's:-L/lib::g' ./configure.in
}

do_compile_ptest() {
	oe_runmake buildtest-TESTS
}
