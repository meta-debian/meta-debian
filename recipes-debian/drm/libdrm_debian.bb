#
# base recipe: meta/recipes-graphics/drm/libdrm_2.4.52.bb
# base branch: daisy
#

PR = "r1"

SUMMARY = "Userspace interface to the kernel DRM services"
DESCRIPTION = "The runtime library for accessing the kernel DRM services.  DRM \
stands for \"Direct Rendering Manager\", which is the kernel portion of the \
\"Direct Rendering Infrastructure\" (DRI).  DRI is required for many hardware \
accelerated OpenGL drivers."

LICENSE = "MIT"
LIC_FILES_CHKSUM = " \
file://xf86drm.c;beginline=9;endline=32;md5=c8a3b961af7667c530816761e949dc71 \
"

inherit debian-package
PV = "2.4.58"

DEBIAN_PATCH_TYPE = "quilt"

PROVIDES = "drm"
DEPENDS = "libpthread-stubs udev libpciaccess valgrind libbsd"

inherit autotools pkgconfig

EXTRA_OECONF = " \
    --enable-radeon \
    --disable-libkms \
    --enable-udev \
    --enable-vmwgfx \
    --enable-nouveau \
"
EXTRA_OECONF += " \
    --enable-intel \
    --disable-omap-experimental-api \
    --disable-freedreno-experimental-api \
    --disable-exynos-experimental-api \
"
EXTRA_OECONF_append_arm = " \
    --disable-intel \
    --enable-omap-experimental-api \
    --enable-freedreno-experimental-api \
    --enable-exynos-experimental-api \
"

ALLOW_EMPTY_${PN}-drivers = "1"
PACKAGES =+ "${PN}-tests ${PN}-drivers ${PN}-radeon ${PN}-nouveau ${PN}-omap \
             ${PN}-intel ${PN}-exynos ${PN}-freedreno"

RRECOMMENDS_${PN}-drivers = "${PN}-radeon ${PN}-nouveau ${PN}-omap ${PN}-intel \
                             ${PN}-exynos ${PN}-freedreno"

FILES_${PN}-tests = "${bindir}/dr* ${bindir}/mode* ${bindir}/*test"
FILES_${PN}-radeon = "${libdir}/libdrm_radeon.so.*"
FILES_${PN}-nouveau = "${libdir}/libdrm_nouveau.so.*"
FILES_${PN}-omap = "${libdir}/libdrm_omap.so.*"
FILES_${PN}-intel = "${libdir}/libdrm_intel.so.*"
FILES_${PN}-exynos = "${libdir}/libdrm_exynos.so.*"
FILES_${PN}-freedreno = "${libdir}/libdrm_freedreno.so.*"
