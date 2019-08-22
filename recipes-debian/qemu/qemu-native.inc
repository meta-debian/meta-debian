# base recipe: meta/recipes-devtools/qemu/qemu-native.inc
# base branch: warrior

inherit native

require qemu.inc

DEPENDS = "glib-2.0-native"

EXTRA_OECONF_append = " --python=python2.7"
EXTRA_OEMAKE_append = " LD='${LD}' AR='${AR}' OBJCOPY='${OBJCOPY}' LDFLAGS='${LDFLAGS}'"

LDFLAGS_append = " -fuse-ld=bfd"

do_install_append() {
	${@bb.utils.contains('PACKAGECONFIG', 'gtk+', 'make_qemu_wrapper', '', d)}
}
