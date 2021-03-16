# The main license in COPYING looks almost same as TCL/TK license except
# the final section ("GOVERNMENT USE:"), which is categolized as BSD-style.
# libsmi recipe in meta-oe also defines its license as "BSD".
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=3ad3076f9332343a21636cfd351f05b7"

PV = "0.4.8+dfsg2"

inherit debian-package

# Doesn't support the separted build directory
inherit autotools-brokensep

DEPENDS += "flex-native bison-native"

do_install_append() {
	install -d ${D}/${sysconfdir}
	install -m 0644 ${S}/debian/smi.conf ${D}/${sysconfdir}

	# "pibs" directory is not shipped in any Debian packages
	rm -rf ${D}/${datadir}/pibs
}

PACKAGES =+ "smistrip smitools"

FILES_smistrip = "${bindir}/smistrip"

FILES_smitools = "${bindir}/smicache \
                  ${bindir}/smidiff \
                  ${bindir}/smidump \
                  ${bindir}/smilint \
                  ${bindir}/smiquery \
                  ${bindir}/smixlate \
                 "

# Provide a binary package which is same name as Debian's
RPROVIDES_${PN} += "libsmi2ldbl"
PKG_${PN} = "libsmi2ldbl"
