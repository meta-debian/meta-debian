SUMMARY = "management tool for OCaml libraries"
DESCRIPTION = "The "findlib" OCaml library provides a scheme to manage reusable \
software components (packages), and includes tools that support this \
scheme. Packages are collections of OCaml modules for which \
metainformation can be stored. The packages are kept in the \
filesystem hierarchy, but with strict directory structure. The \
library contains functions to look the directory up that stores a \
package, to query metainformation about a package, and to retrieve \
dependency information about multiple packages."
HOMEPAGE = "http://projects.camlcity.org/projects/findlib.html"

inherit debian-package
PV = "1.4.1"

LICENSE = "MIT & QPL-1.0"
LIC_FILES_CHKSUM = "\
	file://LICENSE;md5=a30ace4f9508a47d2c25c45c48af6492 \
	file://tools/make-package-macosx;endline=16;md5=f9423c9130e4e07f0a369bbb01cbc9f5"

inherit autotools-brokensep native

DEPENDS += "ocaml-native"
do_configure() {
	./configure \
		-config ${sysconfdir}/ocamlfind.conf \
		-bindir ${bindir} \
		-sitelib ${libdir}/ocaml \
		-mandir ${mandir} \
		-with-toolbox
}
do_install() {
	oe_runmake install prefix="${D}"
}

# do not strip executables on bytecode executables
INHIBIT_SYSROOT_STRIP = "1"
