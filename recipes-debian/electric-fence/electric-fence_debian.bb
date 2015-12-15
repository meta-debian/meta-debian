SUMMARY = "A malloc(3) debugger"
DESCRIPTION = "	Electric Fence is a debugger that uses virtual memory hardware	\
		to detect illegal memory accesses. It can detect two common 	\
		programming bugs: software that overruns or underruns the 	\
		boundaries of a malloc() memory allocation, and software 	\
		that touches a memory allocation that has been released by free()."

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=18810669f13b87348459e611d31ab760"

PR = "r0"
inherit debian-package

#fix-unknown-file.patch
#	fixed the install some unknown file: ef.sh, efence.3
#	correct the softlink: libefence.so --> libefence.so.0
SRC_URI += "file://makefile-fix-install-unknown-file.patch"

DEBIAN_PATCH_TYPE = "nopatch"

#Position-independent code
CFLAGS += " -fPIC"

#Install file follow Debian jessies
do_install() {
	#Create new folders
	install -d ${D}${libdir}
	install -d ${D}${mandir}/man3
	oe_runmake 'DESTDIR=${D}' 'LIB_INSTALL_DIR=${D}${libdir}' \
		   'MAN_INSTALL_DIR= ${D}${mandir}/man3' install
}
#Correct the sub-package name
DEBIANNAME_${PN} = "electric-fence"
