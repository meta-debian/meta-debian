SUMMARY = "A malloc(3) debugger"
DESCRIPTION = "	Electric Fence is a debugger that uses virtual memory hardware	\
		to detect illegal memory accesses. It can detect two common 	\
		programming bugs: software that overruns or underruns the 	\
		boundaries of a malloc() memory allocation, and software 	\
		that touches a memory allocation that has been released by free()."

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=18810669f13b87348459e611d31ab760"

PR = "r2"
inherit debian-package
PV = "2.2.4"

#makefile-fix-install-unknown-file_debian.patch:
#	- fixed the install some unknown file: ef.sh, efence.3
#	correct the softlink: libefence.so --> libefence.so.0
#	- remove the Testing for Electric Fence
#	script to test:"./eftest" and "./tstheap" depend on diffrent architecture
SRC_URI += "file://makefile-fix-install-unknown-file_debian.patch"

DEBIAN_PATCH_TYPE = "nopatch"

#Position-independent code
CFLAGS += " -fPIC"

EXTRA_OEMAKE = "-e MAKEFLAGS="

#Install file follow Debian jessies
do_install() {
	#Create new folders
	install -d ${D}${libdir}
	install -d ${D}${mandir}/man3
	oe_runmake 'DESTDIR=${D}' 'LIB_INSTALL_DIR=${D}${libdir}' \
		   'MAN_INSTALL_DIR= ${D}${mandir}/man3' install

	#correct the permission of /usr/lib/libefence.so.0.0
	LINKLIB=$(basename $(readlink ${D}${libdir}/libefence.so))
	LINKLIB_2=$(basename $(readlink ${D}${libdir}/$LINKLIB))
	chmod 0644 ${D}${libdir}/${LINKLIB_2}	
}
#Correct the sub-package name
DEBIANNAME_${PN} = "electric-fence"
