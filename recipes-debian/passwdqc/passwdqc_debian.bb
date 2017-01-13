DESCRIPTION = "password strength checking and policy enforcement toolset"

PR="r0"

inherit debian-package autotools-brokensep
PV = "1.3.0"

LICENSE = "BSD & PD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1b4af6f3d4ee079a38107366e93b334d"

DEPENDS += "libpam"

do_compile_prepend_class-target() {
	sed -i -e "s/^CC = gcc/CC = ${TARGET_SYS}-gcc/" ${S}/Makefile
}

# Add more package follow deian
PACKAGES =+ "libpam-${PN} libpasswdqc"

# Install files follow debian
FILES_libpasswdqc += "${base_libdir}/*.so.*"
FILES_libpam-${PN} += "${base_libdir}/security/*"
FILES_${PN}-dbg += "${base_libdir}/security/.debug"

# Rename package name follow debian
PKG_${PN}-dev = "lib${PN}-dev"
RPROVIDES_${PN}-dev += "lib${PN}-dev"
