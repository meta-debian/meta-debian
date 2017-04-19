#
# base recipe: meta/recipes-connectivity/openssl/openssl_1.0.1m.bb
# base branch: daisy
#

PR = "r1"

inherit debian-package
PV = "1.0.1t"

# "openssl | SSLeay" dual license
LICENSE = "openssl"
LIC_FILES_CHKSUM = "file://LICENSE;md5=27ffa5d74bb5a337056c14b2ef93fbf6"

# configure-targets.patch: provides targets needed by do_configure
# shared-libs.patch: for cross-build, use environment variables in Makefiles
# oe-ldflags.patch: for cross-build, works together with ${OE_}
# find.sh: wrapper script; perl4 scripts require this, but perl5 doesn't have
SRC_URI += " \
file://configure-targets.patch \
file://shared-libs.patch \
file://oe-ldflags.patch \
file://find.pl \
"

# "${S}/Configure" is written by perl script
DEPENDS = "hostperl-runtime-native"

# CFLAG replaces the second parameters (next to gcc:) of the target config
# in Configure (see do_configure). We simply use the same options as OE-Core
# for supporting cross-build with sysroots.
CFLAG = "${@base_conditional('SITEINFO_ENDIANNESS', 'le', '-DL_ENDIAN', '-DB_ENDIAN', d)} \
	-DTERMIO ${CFLAGS} -Wall -Wa,--noexecstack"
CFLAG_mtx-1 := "${@'${CFLAG}'.replace('-O2', '')}"
CFLAG_mtx-2 := "${@'${CFLAG}'.replace('-O2', '')}"

# comes from "CONFARGS" in debian/rules
EXTRA_OECONF = " \
--openssldir=${libdir}/ssl \
no-idea no-mdc2 no-rc5 no-zlib enable-tlsext no-ssl2 no-ssl3 \
"
EXTRA_OECONF_append_x86-64 = "enable-ec_nistp_64_gcc_128"

# without this, ar commands fail in do_compile
AR_append = " r"
EXTRA_OEMAKE = "-e MAKEFLAGS="
# Only "test" is removed from the default targets because it includes
# running tests. Instead, "buildtest" and "runtest" are added by
# Makefiles-ptest.patch as new targets
export DIRS = "crypto ssl engines apps tools"

# works together with oe-ldflags.patch
export OE_LDFLAGS="${LDFLAGS}"

# according to OE-core, parallel build and install are not supported
PARALLEL_MAKE = ""
PARALLEL_MAKEINST = ""

# Configure only sets MAKEDEPPROG to $cc if $cc is "gcc"
# but CC in bitbake doesn't match the value Configure assumes,
# so we need set it here.
EXTRA_OEMAKE += "'MAKEDEPPROG=${CC}'"

# In "build-stamp" in debian/rules, "Configure" and "make" are called
# at least more than one time for building "no-shared", "shared", and
# optional "shared" targets. However, do_configure calls them for
# building "shared" target only once.
do_configure () {
	# perlpath.pl requires this, but not provided by perl5
	cp ${WORKDIR}/find.pl ${S}/util/find.pl

	# replace the perl path in all scripts by sysroots
	cd util
	perl perlpath.pl ${STAGING_BINDIR_NATIVE}
	cd ..

	# get "target" from ${HOST_OS} and ${HOST_ARCH}
	# "target" is used as an argument of "Configure"
	os=${HOST_OS}
	case $os in
	linux-uclibc |\
	linux-uclibceabi |\
	linux-gnueabi |\
	linux-uclibcspe |\
	linux-gnuspe |\
	linux-musl*)
		os=linux
		;;
		*)
		;;
	esac
	target="$os-${HOST_ARCH}"
	case $target in
	linux-arm)
		target=linux-armv4
		;;
	linux-armeb)
		target=linux-elf-armeb
		;;
	linux-aarch64*)
		target=linux-generic64
		;;
	linux-sh3)
		target=debian-sh3
		;;
	linux-sh4)
		target=debian-sh4
		;;
	linux-i486)
		target=debian-i386-i486
		;;
	linux-i586 | linux-viac3)
		target=debian-i386-i586
		;;
	linux-i686)
		target=debian-i386-i686/cmov
		;;
	linux-gnux32-x86_64)
		target=linux-x32
		;;
	linux-gnu64-x86_64)
		target=linux-x86_64
		;;
	linux-mips)
		target=debian-mips
		;;
	linux-mipsel)
		target=debian-mipsel
		;;
        linux-*-mips64)
               target=linux-mips
                ;;
	linux-powerpc)
		target=linux-ppc
		;;
	linux-powerpc64)
		target=linux-ppc64
		;;
	linux-supersparc)
		target=linux-sparcv8
		;;
	linux-sparc)
		target=linux-sparcv8
		;;
	darwin-i386)
		target=darwin-i386-cc
		;;
	esac
	# inject machine-specific flags
	sed -i -e "s|^\(\"$target\",\s*\"[^:]\+\):\([^:]\+\)|\1:${CFLAG}|g" Configure
	useprefix=${prefix}
	if [ "x$useprefix" = "x" ]; then
		useprefix=/
	fi        

	# ${EXTRA_OECONF} follows debian/rules
	# $useprefix and $target are set by the above commands
	# The path "prefix" must be excluded from "libdir"
	perl ./Configure ${EXTRA_OECONF} shared --prefix=$useprefix \
		--libdir=$(basename ${libdir}) $target
}

do_compile() {
	# Directory ${S}/include is empty,
	# so it is required to run "make depend" for necessary headers.
	# https://github.com/openssl/openssl/issues/492
	oe_runmake depend
	oe_runmake
}

do_install() {
	oe_runmake INSTALL_PREFIX="${D}" MANDIR="${mandir}" install

	# comes from "binary-arch" in debian/rules
	install -d ${D}${sysconfdir}/ssl
	for f in certs openssl.cnf private; do
		mv ${D}${libdir}/ssl/${f} ${D}${sysconfdir}/ssl
		ln -s ${sysconfdir}/ssl/${f} ${D}${libdir}/ssl/${f}
	done
}

PACKAGES =+ "libssl1.0.0"

FILES_${PN} += "${libdir}/ssl"
FILES_${PN}-dbg += "${libdir}/openssl-1.0.0/engines/.debug"
FILES_libssl1.0.0 = " \
${libdir}/libssl.so.* \
${libdir}/libcrypto.so.* \
${libdir}/openssl-1.0.0/engines/*.so \
"

BBCLASSEXTEND = "native nativesdk"

inherit ptest

SRC_URI += " \
file://Makefiles-ptest.patch \
file://ptest-deps.patch \
file://run-ptest \
"

do_compile_ptest () {
	oe_runmake buildtest
}

do_install_ptest () {
	cp -r Makefile test ${D}${PTEST_PATH}
	cp -r certs ${D}${PTEST_PATH}
	mkdir -p ${D}${PTEST_PATH}/apps
	ln -sf /usr/lib/ssl/misc/CA.sh  ${D}${PTEST_PATH}/apps
	ln -sf /usr/lib/ssl/openssl.cnf ${D}${PTEST_PATH}/apps
	ln -sf /usr/bin/openssl         ${D}${PTEST_PATH}/apps
	cp apps/server2.pem             ${D}${PTEST_PATH}/apps
	mkdir -p ${D}${PTEST_PATH}/util
	install util/opensslwrap.sh    ${D}${PTEST_PATH}/util
	install util/shlib_wrap.sh     ${D}${PTEST_PATH}/util
}

RDEPENDS_${PN}-ptest += "make perl perl-module-filehandle bc"
