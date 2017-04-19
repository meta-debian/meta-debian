# base recipe: http://cgit.openembedded.org/cgit.cgi/meta-openembedded/tree/\
#	meta-oe/recipes-connectivity/libnet/libnet_1.2-rc3.bb?
# base branch: master
# base commit: a558e8096c381bb2b83ad1ef39f3301e73759cbb
#

SUMMARY = "A packet dissection and creation library"
DESCRIPTION = "libnet provides a portable framework for low-level \
network packet writing and handling \
libnet features portable packet creation interfaces at the IP layer \
and link layer, as well as a host of supplementary functionality"
HOMEPAGE = "http://libnet-dev.sourceforge.net/"

PR = "r0"
inherit debian-package
PV = "1.1.6+dfsg"

LICENSE = "BSD-2-Clause & BSD-3-Clause & BSD-4-Clause"
LIC_FILES_CHKSUM = "\
	file://doc/COPYING;md5=fb43d5727b2d3d1238545f75ce456ec3 \
	file://include/ifaddrlist.h;beginline=1;endline=21;md5=ccc5a11df2bba85cf4b010112171df59 \
	file://include/bpf.h;beginline=6;endline=41;md5=d7d64c78ed4248f365f5788c2c469b45 \
"
DEPENDS = "libpcap"

inherit autotools binconfig

#Correct the packages name
DEBIANNAME_${PN}-dev = "${PN}1-dev"
DEBIANNAME_${PN}-doc = "${PN}1-doc"
DEBIANNAME_${PN}-dbg = "${PN}1-dbg"
