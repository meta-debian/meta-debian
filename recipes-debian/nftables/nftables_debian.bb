# base recipe: meta-openembedded/meta-networking/recipes-filter/nftables/nftables_0.9.0.bb
# base branch: warrior

SUMMARY = "Netfilter Tables userspace utillites"
SECTION = "net"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d1a78fdd879a263a5e0b42d1fc565e79"

inherit debian-package
require recipes-debian/sources/nftables.inc

DEPENDS = "libmnl libnftnl readline gmp bison-native"

inherit autotools manpages pkgconfig

PACKAGECONFIG ?= ""
PACKAGECONFIG[manpages] = "--enable--man-doc, --disable-man-doc"

ASNEEDED = ""

RRECOMMENDS_${PN} += "kernel-module-nf-tables"
