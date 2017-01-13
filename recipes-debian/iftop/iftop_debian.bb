inherit debian-package
PV = "1.0~pre4"
DEBIAN_PATCH_TYPE = "quilt"

SUMMARY = "displays bandwidth usage information on an network interface"
DESCRIPTION = "\
iftop does for network usage what top(1) does for CPU usage. It listens to \
network traffic on a named interface and displays a table of current bandwidth \
usage by pairs of hosts. Handy for answering the question "Why is my Internet \
link so slow?"."
HOMEPAGE = "http://www.ex-parrot.com/~pdw/iftop/"
SECTION = "net"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=76498170798db0f4f0fb685a225f702f"

inherit autotools-brokensep

DEPENDS_${PN} += " ncurses libpcap"
RDEPENDS_${PN} += " ncurses-base libgcc"
