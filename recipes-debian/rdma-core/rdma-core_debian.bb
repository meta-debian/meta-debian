#
# base recipe: meta-openembedded/meta-networking/recipes-support/rdma-core/rdma-core_18.1.bb
# base branch: master
# base commit: 0aff7abedc1c7727c88029a123107f4faf5ba4f1
#

SUMMARY = "Userspace support for InfiniBand/RDMA verbs"
DESCRIPTION = "This is the userspace components for the Linux Kernel's drivers Infiniband/RDMA subsystem."
HOMEPAGE = "https://github.com/linux-rdma/rdma-core"

inherit debian-package
require recipes-debian/sources/rdma-core.inc

LICENSE = "(BSD-2-Clause | GPLv2) & CC0-1.0 & MIT & (GPLv2 | BSD-3-Clause)"
LIC_FILES_CHKSUM = " \
    file://COPYING.BSD_MIT;md5=4cef3f976a07850eb6d2e5d1ef945e6b \
    file://COPYING.GPL2;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
    file://COPYING.BSD_FB;md5=0ec18bae1a9df92c8d6ae01f94a289ae \
    file://ccan/LICENSE.CCO;md5=7cd3c7ede97f351a268426770fb77e8b \
    file://ccan/LICENSE.MIT;md5=838c366f69b72c5df05c96dff79b35f2 \
    file://providers/hfi1verbs/hfiverbs.h;endline=56;md5=68992f2b98de617250b252ac1766637b \
    file://providers/ipathverbs/COPYING;md5=ecb867951fede977db70f9e542a3d62e \
    file://providers/ocrdma/ocrdma_main.h;endline=33;md5=06080ecdf1d0bdb109d1721d7c4467bd \
"

DEPENDS = "libnl"

inherit cmake

EXTRA_OECMAKE = " \
    -DCMAKE_INSTALL_SYSTEMD_SERVICEDIR:PATH=${systemd_system_unitdir} \
"

OECMAKE_FIND_ROOT_PATH_MODE_PROGRAM = "BOTH"

FILES_${PN} += " \
    ${libdir}/*/*.so* \
    ${systemd_system_unitdir} \
"

RDEPENDS_${PN} = "perl bash"

# Avoid QA error about non -dev/-dbg/nativesdk- package contains symlink .so:
#   /usr/lib/libibverbs/libmlx{4,5}-rdmav19.so
# These files are not dev-so but alternative files.
INSANE_SKIP_${PN} += "dev-so"
