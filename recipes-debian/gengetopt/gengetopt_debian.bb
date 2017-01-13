PR = "r0"

inherit debian-package autotools-brokensep
PV = "2.22.6+dfsg0"

LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=ff95bfe019feaf92f524b73dd79e76eb"

DEPENDS = "flex"

do_configure_append() {
	sed -i "s:gengetopt_DEPENDENCIES = \$(top_builddir)/src/libgengetopt\.la:gengetopt_DEPENDENCIES = libgengetopt\.la:" ${S}/src/Makefile
}

BBCLASSEXTEND = "native"
