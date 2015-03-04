require recipes-core/eglibc/eglibc_2.19.bb
FILESEXTRAPATHS_prepend = "${COREBASE}/meta/recipes-core/eglibc/eglibc-2.19:"

inherit debian-package
DEBIAN_SECTION = "libs"
DPR = "1"

DPN = "glibc"

LICENSE = "GPLv2 & LGPLv2.1 & ISC"
LIC_FILES_CHKSUM = " \
file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
file://COPYING.LIB;md5=4fbd65380cdd255951079008b364516c \
file://LICENSES;md5=e9a558e243b36d3209f380deb394b213 \
"

#Remove depend on kconfig-frontends-native
DEPENDS_remove = "kconfig-frontends-native"

# Exclude patch that apply for old version:
# mips-rld-map-check.patch
# initgroups_keys.patch
# eglibc_fix_findidx_parameters.patch
# fileops-without-wchar-io.patch
# fix-tibetian-locales.patch
# CVE-2014-5119.patch
#
# Exclude GLRO_dl_debug_mask.patch because debian source code (2.19-10) 
# does not support RTLD debug
#
# Exclude 0001-eglibc-menuconfig-support.patch
#         0002-eglibc-menuconfig-hex-string-options_debian.patch
#         0003-eglibc-menuconfig-build-instructions.patch
# because Debian distro does not support kconfig-frontend

SRC_URI += " \
	file://eglibc-svn-arm-lowlevellock-include-tls.patch \
	file://IO-acquire-lock-fix.patch \
	file://etc/ld.so.conf \
	file://generate-supported.mk \
	file://glibc.fix_sqrt2.patch \
	file://multilib_readlib.patch \
	file://ppc-sqrt_finite.patch \
	file://ppc_slow_ieee754_sqrt.patch \
	file://add_resource_h_to_wait_h.patch \
	file://fsl-ppc-no-fsqrt.patch \
	file://0001-R_ARM_TLS_DTPOFF32.patch \
	file://0001-eglibc-run-libm-err-tab.pl-with-specific-dirs-in-S.patch \
	file://ppce6500-32b_slow_ieee754_sqrt.patch \
	file://grok_gold.patch \
"

# manual: texinfo meterial to build libc's info page
SRC_URI += " \
	file://manual \
"
# Install missing texinfo materials
do_debian_patch_append() {
	cp -r ${WORKDIR}/manual ${S}
	# Set empty because kconfig-frontend is not supported
	echo "config:" >> ${S}/Makeconfig
}

EXTRA_OECONF += " \
	--disable-profile \
	--without-gd \
	--without-cvs \
	--enable-add-ons=nptl,libidn,ports \
"
