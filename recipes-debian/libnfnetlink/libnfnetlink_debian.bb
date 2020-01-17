# base recipe: meta-openembedded/meta-networking/recipes-filter/libnfnetlink/libnfnetlink_1.0.1.bb
# base branch: warrior

SUMMARY = "Low-level library for netfilter related kernel/userspace communication"
DESCRIPTION = "libnfnetlink is the low-level library for netfilter related \
kernel/userspace communication. It provides a generic messaging \
infrastructure for in-kernel netfilter subsystems (such as nfnetlink_log, \
nfnetlink_queue, nfnetlink_conntrack) and their respective users and/or \
management tools in userspace."
HOMEPAGE = "http://www.netfilter.org/projects/libnfnetlink/index.html"
SECTION = "libs"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b"

inherit debian-package
require recipes-debian/sources/libnfnetlink.inc

DEBIAN_QUILT_PATCHES = ""

SRC_URI += "file://0001-build-resolve-automake-1.12-warnings.patch \
            file://0003-configure-uclinux-is-also-linux.patch \
            file://0004-libnfnetlink-initialize-attribute-padding-to-resolve.patch \
            file://0005-include-Sync-with-kernel-headers.patch \
            file://0006-src-Use-stdint-types-everywhere.patch \
           "

inherit autotools pkgconfig
