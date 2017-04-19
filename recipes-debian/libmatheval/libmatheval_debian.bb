SUMMARY = "GNU library for evaluating symbolic mathematical expressions"
DESCRIPTION = "GNU libmatheval is a library comprising of several procedures that make \
 it possible to create an in-memory tree representation of mathematical \ 
 functions over single or multiple variables and later use this \
 representation to evaluate functions for specified variable values, to \
 create corresponding trees for function derivatives over specified \
 variables or to print textual representations of in-memory trees to a \
 specified string.  The library supports arbitrary variable names in \
 expressions, decimal constants, basic unary and binary operators and \
 elementary mathematical functions."
HOMEPAGE = "http://www.gnu.org/software/libmatheval/"

PR = "r0"
inherit debian-package
PV = "1.1.11+dfsg"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=f27defe1e96c2e1ecd4e0c9be8967949"

inherit autotools gettext pkgconfig
do_configure_prepend() {
	# base on debian/rules
	touch ${S}/config/config.rpath
}
DEPENDS += "guile-2.0 flex libtool"
RPROVIDES_${PN}-dev = "libmatheval1-dev"
