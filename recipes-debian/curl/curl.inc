#
# base recipe: meta/recipes-support/curl/curl_7.38.0.bb
# base commit: bcff2a7a69f4d9e493e5a2d22ba2b3d5023138cb
#

SUMMARY = "Command line tool and library for client-side URL transfers"
HOMEPAGE = "http://curl.haxx.se/"
BUGTRACKER = "http://curl.haxx.se/mail/list.cgi?list=curl-tracker"

INC_PR = "r1"
DPN = "curl"

inherit debian-package
PV = "7.38.0"

LICENSE = "MIT & ISC & BSD-3-Clause & BSD-4-Clause"
LIC_FILES_CHKSUM = " \
	file://COPYING;beginline=7;md5=3a34942f4ae3fbf1a303160714e664ac \
	file://lib/inet_pton.c;endline=17;md5=93e66258959eb3c95b8bcd3752e183e5 \
	file://lib/krb5.c;endline=33;md5=f993c95df7bcfe8d38bcf27c90b860c2 \
	file://tests/server/tftpd.c;endline=48;md5=d0619389d1ab7a9883416c118cb372f1 \
"

# curl likes to set -g0 in CFLAGS, so we stop it
# from mucking around with debug options
#
SRC_URI += " \
	file://configure_ac.patch \
	file://pkgconfig_fix.patch \
	file://correct-path-to-krb5-config_debian.patch \
"

inherit autotools pkgconfig multilib_header
DEPENDS_class-target += "krb5"
EXTRA_OECONF = " \
	--without-libidn \
	--enable-crypto-auth \
	--disable-ldap \
	--disable-ldaps \
	--with-ca-bundle=${sysconfdir}/ssl/certs/ca-certificates.crt \
"

# Follow debian/rules
EXTRA_OECONF += " \
	--disable-dependency-tracking \
	--disable-symbol-hiding \
	--enable-versioned-symbols \
	--enable-threaded-resolver \
	--with-lber-lib=lber \
"
EXTRA_OECONF_class-target += "--with-gssapi=${STAGING_LIBDIR}/../"
