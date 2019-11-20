#
# base recipe: meta/recipes-core/glib-2.0/glib-2.0_2.56.1.bb
# base branch: master
# base commit: 63a4ff7cf5f7d1671ab85800bc2212dd9cd9748d
#

require recipes-core/glib-2.0/glib.inc

inherit debian-package
require recipes-debian/sources/glib2.0.inc
BPN = "glib2.0"
DEBIAN_UNPACK_DIR = "${WORKDIR}/glib-${PV}"

FILESPATH_append = ":${COREBASE}/meta/recipes-core/glib-2.0/glib-2.0"
SRC_URI += " \
    file://run-ptest \
    file://uclibc_musl_translation.patch \
    file://Enable-more-tests-while-cross-compiling.patch \
    file://0001-Install-gio-querymodules-as-libexec_PROGRAM.patch \
    file://0010-Do-not-hardcode-python-path-into-various-tools.patch \
    file://0001-meson.build-do-not-hardcode-linux-as-the-host-system.patch \
    file://0001-meson-do-a-build-time-check-for-strlcpy-before-attem.patch \
    file://glib-meson.cross \
"
SRC_URI_append_class-natve = "file://relocate-modules.patch"

do_write_config_append_class-nativesdk() {
    sed -e "/^c_args/ s|c_args = \[|c_args = \['$(echo ${TOOLCHAIN_OPTIONS} | sed -e 's/^ *//')', |" \
        -e "/^c_link_args/ s|c_link_args = |c_link_args = \['$(echo ${TOOLCHAIN_OPTIONS} | sed -e 's/^ *//')', |" \
        -e "/^c_link_args/ s|$|\]|" \
        -e "/^cpp_args/ s|cpp_args = \[|cpp_args = \['$(echo ${TOOLCHAIN_OPTIONS} | sed -e 's/^ *//')', |" \
        -e "/^cpp_link_args/ s|cpp_link_args = |cpp_link_args = \['$(echo ${TOOLCHAIN_OPTIONS} | sed -e 's/^ *//')', |" \
        -e "/^cpp_link_args/ s|$|\]|" \
        -i ${WORKDIR}/meson.cross
}
