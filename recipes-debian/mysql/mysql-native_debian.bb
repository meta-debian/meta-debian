include mysql.inc

inherit native

EXTRA_OECMAKE += "-DSTACK_DIRECTION=-1 -DCAT_EXECUTABLE=`which cat` -DAWK_EXECUTABLE=`which awk`"
DEPENDS += "libaio libbsd"
do_generate_toolchain_file_append () {
    # If these are set cmake will assume we're cross-compiling, which will
    # result in certain things we want being disabled
    sed -i "/set( CMAKE_SYSTEM_NAME/d" ${WORKDIR}/toolchain.cmake
    sed -i "/set( CMAKE_SYSTEM_PROCESSOR/d" ${WORKDIR}/toolchain.cmake
}
do_install_append() {
	install -d ${D}${bindir}
	install -m 0755 sql/gen_lex_hash ${D}${bindir}/
	install -m 0755 extra/comp_err ${D}${bindir}/
	install -m 0755 scripts/comp_sql ${D}${bindir}/
}
