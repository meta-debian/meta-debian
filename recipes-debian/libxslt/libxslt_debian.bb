#
# Base recipe: meta/recipes-support/libxslt/libxslt_1.1.28.bb
# Base branch: Daisy
# Base commit: 9e4aad97c3b4395edeb9dc44bfad1092cdf30a47
#

SUMMARY = "GNOME XSLT library"
HOMEPAGE = "http://xmlsoft.org/XSLT/"
BUGTRACKER = "https://bugzilla.gnome.org/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://Copyright;md5=0cd9a07afbeb24026c9b03aecfeba458"

PR = "r0"
DEPENDS = "libxml2"

inherit debian-package
PV = "1.1.28"

EXTRA_OECONF = "--without-python --without-debug --without-mem-debug --without-crypto"

inherit autotools pkgconfig binconfig lib_package

# We don't DEPEND on binutils for ansidecl.h so ensure we don't use the header
do_configure_prepend () {
	sed -i -e 's/ansidecl.h//' ${S}/configure.in

	# The timestamps in the 1.1.28 tarball are messed up causing this file to
	# appear out of date.  Touch it so that we don't try to regenerate it.
	touch ${S}/doc/xsltproc.1
}

# older versions of this recipe had ${PN}-utils
RPROVIDES_${PN}-bin += "${PN}-utils"
RCONFLICTS_${PN}-bin += "${PN}-utils"
RREPLACES_${PN}-bin += "${PN}-utils"

FILES_${PN} += "${libdir}/libxslt-plugins"
FILES_${PN}-dev += "${libdir}/xsltConf.sh"

# Set this variable to specify the library to which to apply the naming
# scheme. Without this variable, DEBIANNAME could not set.
LEAD_SONAME = "libxslt.so"

DEBIANNAME_${PN}-dbg = "libxslt1-dbg"
DEBIANNAME_${PN}-dev = "libxslt1-dev"
DEBIANNAME_${PN}-bin = "xsltproc"
DEBIANNAME_${PN} = "libxslt1.1"

BBCLASSEXTEND = "native"
