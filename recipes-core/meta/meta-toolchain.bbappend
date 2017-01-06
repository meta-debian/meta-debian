create_sdk_files_append() {
	mkdir -p ${SDK_OUTPUT}${SDKPATHNATIVE}/environment-setup.d
	script=${SDK_OUTPUT}${SDKPATHNATIVE}/environment-setup.d/01-cross-menuconfig.sh

	cat <<EOF > ${script}
# Export the same variables as cml1.bbclass so that
# make menuconfig of kernel and busybox with this SDK properly
# finds native ncurses header and library from SDKPATHNATIVE.
#
# This script assumes that scripts/kconfig/lxdialog/check-lxdialog.sh
# in kernel and busybox source code you are trying to cross-build with
# this SDK is already patched to handle CROSS_CURSES_LIB and CROSS_CURSES_INC.
# See the following Yocto Project patches for kernel and busybox.
# http://git.yoctoproject.org/cgit/cgit.cgi/linux-yocto-4.4/commit?id=badf6fedf455958fe0ff3c060c8e3965ef6d80dc
# http://git.yoctoproject.org/cgit/cgit.cgi/linux-yocto-4.4/commit?id=26e282c0686e56cfc1c7eb4cc4c63275e49aad6e
# http://git.yoctoproject.org/cgit/cgit.cgi/poky/commit/?id=da6a4f6c2b2f108e52fcb546d68c8e16ec546b6c

export HOST_EXTRACFLAGS="-isystem${SDKPATHNATIVE}${includedir} -L${SDKPATHNATIVE}${libdir} -L${SDKPATHNATIVE}${base_libdir}"
export HOSTLDFLAGS="-L${SDKPATHNATIVE}${libdir} -L${SDKPATHNATIVE}${base_libdir}"
export CROSS_CURSES_LIB="-lncurses -ltinfo"
export CROSS_CURSES_INC='-DCURSES_LOC="<curses.h>"'
EOF
}