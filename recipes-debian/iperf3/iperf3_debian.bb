SUMMARY = "Internet Protocol bandwidth measuring tool"
DESCRIPTION = "Iperf3 is a tool for performing network throughput measurements. It can \
 test either TCP or UDP throughput. \
 This is a new implementation that shares no code with the original \
 iperf from NLANR/DAST and also is not backwards compatible."
HOMEPAGE = "http://software.es.net/iperf/"

PR = "r0"
inherit debian-package
PV = "3.0.7"

LICENSE = "BSD-3-Clause & MIT & NCSA"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9ceb58c78d73a576a15ad224f23a6cdc \
                    file://src/cjson.c;endline=20;md5=d3f527358d19c4f3be174cb561159e40 \
                    file://src/locale.c;endline=46;md5=c7ff631c2856f8afce226fac3888be9f"

# Avoid a parallel build problem
PARALLEL_MAKE = ""
inherit autotools-brokensep

PACKAGES =+ "libiperf"
FILES_libiperf = "${libdir}/libiperf${SOLIBS}"

DEBIANNAME_${PN}-dev = "libiperf-dev"
