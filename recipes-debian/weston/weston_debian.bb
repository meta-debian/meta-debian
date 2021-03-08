SUMMARY = "reference implementation of a wayland compositor"
DESCRIPTION = "Part of the Wayland project is also the Weston reference implementation \
of a Wayland compositor. Weston can run as an X client or under Linux \
KMS and ships with a few demo clients. The Weston compositor is a minimal \
and fast compositor and is suitable for many embedded and mobile use \
cases."
HOMEPAGE = "http://wayland.freedesktop.org/"

inherit debian-package
PV = "1.6.0"

LICENSE = "MIT & CC-BY-SA-3.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=275efac2559a224527bd4fd593d38466 \
                    file://data/COPYING;md5=75141cf73eacffcf9fcc22c6eaf8f130"

DEBIAN_PATCH_TYPE = "nopatch"

DEPENDS = "wayland cairo libjpeg-turbo libxkbcommon pixman"

inherit autotools pkgconfig

# depends on virtual/egl
REQUIRED_DISTRO_FEATURES = "opengl"

PACKAGECONFIG ?= " \
    libinput \
    ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'launch', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'egl fbdev kms wayland', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11 wayland', 'xwayland', '', d)} \
"

EXTRA_OECONF = "--with-cairo=image"

PACKAGECONFIG[egl] = "--enable-egl --enable-simple-egl-clients,--disable-egl --disable-simple-egl-clients,virtual/egl"
PACKAGECONFIG[fbdev] = "--enable-fbdev-compositor,--disable-fbdev-compositor,udev mtdev"
PACKAGECONFIG[kms] = "--enable-drm-compositor,--disable-drm-compositor,drm udev virtual/mesa mtdev"
# weston-launch requires pam
PACKAGECONFIG[launch] = "--enable-weston-launch,--disable-weston-launch,drm"
PACKAGECONFIG[libinput] = "--enable-libinput-backend,--disable-libinput-backend,libinput"
PACKAGECONFIG[libunwind] = "--enable-libunwind,--disable-libunwind,libunwind"
PACKAGECONFIG[wayland] = "--enable-wayland-compositor,--disable-wayland-compositor,virtual/mesa"
PACKAGECONFIG[x11] = "--enable-x11-compositor,--disable-x11-compositor,virtual/libx11 libxcb libxcursor cairo"
PACKAGECONFIG[xwayland] = "--enable-xwayland,--disable-xwayland"

RDEPENDS_${PN} += "xkb-data libgl-mesa-dri"
