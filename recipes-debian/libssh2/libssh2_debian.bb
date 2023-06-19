SUMMARY = "SSH2 client-side library"
DESCRIPTION = "libssh2 is a client-side C library implementing the SSH2 protocol. \
 It supports regular terminal, SCP and SFTP (v1-v5) sessions; \
 port forwarding, X11 forwarding; password, key-based and \
 keyboard-interactive authentication."
HOMEPAGE = "http://libssh2.org/"

inherit debian-package
PV = "1.4.3"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=d00afe44f336a79a2ca7e1681ce14509 \
"

inherit autotools

DEPENDS += "zlib"

# only one of openssl and gcrypt could be set
PACKAGECONFIG ??= "gcrypt"
PACKAGECONFIG[openssl] = "--with-openssl --with-libssl-prefix=${STAGING_LIBDIR},--without-openssl,openssl"
PACKAGECONFIG[gcrypt] = "--with-libgcrypt --with-libgcrypt-prefix=${STAGING_EXECPREFIXDIR},--without-libgcrypt,libgcrypt"

DEBIANNAME_${PN}-dev = "${DPN}-1-dev"
RPROVIDES_${PN}-dev = "${DPN}-1-dev"

do_install_append() {
	# remove redundant file
	rm -rf ${D}${libdir}/libssh2.la
}
BBCLASSEXTEND = "native nativesdk"
