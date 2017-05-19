#
# base recipe: meta/recipes-devtools/automake/automake_1.14.bb
# base branch: daisy
#

PR = "r0"

inherit debian-package
PV = "1.14.1"

DPN = "automake-1.14"

LICENSE = "GPLv2+ & GPLv3+ & GFDL-1.3+"
LIC_FILES_CHKSUM = " \
	file://COPYING;md5=751419260aa954499f7abaabaa882bbe \
	file://t/ext3.sh;beginline=2;endline=15;md5=acd264ffa44ba4128333c4a9501d0532 \
	file://doc/automake.info;beginline=8;endline=15;md5=c865c89fa5e4f14520fe3f480b07b4f1 \
"

# for native package
SRC_URI += " \
	file://python-libdir.patch \
	file://py-compile-compile-only-optimized-byte-code.patch \
	file://buildtest.patch \
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

# regenerate dependent files created by aclocal and automake
do_configure_prepend() {
	cd ${S}
	./bootstrap.sh && cd -
}

inherit autotools

export AUTOMAKE = "${@bb.utils.which('automake', d.getVar('PATH', True))}"
NAMEVER = "${@oe.utils.trim_version("${PV}", 2)}"

do_configure() {
	oe_runconf
}

do_install_append () {
	install -d ${D}${datadir}

	# Some distros have both /bin/perl and /usr/bin/perl, but we set perl location
	# for target as /usr/bin/perl, so fix it to /usr/bin/perl.
	for i in aclocal aclocal-${NAMEVER} automake automake-${NAMEVER}; do
		if [ -f ${D}${bindir}/$i ]; then
			sed -i -e '1s,#!.*perl,#! ${USRBINPATH}/perl,' \
			-e 's,exec .*/bin/perl \(.*\) exec .*/bin/perl \(.*\),exec ${USRBINPATH}/perl \1 exec ${USRBINPATH}/perl \2,' \
			${D}${bindir}/$i
		fi
	done
}

FILES_${PN} += "${datadir}/automake* ${datadir}/aclocal*"
BBCLASSEXTEND = "native nativesdk"
