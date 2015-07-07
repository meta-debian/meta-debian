SRC_URI = "${GNU_MIRROR}/wget/wget-${PV}.tar.gz \
           file://fix_makefile.patch \
           file://0001-Unset-need_charset_alias-when-building-for-musl.patch \
          "

SRC_URI[md5sum] = "f61d9011b99f824106a5d5a05dd0f63d"
SRC_URI[sha256sum] = "9f1c6d09d7148c1c2d9fd0ea655dcf4dcc407deb2db32d4126251ca0245cb670"

require wget.inc

#
#Meta-debian
#
inherit debian-package
DPR = "0"
DEPENDS_remove = "gnutls"
RRECOMMENDS_${PN}_remove = "ca-certificates"
EXTRA_OECONF = "--enable-ipv6 --without-ssl --disable-rpath --disable-iri \
                --without-libgnutls-prefix ac_cv_header_uuid_uuid_h=no"
