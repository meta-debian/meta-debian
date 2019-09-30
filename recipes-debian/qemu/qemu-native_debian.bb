# base recipe: meta/recipes-devtools/qemu/qemu-native_3.1.0.bb
# base branch: warrior

require qemu-native.inc

EXTRA_OECONF_append = " \
    --target-list=${@get_qemu_usermode_target_list(d)} \
    --disable-tools --disable-blobs --disable-guest-agent \
"

PACKAGECONFIG ??= ""
