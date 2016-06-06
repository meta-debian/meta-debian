SUMMARY = "MySQL database"
DESCRIPTION = "\
MySQL is a fast, stable and true multi-user, multi-threaded SQL database \
server. SQL (Structured Query Language) is the most popular database query \
language in the world. The main goals of MySQL are speed, robustness and \
ease of use \
"
HOMEPAGE = "http://dev.mysql.com/"
PR = "r0"
inherit debian-package native

LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=751419260aa954499f7abaabaa882bbe"
inherit cmake

DPN = "mysql-5.5"

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
