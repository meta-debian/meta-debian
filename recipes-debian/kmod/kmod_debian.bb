#
# base recipe: meta/recipes-kernel/kmod/kmod_git.bb
# base branch: daisy
#
# Copyright (C) 2012 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

require kmod.inc

PR = "${INC_PR}.0"

# Follow debian/rules
do_install_append () {
	mkdir ${D}${base_libdir}/modprobe.d/
	cp ${S}/extra/aliases.conf ${D}${base_libdir}/modprobe.d/

	mkdir -p ${D}${datadir}/initramfs-tools/hooks
        install -m 0755 ${S}/debian/kmod.initramfs-hook \
                ${D}${datadir}/initramfs-tools/hooks/kmod

	# Install initscript
	mkdir -p ${D}${sysconfdir}/init.d
	install -m 0755 ${S}/debian/kmod.init ${D}${sysconfdir}/init.d/kmod

	ln -sf kmod ${D}${base_bindir}/lsmod
	install -d ${D}${base_sbindir}
	for tool in depmod insmod lsmod modinfo modprobe rmmod; do
		ln -sf ..${base_bindir}/kmod ${D}${base_sbindir}/${tool}
	done
}

do_compile_ptest () {
	oe_runmake buildtest-TESTS rootfs
}

INHIBIT_PACKAGE_STRIP = "${@bb.utils.contains("DISTRO_FEATURES", "ptest", "1", "0", d)}"
INSANE_SKIP_${PN}-ptest = "arch"

PROVIDES += "module-init-tools"
PACKAGES =+ "libkmod module-init-tools"

# Base on debian/control
# init.d/kmod require lsb-base
RDEPENDS_${PN} += "lsb-base"
RDEPENDS_module-init-tools += "kmod libkmod"

RREPLACES_${PN} += "module-init-tools"

FILES_libkmod = "${base_libdir}/libkmod*${SOLIBS} ${libdir}/libkmod*${SOLIBS}"
FILES_${PN} += " \
	${base_libdir}/modprobe.d \
	${datadir}/bash-completion \
	${datadir}/initramfs-tools \
"

DEBIANNAME_${PN}-dev = "libkmod-dev"

# Add update-alternatives definitions
inherit update-alternatives

base_sbindir_progs = "depmod insmod lsmod modinfo modprobe rmmod"
ALTERNATIVE_PRIORITY="100"
ALTERNATIVE_${PN} = "${base_sbindir_progs}"
python __anonymous() {
        for prog in d.getVar('base_sbindir_progs', True).split():
                d.setVarFlag('ALTERNATIVE_LINK_NAME', prog, '%s/%s' % (d.getVar('base_sbindir', True), prog))
}
