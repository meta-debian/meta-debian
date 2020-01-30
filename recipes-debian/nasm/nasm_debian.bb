# base recipe: meta/recipes-devtools/nasm/nasm_2.14.02.bb
# base branch: warrior
# base commit: dbd22b6cd75dd607e1e47bf12da4d54b574c9a8f

SUMMARY = "General-purpose x86 assembler"
SECTION = "devel"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=90904486f8fbf1861cf42752e1a39efe"

inherit debian-package
require recipes-debian/sources/nasm.inc

FILESPATH_append = ":${COREBASE}/meta/recipes-devtools/nasm/nasm"
SRC_URI += " \
           file://CVE-2018-19755.patch \
           "

# brokensep since this uses autoconf but not automake
inherit autotools-brokensep

EXTRA_AUTORECONF += "--exclude=aclocal"

BBCLASSEXTEND = "native"

DEPENDS = "groff-native"
