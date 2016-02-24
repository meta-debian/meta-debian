SUMMARY = "SELinux library for manipulating binary security policies"
inherit debian-package

PR = "r0"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=a6f89e2100d9b6cdffcea4f398e37343"

# source format is 3.0 (quilt) but there is no patch
DEBIAN_QUILT_PATCHES = ""

do_compile(){
	oe_runmake
}

do_install(){
	oe_runmake install DESTDIR=${D}  
}

PACKAGES =+ "sepol-utils"

FILES_sepol-utils += "${bindir}/chkcon"
FILES_${PN} += "${base_libdir}/libsepol.so.1"

