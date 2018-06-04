#
# base recipe: meta/recipes-devtools/automake/automake_1.15.bb
# base branch: master
# base commit: d886fa118c930d0e551f2a0ed02b35d08617f746
#

SUMMARY = "Tool for generating GNU Standards-compliant Makefiles"
DESCRIPTION = "Automake is a tool for automatically generating `Makefile.in's from\n\
files called `Makefile.am'.\n\
.\n\
The goal of Automake is to remove the burden of Makefile maintenance\n\
from the back of the individual GNU maintainer (and put it on the back\n\
of the Automake maintainer).\n\
.\n\
The `Makefile.am' is basically a series of `make' macro definitions\n\
(with rules being thrown in occasionally).  The generated\n\
`Makefile.in's are compliant with the GNU Makefile standards."
HOMEPAGE = "https://www.gnu.org/software/automake/"

inherit debian-package
PV = "1.15.1"

DPN = "automake-1.15"

LICENSE = "GPLv2+ & GPLv3+ & GFDL-1.3+"
LIC_FILES_CHKSUM = " \
	file://COPYING;md5=751419260aa954499f7abaabaa882bbe \
	file://t/ext3.sh;beginline=2;endline=15;md5=91e9e4681cdb2948e3cc91b4fbe6d096 \
	file://doc/automake.info;beginline=8;endline=15;md5=5ab793806113a41dbf71ef679a1518a3 \
"

SRC_URI += " \
	file://python-libdir.patch \
	file://py-compile-compile-only-optimized-byte-code.patch \
	file://buildtest.patch \
	file://automake-replace-w-option-in-shebangs-with-modern-use-warnings.patch \
	file://config-HELP2MAN.patch \
"

DEPENDS_class-native = "autoconf-native"

RDEPENDS_${PN} += "\
	autoconf \
	perl \
	perl-module-bytes \
	perl-module-data-dumper \
	perl-module-strict \
	perl-module-text-parsewords \
	perl-module-thread-queue \
	perl-module-threads \
	perl-module-vars \
"
RDEPENDS_${PN}_class-native = "autoconf-native hostperl-runtime-native"
RDEPENDS_${PN}_class-nativesdk = "nativesdk-autoconf"

# regenerate dependent files created by aclocal and automake
do_configure_prepend() {
	( cd ${S}
	./bootstrap )
}

inherit autotools texinfo

export AUTOMAKE = "${@bb.utils.which('automake', d.getVar('PATH', True))}"
NAMEVER = "${@oe.utils.trim_version("${PV}", 2)}"

PERL = "${USRBINPATH}/perl"
PERL_class-native = "${USRBINPATH}/env perl"
PERL_class-nativesdk = "${USRBINPATH}/env perl"

CACHED_CONFIGUREVARS += "ac_cv_path_PERL='${PERL}'"

do_configure() {
	oe_runconf
}

do_install_append () {
	install -d ${D}${datadir}
}

FILES_${PN} += "${datadir}/automake* ${datadir}/aclocal*"
BBCLASSEXTEND = "native nativesdk"
