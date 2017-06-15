SUMMARY = "Shell with C-like syntax"
DESCRIPTION = "The C shell was originally written at UCB to overcome limitations in the \
Bourne shell.  Its flexibility and comfort (at that time) quickly made it \
the shell of choice until more advanced shells like ksh, bash, zsh or \
tcsh appeared.  Most of the latter incorporate features original to csh."

inherit debian-package
PV = "20110502"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://csh.h;beginline=4;endline=30;md5=60d2e466017ade807b579cb0367517e6"

inherit autotools-brokensep update-alternatives

DEPENDS += "bmake-native libbsd"

export MAKE="bmake"
export LDADD="${LDFLAGS}"
do_compile() {
	oe_runmake CC="${CC}"
}

do_install() {
	install -D ${B}/csh ${D}${base_bindir}/bsd-csh
}
do_install_append_class-native() {
	ln -sf bsd-csh ${D}${base_bindir}/csh
}

# Add update-alternatives definitions base on debian/postinst
ALTERNATIVE_PRIORITY="30"
ALTERNATIVE_${PN} = "csh"
ALTERNATIVE_LINK_NAME[csh] = "${base_bindir}/csh"
ALTERNATIVE_TARGET[csh] = "${base_bindir}/bsd-csh"

RPROVIDES_${PN} = "c-shell"
RDEPENDS_${PN}_class-target += "libbsd"

BBCLASSEXTEND = "native"

# Disable parallel make
PARALLEL_MAKE = ""
