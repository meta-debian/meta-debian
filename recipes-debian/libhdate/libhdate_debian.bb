DESCRIPTION = "LibHdate is a small C,C++ library for Hebrew dates, \
holidays, and reading sequence (parasha). It is using \
the source code from Amos Shapir's "hdate" package fixed \
and patched by Nadav Har'El. The Torah reading sequence \
is from tables by Zvi Har'El."
HOMEPAGE = "http://libhdate.sourceforge.net/"

inherit debian-package
PV = "1.6"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

# bindings-cross-compile.patch:
#     Prevent using g++ and host include dir when build bindings/python and bindings/perl
SRC_URI += "file://bindings-cross-compile.patch"

inherit autotools-brokensep gettext pythonnative cpan-base

# hdate python binding depends on swig in configure
DEPENDS += "swig"

EXTRA_OECONF = " \
    PY_PREFIX=${STAGING_DIR_HOST}${prefix} \
    PE_PREFIX=${STAGING_LIBDIR}${PERL_OWN_DIR}/perl/${@get_perl_version(d)} \
    --with-python-sitelib-dir=${PYTHON_SITEPACKAGES_DIR} \
    --with-perl-sitelib-dir=${PERLLIBDIRS} \
    --disable-ruby \
"

PACKAGE_BEFORE_PN =+ "hdate python-hdate ${PN}-perl"

FILES_hdate = "${bindir}/*"
FILES_python-hdate = "${PYTHON_SITEPACKAGES_DIR}/*"
FILES_${PN}-perl = "${PERLLIBDIRS}/*"
FILES_${PN}-dbg += "${PYTHON_SITEPACKAGES_DIR}/.debug"

# Base on debian/control
RDEPENDS_python-hdate += "${PN}"
RDEPENDS_${PN}-perl += "${PN}"
RDEPENDS_hdate += "${PN}"

BBCLASSEXTEND = "native"
