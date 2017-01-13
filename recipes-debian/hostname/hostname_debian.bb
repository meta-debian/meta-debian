SUMMARY = "utility to set/show the host name or domain name"
DESCRIPTION = "This package provides commands which can be used to display the system's \
DNS name, and to display or set its hostname or NIS domain name."

inherit debian-package
PV = "3.15"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=86dc5e6760b0845ece4d5be3a9d397d9"

# There is no debian/patches
DEBIAN_PATCH_TYPE = "nopatch"

do_compile() {
	oe_runmake
}

do_install() {
	oe_runmake 'BASEDIR=${D}' install
}
