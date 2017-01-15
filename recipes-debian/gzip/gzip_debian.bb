#
# base recipe: meta/recipes-extended/gzip/gzip_1.6.bb
# base branch: daisy
#

PR = "r0"

inherit debian-package
PV = "1.6"

LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = " \
	file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
	file://gzip.h;beginline=8;endline=20;md5=6e47caaa630e0c8bf9f1bc8d94a8ed0e \
"

inherit autotools update-alternatives

# Disable gcc warnings to avoid warnings are treated as errors
# Set bindir to ${base_bindir} base on debian/rules
EXTRA_OECONF_class-target = "--disable-gcc-warnings --bindir=${base_bindir}"
EXTRA_OEMAKE_class-target = "GREP=${base_bindir}/grep"

PROVIDES_append_class-native = " gzip-replacement-native"
NATIVE_PACKAGE_PATH_SUFFIX = "/${PN}"

#Add update-alternatives definitions
ALTERNATIVE_PRIORITY="100"
ALTERNATIVE_${PN} = "gunzip gzip uncompress zcat"
ALTERNATIVE_LINK_NAME[gunzip] = "${base_bindir}/gunzip"
ALTERNATIVE_LINK_NAME[gzip] = "${base_bindir}/gzip"
ALTERNATIVE_LINK_NAME[uncompress] = "${base_bindir}/uncompress"
ALTERNATIVE_LINK_NAME[zcat] = "${base_bindir}/zcat"

BBCLASSEXTEND = "native"
