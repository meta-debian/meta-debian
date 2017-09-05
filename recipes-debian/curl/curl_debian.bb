#
# base recipe: meta/recipes-support/curl/curl_7.38.0.bb
# base commit: bcff2a7a69f4d9e493e5a2d22ba2b3d5023138cb
#

require curl.inc

PR = "${INC_PR}.0"

inherit binconfig

PACKAGECONFIG ??= "${@bb.utils.contains("DISTRO_FEATURES", "ipv6", "ipv6", "", d)} gnutls zlib"
PACKAGECONFIG_class-native = "ipv6 ssl zlib"
PACKAGECONFIG_class-nativesdk = "ipv6 ssl zlib"

PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
PACKAGECONFIG[ssl] = "--with-ssl --with-random=/dev/urandom,--without-ssl,openssl"
PACKAGECONFIG[gnutls] = "--with-gnutls,--without-gnutls,gnutls"
PACKAGECONFIG[zlib] = "--with-zlib=${STAGING_LIBDIR}/../,--without-zlib,zlib"
PACKAGECONFIG[rtmpdump] = "--with-librtmp,--without-librtmp,rtmpdump"
PACKAGECONFIG[libssh2] = "--with-libssh2,--without-libssh2,libssh2"

# Remove patch of gnutls and nss, we will built them separately
do_debian_patch_prepend(){
	sed -i '/^90_gnutls.patch/d' ${DEBIAN_QUILT_PATCHES}/series
	sed -i '/^99_nss.patch/d' ${DEBIAN_QUILT_PATCHES}/series
	rm ${DEBIAN_QUILT_PATCHES}/90_gnutls.patch
	rm ${DEBIAN_QUILT_PATCHES}/99_nss.patch
}

do_install_append() {
	oe_multilib_header curl/curlbuild.h
}

PACKAGES =+ "lib${DPN} lib${DPN}-openssl-dev lib${DPN}-staticdev lib${DPN}-doc"

FILES_lib${DPN} = "${libdir}/lib*.so.*"
RRECOMMENDS_lib${DPN} += "ca-certificates"
FILES_lib${DPN}-openssl-dev = "${libdir}/lib*.so \
                      ${libdir}/lib*.la \
                      ${datadir}/aclocal"
FILES_lib${DPN}-staticdev = "${libdir}/lib*.a"
FILES_lib${DPN}-doc = "${mandir}/man3 \
                      ${mandir}/man1/curl-config.1"
FILES_${PN}-dev += "${bindir}/*-config"

RDEPENDS_lib${DPN}-openssl-dev += "${PN}-dev"

DEBIANNAME_lib${DPN}-openssl-dev = "lib${DPN}4-openssl-dev"
RPROVIDES_lib${DPN}-openssl-dev = "lib${DPN}4-openssl-dev"
RCONFLICTS_lib${DPN}-openssl-dev = "libcurl4-nss-dev libcurl4-gnutls-dev"

DEBIANNAME_lib${DPN} = "libcurl3"
RPROVIDES_lib${DPN} += "libcurl3"

BBCLASSEXTEND = "native nativesdk"
