SUMMARY = "Asynchronous name service query library"
DESCRIPTION = "libasyncns is a C library for Linux/Unix for executing name service queries \
 asynchronously. It is an asynchronous wrapper around getaddrinfo(3), \
 getnameinfo(3), res_query(3) and res_search(3) from libc and libresolv."
HOMEPAGE = "http://0pointer.de/lennart/projects/libasyncns/"

inherit debian-package
PV = "0.8"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fad9b3332be894bab9bc501572864b29"

inherit autotools

RPROVIDES_${PN} += "libasyncns0"
