FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
SRC_URI += "file://run-ptest"

inherit ptest

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
