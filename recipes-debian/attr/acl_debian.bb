SUMMARY = "Utilities for managing POSIX Access Control Lists"
HOMEPAGE = "http://savannah.nongnu.org/projects/acl/"
SECTION = "libs"

LICENSE = "LGPLv2.1+ & GPLv2+"
LICENSE_${PN} = "GPLv2+"
LICENSE_lib${BPN} = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://doc/COPYING;md5=c781d70ed2b4d48995b790403217a249 \
                    file://doc/COPYING.LGPL;md5=9e9a206917f8af112da634ce3ab41764"

DEPENDS = "attr"

inherit debian-package
require recipes-debian/sources/acl.inc

SRC_URI += "file://run-ptest"

inherit autotools gettext ptest

BBCLASSEXTEND = "native nativesdk"

PACKAGES =+ "lib${BPN}"
FILES_lib${BPN} = "${libdir}/lib*${SOLIBS}"

do_install_ptest() {
	install -Dm755 ${WORKDIR}/run-ptest ${D}${PTEST_PATH}
	cp -r ${S}/test ${D}${PTEST_PATH}
	rm ${D}${PTEST_PATH}/test/runwrapper
}

RDEPENDS_${PN}-ptest = "\
	acl \
	perl-module-cwd \
	perl-module-file-basename \
	perl-module-file-path \
	perl-module-filehandle \
	perl-module-file-spec \
	perl-module-constant \
	perl-module-getopt-std \
	perl-module-posix \
	e2fsprogs-mke2fs \
"
