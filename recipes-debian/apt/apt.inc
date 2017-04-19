#
# base recipe: meta/recipes-devtools/apt/apt.inc
# base branch: daisy
#

inherit debian-package
PV = "1.0.9.8.4"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING.GPL;md5=0636e73ff0215e8d672dc4c32c317bb3"

SRC_URI += " \
	file://use-host_debian.patch \
	file://makerace.patch \
	file://no-nls-dpkg.patch \
	file://fix-gcc-4.6-null-not-defined.patch \
	file://truncate-filename.patch \
	file://nodoc.patch \
	file://disable-configure-in-makefile.patch \
	file://apt.conf \
	file://reverse-patch-to-avoid-running-dpkg-configure-a.patch \
"

inherit autotools gettext

EXTRA_AUTORECONF = "--exclude=autopoint,autoheader"

do_configure_prepend() {
	rm -rf ${S}/buildlib/config.sub
	rm -rf ${S}/buildlib/config.guess
}

# Apt wants to know the glibc version by running a binary file, which will
# fail, so we have to tell configure which version to use Since I don't know
# the impliations of setting a wrong value I only provide one for angstrom,
# which uses glibc 2.5 (which claims to be 2.4)
# Koen - 20070327
EXTRA_OECONF_append_angstrom = " ac_cv_glibc_ver=libc6.4"

# under Debian it is set to libc6.3 as they use glibc 2.3
# They also provide glibc 2.5 in 'experimental' and it works with APT built
# for 2.3 so we set it in same way
EXTRA_OECONF_append = " ac_cv_glibc_ver=libc6.3"

FILES_${PN}-dbg += "${libdir}/apt/methods/.debug/"
