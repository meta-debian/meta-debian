#
# base recipe: meta/recipes-devtools/automake/automake_1.16.1.bb
# base branch: master
# base commit: 028a292001f64ad86c6b960a05ba1f6fd72199de
#

require recipes-devtools/automake/automake.inc

inherit debian-package
PV = "1.16.1"
DPR = "-1"
BPN = "automake-1.16"
DSC_URI = "${DEBIAN_MIRROR}/main/a/${BPN}/${BPN}_${PV}${DPR}.dsc;md5sum=95b30e01d980bfa66e71fa707951fc6b"

DEBIAN_UNPACK_DIR = "${WORKDIR}/automake-1.16.1"

LICENSE = "GPLv2+ & GPLv3+ & GFDL-1.3+"
LIC_FILES_CHKSUM = " \
	file://COPYING;md5=751419260aa954499f7abaabaa882bbe \
	file://t/ext3.sh;beginline=2;endline=15;md5=b439006710a0173bbb27c1f522fb536a \
	file://doc/automake.texi;beginline=22;endline=39;md5=25a35cf78fd5b19b0a2579aa3cc02483 \
"

FILESPATH_append = ":${COREBASE}/meta/recipes-devtools/automake/automake"
SRC_URI += " \
	file://python-libdir.patch \
	file://automake-replace-w-option-in-shebangs-with-modern-use-warnings.patch \
"

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

RDEPENDS_${PN}_class-nativesdk = "nativesdk-autoconf"

PERL = "${USRBINPATH}/perl"
PERL_class-nativesdk = "${USRBINPATH}/env perl"

CACHED_CONFIGUREVARS += "ac_cv_path_PERL='${PERL}'"

do_install_append () {
	install -d ${D}${datadir}
}

BBCLASSEXTEND = "nativesdk"
