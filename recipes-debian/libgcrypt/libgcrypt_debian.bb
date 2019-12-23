# base recipe: meta/recipes-support/libgcrypt/libgcrypt_1.8.4.bb
# base branch: warrior

SUMMARY = "General purpose cryptographic library based on the code from GnuPG"
HOMEPAGE = "http://directory.fsf.org/project/libgcrypt/"

# helper program gcryptrnd and getrandom are under GPL, rest LGPL
LICENSE = "GPLv2+ & LGPLv2.1+ & GPLv3+"
LICENSE_${PN} = "LGPLv2.1+"
LICENSE_${PN}-dev = "GPLv2+ & LGPLv2.1+"
LICENSE_dumpsexp-dev = "GPLv3+"

LIC_FILES_CHKSUM = " \
    file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
    file://COPYING.LIB;md5=bbb461211a33b134d42ed5ee802b37ff \
    file://LICENSES;md5=840e3bcb754e5046ffeda7619034cbd8 \
    file://src/dumpsexp.c;endline=16;md5=3134b0d35419b6af066b890fde4b6e05 \
"


inherit debian-package
require recipes-debian/sources/libgcrypt20.inc

FILESPATH_append = ":${COREBASE}/meta/recipes-support/libgcrypt/files"
SRC_URI += " \
    file://0001-Add-and-use-pkg-config-for-libgcrypt-instead-of-conf.patch \
    file://0002-libgcrypt-fix-building-error-with-O2-in-sysroot-path.patch \
    file://0004-tests-Makefile.am-fix-undefined-reference-to-pthread.patch \
"

DEPENDS = "libgpg-error"

inherit autotools texinfo binconfig-disabled pkgconfig

BINCONFIG = "${bindir}/libgcrypt-config"

EXTRA_OECONF = "--disable-asm"
EXTRA_OEMAKE_class-target = "LIBTOOLFLAGS='--tag=CC'"

PACKAGECONFIG ??= "capabilities"
PACKAGECONFIG[capabilities] = "--with-capabilities,--without-capabilities,libcap"

do_configure_prepend() {
	rm -f ${S}/m4/gpg-error.m4
}

# libgcrypt.pc is added locally and thus installed here
do_install_append() {
	install -d ${D}/${libdir}/pkgconfig
	install -m 0644 ${B}/src/libgcrypt.pc ${D}/${libdir}/pkgconfig/
}

PACKAGES =+ "dumpsexp-dev"

FILES_${PN}-dev += "${bindir}/hmac256"
FILES_dumpsexp-dev += "${bindir}/dumpsexp"

BBCLASSEXTEND = "native nativesdk"
