require recipes-support/nspr/nspr_4.10.3.bb
FILESEXTRAPATHS_prepend = "\
${THISDIR}/files:${COREBASE}/meta/recipes-support/nspr/nspr:\
${COREBASE}/meta/recipes-support/nspr/files:\
"

inherit debian-package
DEBIAN_SECTION = "libs"
DPR = "0"

LICENSE = "GPLv2.0 & MPL-2.0 & LGPLv2.1"
LIC_FILES_CHKSUM = "\
 file://configure.in;beginline=3;endline=6;md5=90c2fdee38e45d6302abcfe475c8b5c5 \
 file://Makefile.in;beginline=4;endline=38;md5=beda1dbb98a515f557d3e58ef06bca99\
"

S = "${WORKDIR}/git/nspr"

SRC_URI += "\
	file://remove-rpath-from-tests.patch \
	file://fix-build-on-x86_64_debian.patch \
	file://trickly-fix-build-on-x86_64_debian.patch \
	file://nspr.pc.in \
"

# Override function do_configure since there is no need
# to run other command than oe_runconf. build/autoconf
# and config.sub, config.guess are in right place
do_configure() {                                                                
        oe_runconf                                                              
}
