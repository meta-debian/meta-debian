require recipes-support/sqlite/sqlite3_3.8.3.1.bb

inherit debian-package
DEBIAN_SECTION = "devel"
DPR = "0"

LICENSE = "PD"
LIC_FILES_CHKSUM = " \
  file://src/sqlite.h.in;endline=11;md5=65f0a57ca6928710b418c094b3570bb0 \
"

DEPENDS += "tcl-native"
do_compile[depends] += "tcl-native:do_populate_sysroot"

# Required to avoid a compile error
SRC_URI += "file://fix-hardcode-libtool.patch"

do_compile_prepend_class-target () {
	export CROSS_BUILDING="yes"
}

do_install_prepend_class-target () {
	export CROSS_BUILDING="yes"
}
