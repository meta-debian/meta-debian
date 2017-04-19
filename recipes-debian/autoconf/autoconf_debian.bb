#
# base recipe: meta/recipes-devtools/autoconf/autoconf_2.69.bb
# base branch: daisy
#

PR = "r0"

inherit debian-package
PV = "2.69"

LICENSE = "GPLv2+ & GPLv3+ & GFDL-1.3+"
LIC_FILES_CHKSUM = " \
	file://COPYING;md5=751419260aa954499f7abaabaa882bbe \
	file://COPYINGv3;md5=d32239bcb673463ab874e80d47fae504 \
	file://doc/autoconf.info;beginline=8;endline=15;md5=7d7c48253fc5f07f296c80f56e817aa2 \
"

SRC_URI += " \
	file://autoreconf-include.patch \
	file://check-automake-cross-warning.patch \
	file://autoreconf-exclude.patch \
	file://autoreconf-foreign.patch \
	file://autoreconf-gnuconfigize.patch \
	file://autoheader-nonfatal-warnings.patch \
	file://config_site.patch \
	file://remove-usr-local-lib-from-m4.patch \
	file://preferbash.patch \
	file://autotest-automake-result-format.patch \
	file://program_prefix.patch \
"
SRC_URI_append_class-native = " file://fix_path_xtra.patch"

DEPENDS += "m4-native"
DEPENDS_class-native = "m4-native gnu-config-native"
DEPENDS_class-nativesdk = "nativesdk-m4 nativesdk-gnu-config"
RDEPENDS_${PN} = " \
	m4 gnu-config \
	perl \
	perl-module-carp \
	perl-module-constant \
	perl-module-errno \
	perl-module-exporter \
	perl-module-file-basename \
	perl-module-file-compare \
	perl-module-file-copy \
	perl-module-file-glob \
	perl-module-file-path \
	perl-module-file-stat \
	perl-module-getopt-long \
	perl-module-io-file \
	perl-module-posix \
"
RDEPENDS_${PN}_class-native = "m4-native gnu-config-native"

inherit autotools

PARALLEL_MAKE = ""

do_configure() {
	oe_runconf
}

do_install_append() {
	rm -rf ${D}${datadir}/emacs

	# Some distros have both /bin/perl and /usr/bin/perl, but we set perl location
	# for target as /usr/bin/perl, so fix it to /usr/bin/perl.
	for i in autoheader autom4te autoreconf autoscan autoupdate ifnames; do
		if [ -f ${D}${bindir}/$i ]; then
			sed -i -e '1s,#!.*perl,#! ${USRBINPATH}/perl,' \
			-e 's,exec .*/bin/perl \(.*\) exec .*/bin/perl \(.*\),exec ${USRBINPATH}/perl \1 exec ${USRBINPATH}/perl \2,' \
            		${D}${bindir}/$i
        	fi
    	done
} 

EXTRA_OECONF += "ac_cv_path_M4=m4"

BBCLASSEXTEND = "native nativesdk"
