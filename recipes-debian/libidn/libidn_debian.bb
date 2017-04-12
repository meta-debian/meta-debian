#
# base recipe: meta/recipes-extended/libidn/libidn_1.30.bb
# base branch: jethro
#

PR = "r1"

SUMMARY = "Internationalized Domain Name support library"
DESCRIPTION = "Implementation of the Stringprep, Punycode and IDNA \
specifications defined by the IETF Internationalized Domain Names (IDN) \
working group."
HOMEPAGE = "http://www.gnu.org/software/libidn/"

inherit debian-package autotools-brokensep
PV = "1.29"

DEPENDS = "gengetopt-native texinfo-native fastjar-native"

LICENSE = "(LGPLv2.1+ | LGPLv3) & GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=b35f8839295dd730a55f1a19ec086217 \
                    file://COPYING.LESSERv2;md5=4fbd65380cdd255951079008b364516c \
                    file://COPYING.LESSERv3;md5=e6a600fd5e1d9cbde2d983680233ad02 \
                    file://COPYINGv2;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
		    file://COPYINGv3;md5=d32239bcb673463ab874e80d47fae504 \
"
inherit pkgconfig autotools gettext texinfo

SRC_URI += "file://disable-doc.patch \
	    file://Makefile-src.patch \
            "

EXTRA_OECONF += "--enable-gtk-doc-html=no"

PARALLEL_MAKE = ""

# command tool is under GPLv3+, while libidn itself is under LGPLv2.1+ or LGPLv3
# so package command into a separate package
PACKAGES =+ "idn"

FILES_${PN} += "${libdir}"
FILES_idn = "${bindir}/idn ${datadir}/emacs"

DEBIANNAME_${PN}-dev = "${PN}11-dev"

do_compile_prepend() {
        export LD_LIBRARY_PATH=${STAGING_LIBDIR}:$LD_LIBRARY_PATH
}

BBCLASSEXTEND = "nativesdk"
