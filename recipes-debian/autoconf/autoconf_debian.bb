#
# base recipe: meta/recipes-devtools/autoconf/autoconf_2.69.bb
# base branch: master
# base commit: b0f2f690a3513e4c9fa30fee1b8d7ac2d7140657
#

require recipes-devtools/autoconf/autoconf.inc

inherit debian-package
require recipes-debian/sources/autoconf.inc

LICENSE = "GPLv2+ & GPLv3+ & GFDL-1.3+"
LIC_FILES_CHKSUM = " \
	file://COPYING;md5=751419260aa954499f7abaabaa882bbe \
	file://COPYINGv3;md5=d32239bcb673463ab874e80d47fae504 \
	file://doc/autoconf.texi;beginline=198;endline=216;md5=69a78e701d621162d545a8d6911c069a \
"

FILESPATH_append = ":${COREBASE}/meta/recipes-devtools/autoconf/autoconf"
SRC_URI += " \
	file://check-automake-cross-warning.patch \
	file://autoreconf-exclude.patch \
	file://autoreconf-gnuconfigize.patch \
	file://config_site.patch \
	file://remove-usr-local-lib-from-m4.patch \
	file://preferbash.patch \
	file://autotest-automake-result-format.patch \
	file://program_prefix.patch \
	file://autoconf-replace-w-option-in-shebangs-with-modern-use-warnings.patch \
"

SRC_URI_append_class-native = " file://fix_path_xtra.patch"

EXTRA_OECONF += "ac_cv_path_M4=m4 ac_cv_prog_TEST_EMACS=no"

BBCLASSEXTEND = "native nativesdk"
