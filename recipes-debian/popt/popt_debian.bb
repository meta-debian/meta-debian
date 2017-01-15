#
# Base recipe: meta/recipes-support/popt/popt_1.16.bb
# Base branch: daisy
#
SUMMARY = "Library for parsing command line options"
HOMEPAGE = "http://rpm5.org/"

PR = "r0"

inherit debian-package
PV = "1.16"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=cb0613c30af2a8249b8dcc67d3edb06d"

inherit autotools gettext

# popt_fix_for_automake-1.12.patch: already applied in 1.16-10
SRC_URI += " \
file://pkgconfig_fix.patch \
file://disable_tests.patch \
"

# Preprocessing directive #elseif should be #elif
# so change it.
do_compile_prepend(){
	sed -i s@"#elseif"@"#elif"@g ${S}/system.h
}

BBCLASSEXTEND = "native nativesdk"
