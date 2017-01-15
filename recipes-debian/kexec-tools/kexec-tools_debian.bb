#
# Base recipe: meta/recipes-kernel/kexec/kexec-tools_2.0.4.bb
# Base branch: daisy
#

SUMMARY = "Kexec fast reboot tools"
DESCRIPTION = "Kexec is a fast reboot feature that lets you reboot to a new Linux kernel"
AUTHOR = "Eric Biederman"
HOMEPAGE = "http://kernel.org/pub/linux/utils/kernel/kexec/"

PR = "r0"

inherit debian-package
PV = "2.0.7"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = " \
file://COPYING;md5=ea5bed2f60d357618ca161ad539f7c0a \
file://kexec/kexec.c;beginline=1;endline=20;md5=af10f6ae4a8715965e648aa687ad3e09"

inherit autotools-brokensep

EXTRA_OECONF = "--sbindir=${base_sbindir}"

# Install files follow Debian.
do_install_append () {
	# Install init script
	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${S}/debian/kexec.init.d ${D}${sysconfdir}/init.d/kexec
	install -m 0755 ${S}/debian/kexec-load.init.d ${D}${sysconfdir}/init.d/kexec-load
}

# kexec_test uses 32-bit code for testing - add an INSANE_SKIP exception for it.
INSANE_SKIP_${PN} = "arch"
