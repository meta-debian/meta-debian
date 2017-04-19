SUMMARY = "unit test framework for C"
DESCRIPTION = "Check features a simple interface for defining unit tests, putting \
little in the way of the developer. Tests are run in a separate \
address space, so Check can catch both assertion failures and code \
errors that cause segmentation faults or other signals. The output \
from unit tests can be used within source code editors and IDEs."
HOMEPAGE = "http://check.sourceforge.net/"

PR = "r0"

inherit debian-package
PV = "0.9.10"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING.LESSER;md5=2d5025d4aa3495befef8f17206a5b0a1"

inherit autotools pkgconfig texinfo

B_pic = "${WORKDIR}/build-pic"

do_configure() {
	oe_runconf

	# Configure for pic
	test -d ${B_pic} || mkdir -p ${B_pic}
	cd ${B_pic}
	CFLAGS="-fPIC" oe_runconf
}

do_compile_append() {
	cd ${B_pic}
	oe_runmake
}

# Follow debian/rules
do_install_append() {
	cp ${B_pic}/src/.libs/libcheck.a ${D}${libdir}/libcheck_pic.a

	# fix the setup
	sed -i '/\/\* generated using gnu compiler gcc.*/d' ${D}${includedir}/check_stdint.h
	rm -f ${D}${libdir}/libcheck.so.* \
	      ${D}${libdir}/libcheck.so \
	      ${D}${libdir}/libcheck.la
	rm -rf ${D}${docdir}/check/example
}

BBCLASSEXTEND = "native"
