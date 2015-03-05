SUMMARY = "Tools for managing Linux kernel modules"                             
DESCRIPTION = "kmod is a set of tools to handle common tasks with Linux kernel modules like \
               insert, remove, list, check properties, resolve dependencies and aliases."

HOMEPAGE = "http://packages.profusion.mobi/kmod/"

inherit debian-package autotools ptest
DEBIAN_SECTION = "admin"
PR = "r0"
DPR = "0"

LICENSE = "GPLv2 & LGPLv2.1"
LICENSE_libkmod = "LGPLv2.1"
LIC_FILES_CHKSUM = "\
 file://COPYING;md5=751419260aa954499f7abaabaa882bbe \
 file://libkmod/COPYING;md5=a6f89e2100d9b6cdffcea4f398e37343 \
"

SRC_URI += "\
 file://depmod-search.conf \
 file://run-ptest \
 file://ptest.patch \
 file://avoid_parallel_tests.patch \
 file://fix-O_CLOEXEC.patch \
 file://Change-to-calling-bswap_-instead-of-htobe-and-be-toh_debian.patch \
"

EXTRA_AUTORECONF += "--install --symlink"                                       
EXTRA_OECONF +="--enable-debug --enable-logging --enable-tools --disable-manpages\
		 --with-zlib --disable-gtk-doc"
