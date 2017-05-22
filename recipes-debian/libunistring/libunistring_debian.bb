SUMMARY = "Unicode string library for C"
DESCRIPTION = "The 'libunistring' library implements Unicode strings (in the UTF-8, \
 UTF-16, and UTF-32 encodings), together with functions for Unicode \
 characters (character names, classifications, properties) and \
 functions for string processing (formatted output, width, word \ 
 breaks, line breaks, normalization, case folding, regular \
 expressions)."
HOMEPAGE = "http://www.gnu.org/software/libunistring/"

PR = "r0"
inherit debian-package
PV = "0.9.3"

LICENSE = "GPLv3+ & LGPLv3+ | GFDL-1.2+"
LICENSE_${PN} = "GPLv3+ & LGPLv3+"
LICENSE_${PN}-dev = "GPLv3+ & LGPLv3+"
LICENSE_${PN}-staticdev = "GPLv3+ & LGPLv3+"
LICENSE_${PN}-dbg = "GPLv3+ & LGPLv3+"
LICENSE_${PN}-doc = "GPLv3+ | GFDL-1.2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://COPYING.LIB;md5=6a6a8e020838b23406c81b19c1d46df6 \
                    file://doc/libunistring.texi;beginline=85;endline=107;md5=0eca48c8641a18c260ed173fdf166a65"

inherit autotools
BBCLASSEXTEND = "native"

# Avoid a parallel build problem
PARALLEL_MAKE = ""
