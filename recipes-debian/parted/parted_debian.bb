#
# base recipe: meta/recipes-extended/parted/parted_3.2.bb
# base branch: jethro
#

include parted.inc
PR = "${INC_PR}.0"

SRC_URI += " \
    file://run-ptest \
    file://Makefile \
"

inherit ptest

PACKAGES =+ "libparted libparted-fs-resize"

FILES_libparted = "${base_libdir}/libparted${SOLIBS}"
FILES_libparted-fs-resize = "${base_libdir}/libparted-fs-resize${SOLIBS}"

RDEPENDS_${PN} += "libparted"
RDEPENDS_libparted-fs-resize += "libparted"

DEBIANNAME_${PN}-dev = "libparted-dev"
DEBIANNAME_${PN}-dbg = "libparted2-dbg"

do_compile_ptest() {
	oe_runmake -C tests print-align print-max dup-clobber duplicate fs-resize
}

do_install_ptest() {
	t=${D}${PTEST_PATH}
	mkdir $t/build-aux
	cp ${S}/build-aux/test-driver $t/build-aux/
	cp -r ${S}/tests $t
	cp ${WORKDIR}/Makefile $t/tests/
	sed -i "s|^VERSION.*|VERSION = ${PV}|g" $t/tests/Makefile
	for i in print-align print-max dup-clobber duplicate fs-resize; do
		cp ${B}/tests/.libs/$i $t/tests/
	done
	sed -e 's| ../parted||' -i $t/tests/*.sh
}

RDEPENDS_${PN}-ptest = "bash coreutils perl mount python"
