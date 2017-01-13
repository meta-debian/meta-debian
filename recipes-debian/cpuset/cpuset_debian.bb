# 
# No base recipe.
#

DESCRIPTION = "Cpuset is a Python application to make using the cpusets \
facilities in the Linux kernel easier. The actual included command is called \
cset and it allows manipulation of cpusets on the system and provides higher \
level functions such as implementation and control of a basic CPU shielding setup."
HOMEPAGE = "http://code.google.com/p/cpuset/"

PR = "r0"

inherit debian-package autotools-brokensep
PV = "1.5.6"

RDEPENDS_${PN} = "python-core python-textutils python-distutils python-logging python-unixadmin"

SRC_URI += " \
	file://adapt_install_layout_for_debian.patch \
"

EXTRA_OEMAKE += "PREFIX=${prefix} DESTDIR=${D}"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

FILES_${PN} += "${libdir}"
