#
# base recipe: meta/recipes-devtools/binutils/binutils-2.30.inc
# base branch: master
# base commit: 53dfa673d78216f852a47bdb48392ee213e3e1cd
#

require recipes-devtools/binutils/binutils.inc

inherit debian-package
require recipes-debian/sources/binutils.inc
BPN = "binutils"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552 \
    file://COPYING.LIB;md5=9f604d8a4f8e74f4f5140845a21b6674 \
    file://COPYING3;md5=d32239bcb673463ab874e80d47fae504 \
    file://COPYING3.LIB;md5=6a6a8e020838b23406c81b19c1d46df6 \
    file://gas/COPYING;md5=d32239bcb673463ab874e80d47fae504 \
    file://include/COPYING;md5=59530bdf33659b29e73d4adb9f9f6552 \
    file://include/COPYING3;md5=d32239bcb673463ab874e80d47fae504 \
    file://libiberty/COPYING.LIB;md5=a916467b91076e631dd8edb7424769c7 \
    file://bfd/COPYING;md5=d32239bcb673463ab874e80d47fae504 \
"

# Ignore patch 0004, 0005, 0011 and 0014 from base recipe.
# - Patches 0004 and 0005 already in Debian source code.
# - Patches 0011 conflicts with Debian fix. Debian set a new target for N64
#   instead of changing existing target. This also makes patch 0014 have no use.
SRC_URI += " \
     file://0003-configure-widen-the-regexp-for-SH-architectures.patch \
     file://0006-Use-libtool-2.4.patch \
     file://0008-don-t-let-the-distro-compiler-point-to-the-wrong-ins.patch \
     file://0009-warn-for-uses-of-system-directories-when-cross-linki.patch \
     file://0010-Fix-rpath-in-libtool-when-sysroot-is-enabled.patch \
     file://0012-Add-support-for-Netlogic-XLP.patch \
     file://0013-fix-the-incorrect-assembling-for-ppc-wait-mnemonic.patch \
     file://0015-sync-with-OE-libtool-changes.patch \
"
FILESPATH_append = ":${COREBASE}/meta/recipes-devtools/binutils/binutils"
