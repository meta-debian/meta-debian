require recipes-support/popt/popt_1.16.bb
FILESEXTRAPATHS_prepend = "${COREBASE}/meta/recipes-support/popt/popt:"

inherit debian-package

DEBIAN_SECTION = "devel"
DPR = "0"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=cb0613c30af2a8249b8dcc67d3edb06d"

SRC_URI += " \
file://pkgconfig_fix.patch \
file://popt_fix_for_automake-1.12.patch \
file://disable_tests.patch \
"

# Preprocessing directive #elseif should be #elif
# so change it.
do_compile_prepend(){
	sed -i s@"#elseif"@"#elif"@g ${S}/system.h
}
