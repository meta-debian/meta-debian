require recipes-core/ncurses/ncurses_5.9.bb
FILESEXTRAPATHS_prepend = "\
${COREBASE}/meta/recipes-core/ncurses/ncurses-5.9:\
"

inherit debian-package
DEBIAN_SECTION = "libs"
DPR = "1"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "\
file://ncurses/base/version.c;beginline=1;endline=27;md5=cbc180a8c44ca642e97c35452fab5f66\
"
SRC_URI += " \
file://tic-hang.patch \
file://config.cache \
file://exclude-host-includedir-from-CXX-test.patch \
"

# Set configure option --enable-overwrite to install
# headers to /usr/include instead of /usr/include/ncurses
# and /usr/include/ncursesw
do_configure_append_class-native () {
	ncurses_configure "narrowc" "--enable-overwrite"
}
