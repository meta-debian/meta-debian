SUMMARY = "Apache HTTP Server"
DESCRIPTION = "The Apache HTTP Server Project's goal is to build a secure, efficient and \
extensible HTTP server as standards-compliant open source software. The \
result has long been the number one web server on the Internet. \
Installing this package results in a full installation, including the \
configuration files, init scripts and support scripts."

INC_PR = "r0"

inherit debian-package
PV = "2.4.10"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=dbff5a2b542fa58854455bf1a0b94b83"

inherit autotools pkgconfig perlnative

# Correct installation and library path.
# Option ap_cv_void_ptr_lt_long=no:
#	Avoid error: Size of "void *" is less than size of "long"
EXTRA_OECONF = " \
    --prefix=${base_prefix} \
    --localstatedir=${localstatedir}/${DPN} \
    --includedir=${includedir}/${DPN} \
    --datadir=${datadir}/${DPN} \
    --sysconfdir=${sysconfdir}/${DPN} \
    --libexecdir=${libexecdir}/modules \
    ap_cv_void_ptr_lt_long=no \
"

do_debian_patch_append() {
	# Apply patch for suexec
	cp ${S}/support/suexec.c ${S}/support/suexec-custom.c
	patch -p1 -i ${S}/debian/patches/suexec-custom.patch
}
