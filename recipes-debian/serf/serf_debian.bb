SUMMARY = "high-performance asynchronous HTTP client library"
DESCRIPTION = "serf library is a C-based HTTP client library built upon the Apache \
 Portable Runtime (APR) library.  It multiplexes connections, running the \
 read/write communication asynchronously.  Memory copies and \
 transformations are kept to a minimum to provide high performance \
 operation."
HOMEPAGE = "http://code.google.com/p/serf/"

PR = "r0"
inherit debian-package
PV = "1.3.8"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

# Apply debian patch by quilt
DEBIAN_PATCH_TYPE = "quilt"

SRC_URI += "file://env.patch"

DEPENDS = "scons-native apr apr-util chrpath-native openssl zlib krb5"
RDEPENDS_${PN}-dev += "apr-dev libaprutil1-dev"
do_compile() {
	${STAGING_BINDIR_NATIVE}/scons ${PARALLEL_MAKE} PREFIX=${prefix} \
		CC="${CC}" \
		APR=`which apr-1-config` APU=`which apu-1-config` \
		CFLAGS="${CFLAGS}" LINKFLAGS="${LDFLAGS}" \
		OPENSSL="${STAGING_EXECPREFIXDIR}"
}

do_install() {
	${STAGING_BINDIR_NATIVE}/scons PREFIX=${D}${prefix} LIBDIR=${D}${libdir} install
	chrpath -d ${D}${libdir}/*.so.*
	# Remove the the absolute path to sysroot
	sed -i -e "s|${STAGING_LIBDIR}|${libdir}|" \
		${D}${libdir}/pkgconfig/*.pc
}
DEBIANNAME_${PN}-dev = "lib${PN}-dev"
RPROVIDES_${PN}-dev += "lib${PN}-dev"
RPROVIDES_${PN} += "lib${PN}-1-1"
