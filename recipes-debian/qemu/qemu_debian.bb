# base recipe: meta/recipes-devtools/qemu/qemu_3.1.0.bb
# base branch: warrior

require qemu.inc

DEPENDS = "glib-2.0 pixman"

EXTRA_OECONF_append = " --target-list=${@get_qemu_target_list(d)}"

PACKAGECONFIG ??= " \
    fdt sdl kvm \
    ${@bb.utils.filter('DISTRO_FEATURES', 'alsa xen', d)} \
"
PACKAGECONFIG_class-nativesdk ??= "fdt sdl kvm"

do_install_append_class-nativesdk() {
	${@bb.utils.contains('PACKAGECONFIG', 'gtk+', 'make_qemu_wrapper', '', d)}
}

RDEPENDS_${PN}_class-target += "bash"

BBCLASSEXTEND = "nativesdk"
