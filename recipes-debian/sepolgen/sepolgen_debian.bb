SUMMARY = "Python module used in SELinux policy generation"
DESCRIPTION = "The sepolgen library is structured to give flexibility to the application \
using it. The library contains: Reference Policy Representation, \
which are Objects for representing policies and the reference policy \
interfaces. Secondly, it has objects and algorithms for representing \
access and sets of access in an abstract way and searching that \
access. It also has a parser for reference policy "headers". It \
contains infrastructure for parsing SELinux related messages as \
produced by the audit system. It has facilities for generating policy \
based on required access."
HOMEPAGE = "http://userspace.selinuxproject.org/"

PR = "r0"

inherit debian-package
PV = "1.2.1"

LICENSE = "GPLv2+ & LGPLv2.1+"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=393a5ca445f6965873eca0259a17f833 \
    file://src/sepolgen/yacc.py;endline=22;md5=ac24ba658bf16b09297ca83ec064d56a \
"

inherit pythonnative

DEPENDS += "python"

# source format is 3.0 (quilt) but there is no debian patch
DEBIAN_QUILT_PATCHES = ""

do_install() {
	oe_runmake DESTDIR=${D} \
	           PYTHONLIBDIR='${PYTHON_SITEPACKAGES_DIR}' \
	           install
}

FILES_${PN} += "${libdir}/${PYTHON_DIR}/*-packages/sepolgen/*"

PKG_${PN} = "python-${PN}"

BBCLASSEXTEND = "native"
