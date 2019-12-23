#
# base recipe: meta/recipes-extended/libidn/libidn_1.35.bb
# base branch: warrior
#
SUMMARY = "Internationalized Domain Name support library"
DESCRIPTION = " GNU Libidn is a fully documented implementation of the Stringprep, \
 Punycode and IDNA specifications.  Libidn's purpose is to encode and \
 decode internationalized domain names.  The Nameprep, XMPP, SASLprep, \
 and iSCSI profiles are supported."
HOMEPAGE = "http://www.gnu.org/software/libidn/"

LICENSE = "(GPLv2+ | LGPLv3+) & GPLv3+ & LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=df4be47940a91ee69556f5f71eed4aec \
                    file://COPYING.LESSERv2;md5=4fbd65380cdd255951079008b364516c \
                    file://COPYING.LESSERv3;md5=e6a600fd5e1d9cbde2d983680233ad02 \
                    file://COPYINGv2;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYINGv3;md5=d32239bcb673463ab874e80d47fae504 \
                    file://lib/idna.h;endline=21;md5=37cffad24807f446a24de3e7371f20b9 \
                    file://src/idn.c;endline=20;md5=09e97034a8877b3451cb65065fc2c06e"


inherit debian-package
require recipes-debian/sources/libidn.inc

DEPENDS = "virtual/libiconv autoconf-archive"

inherit pkgconfig autotools gettext texinfo gtk-doc

FILESEXTRAPATHS =. "${COREBASE}/meta/recipes-extended/libidn/libidn:"

SRC_URI += "file://dont-depend-on-help2man.patch \
            file://0001-idn-format-security-warnings.patch \
           "

# command tool is under GPLv3+, while libidn itself is under LGPLv2.1+ or LGPLv3
# so package command into a separate package
PACKAGES =+ "idn"
FILES_idn = "${bindir}/*"

LICENSE_${PN} = "GPLv2+ | LGPLv3+"
LICENSE_idn = "GPLv3+"

EXTRA_OECONF = "--disable-csharp"

do_install_append() {
	rm -rf ${D}${datadir}/emacs
}

BBCLASSEXTEND = "native nativesdk"
