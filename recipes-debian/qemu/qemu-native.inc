# base recipe: meta/recipes-devtools/qemu/qemu-native.inc
# base branch: warrior

inherit native

require qemu.inc

DEPENDS = "glib-2.0-native"

# Imported from meta-emlinux project to fix native build issues
# on Ubuntu 20.04, which are mainly backported from poky thud
SRC_URI += " \
    file://0001-Apply-patch-0001-linux-user-assume-__NR_gettid-alway.patch \
    file://0002-Apply-patch-0001-linux-user-rename-gettid-to-sys_get.patch \
    file://0003-Apply-patch-0011-linux-user-remove-host-stime-sysca.patch \
    file://0014-linux-user-fix-to-handle-variably-sized-SIOCGSTAMP-w-custom.patch \
"

EXTRA_OECONF_append = " --python=python2.7"
EXTRA_OEMAKE_append = " LD='${LD}' AR='${AR}' OBJCOPY='${OBJCOPY}' LDFLAGS='${LDFLAGS}'"

LDFLAGS_append = " -fuse-ld=bfd"

do_install_append() {
	${@bb.utils.contains('PACKAGECONFIG', 'gtk+', 'make_qemu_wrapper', '', d)}
}
