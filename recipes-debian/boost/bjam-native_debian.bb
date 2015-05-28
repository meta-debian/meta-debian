include boost-debian.inc

SUMMARY = "Portable Boost.Jam build tool for boost"
SECTION = "devel"

inherit native

do_compile() {
    ./bootstrap.sh --with-toolset=gcc
}

do_install() {
    install -d ${D}${bindir}/
    install -c -m 755 bjam ${D}${bindir}/
}

#
# Meta Debian
#
inherit debian-package
DPR = "0"
DPN = "boost1.55"
