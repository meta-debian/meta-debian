SUMMARY = "library for decimal floating point arithmetic"
DESCRIPTION = "mpdecimal is a package for correctly-rounded arbitrary precision decimal \
floating point arithmetic."
HOMEPAGE = "http://www.bytereef.org/mpdecimal/index.html"

PR = "r0"

inherit debian-package
PV = "2.4.1"

LICENSE = "BSD"
LIC_FILES_CHKSUM = " \
    file://LICENSE.txt;md5=3fd3cc700fe3844066bc8f4bea75100d \
    file://libmpdec/vcstdint.h;beginline=4;endline=28;md5=0dbc6044c6886dcd128998c24fd7a7db \
"

inherit autotools-brokensep

# Follow debian/rules
EXTRA_OECONF = " \
	CFLAGS='${CFLAGS}' \
	LDFLAGS='${LDFLAGS}' \
	have_glibc_memmove_bug=no \
	have_ipa_pure_const_bug=no \
"

# Follow configure file. Don't set value of LD as poky in this package
EXTRA_OECONF += "LD='${CC}'"

BBCLASSEXTEND = "native nativesdk"
