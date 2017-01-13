#
# base recipe: meta/recipes-kernel/kmod/kmod.inc
# base branch: daisy
#
# Copyright (C) 2012 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Tools for managing Linux kernel modules"
DESCRIPTION = "kmod is a set of tools to handle common tasks with Linux kernel modules like \
               insert, remove, list, check properties, resolve dependencies and aliases."
HOMEPAGE = "http://packages.profusion.mobi/kmod/"
LICENSE = "GPL-2.0+ & LGPL-2.1+"
LICENSE_libkmod = "LGPL-2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe \
                    file://libkmod/COPYING;md5=a6f89e2100d9b6cdffcea4f398e37343 \
                   "

INC_PR = "r0"
inherit debian-package
PV = "18"

DEPENDS += "pkgconfig-native"

inherit autotools gtk-doc ptest

SRC_URI += " \
           file://run-ptest \
           file://ptest.patch \
           file://avoid_parallel_tests.patch \
           file://fix-O_CLOEXEC.patch \
          "

# Follow debian/rules
EXTRA_AUTORECONF += "--force --install --symlink"
EXTRA_OECONF +=" \
	--bindir=${base_bindir} --with-rootlibdir=${base_libdir} \
	--enable-debug --disable-manpages \
"

do_configure_prepend () {
        gtkdocize --docdir ${S}/libkmod/docs || touch ${S}/libkmod/docs/gtk-doc.make
}
