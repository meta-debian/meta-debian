#
# base recipe: meta/recipes-graphics/mesa/mesa_10.6.3.bb
# base branch: jethro
#

SUMMARY = "free implementation of the OpenGL API"
HOMEPAGE = "http://mesa3d.sourceforge.net/"

inherit debian-package
PV = "10.3.2"
PR = "r1"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://docs/license.html;md5=6a23445982a7a972ac198e93cc1cb3de"

DEBIAN_PATCH_TYPE = "quilt"

DEPENDS = "expat flex-native bison-native udev"

PROVIDES = "virtual/libgl virtual/libgles1 virtual/libgles2 virtual/egl virtual/mesa"

inherit autotools pkgconfig pythonnative gettext distro_features_check

REQUIRED_DISTRO_FEATURES = "opengl"

PACKAGECONFIG ??= "dri dri3 egl gallium openvg r600 gles xa \
                ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11 xvmc vdpau', '', d)} \
                ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland', '', d)} \
                "

X11_DEPS = "glproto libxext libxdamage libxfixes libxxf86vm"
PACKAGECONFIG[x11] = "--enable-glx-tls,--disable-glx,${X11_DEPS}"
PACKAGECONFIG[xvmc] = "--enable-xvmc,--disable-xvmc,libxvmc"
PACKAGECONFIG[wayland] = ",,wayland"

DRI_DRIVERS = "nouveau"
DRI_DRIVERS_append_x86 = ",i915,i965,r200,radeon"
DRI_DRIVERS_append_x86-64 = ",i915,i965,r200,radeon"
DRI_DRIVERS_append_arm = ",swrast"
PACKAGECONFIG[dri] = "--enable-dri --enable-driglx-direct --with-dri-drivers=${DRI_DRIVERS}, \
                      --disable-dri, dri2proto libdrm"
PACKAGECONFIG[dri3] = "--enable-dri3, --disable-dri3, x11proto-dri3 presentproto libxshmfence"

PACKAGECONFIG[gles] = "--enable-gles1 --enable-gles2, --disable-gles1 --disable-gles2"

EGL_DISPLAYS  = "drm"
EGL_DISPLAYS .="${@bb.utils.contains('PACKAGECONFIG', 'x11', ',x11', '', d)}"
EGL_DISPLAYS .="${@bb.utils.contains('PACKAGECONFIG', 'wayland', ',wayland', '', d)}"
PACKAGECONFIG[egl] = "--enable-egl --with-egl-platforms=${EGL_DISPLAYS}, --disable-egl"

GALLIUM_DRIVERS = "nouveau,svga"
GALLIUM_DRIVERS_append_arm = ",freedreno"
PACKAGECONFIG[r600] = ""
GALLIUM_DRIVERS_LLVM = ",r300${@bb.utils.contains('PACKAGECONFIG', 'r600', ',radeonsi,r600', '', d)}"
GALLIUM_DRIVERS_append = "${@bb.utils.contains('PACKAGECONFIG', 'gallium-llvm', ',${GALLIUM_DRIVERS_LLVM}', '', d)}"
GALLIUM_DRIVERS_append_x86 = ",swrast"
GALLIUM_DRIVERS_append_x86-64 = ",swrast"

# keep --with-gallium-drivers separate, because when only one of gallium versions is enabled,
# other 2 were adding --without-gallium-drivers
PACKAGECONFIG[gallium]      = "--with-gallium-drivers=${GALLIUM_DRIVERS}, --without-gallium-drivers"
PACKAGECONFIG[gallium-llvm] = "--enable-gallium-llvm --enable-llvm-shared-libs, \
                               --disable-gallium-llvm, llvm-toolchain-3.5"
# cannot enable openvg or opencl without gallium
PACKAGECONFIG[openvg] = "--enable-openvg --enable-gallium-egl,--disable-openvg"
PACKAGECONFIG[opencl] = "--enable-opencl --enable-opencl-icd,--disable-opencl,libclc"

PACKAGECONFIG[xa]  = "--enable-xa, --disable-xa"
PACKAGECONFIG[vdpau]  = "--enable-vdpau, --disable-vdpau,libvdpau"

# llvmpipe is slow if compiled with -fomit-frame-pointer (e.g. -O2)
FULL_OPTIMIZATION_append = " -fno-omit-frame-pointer"

EXTRA_OECONF += " \
        --enable-osmesa \
        --enable-shared-glapi \
        --enable-texture-float \
        --disable-omx \
"

do_install_append() {
	# According to debian/libosmesa6.links.in
	ln -sf libOSMesa.so.8 ${D}${libdir}/libOSMesa.so.6
}

PACKAGES =+ "libxatracker libxatracker-dev libgbm libgbm-dev libegl-mesa libegl-mesa-dev \
             libegl-mesa-drivers libwayland-egl-mesa libopenvg-mesa libopenvg-mesa-dev \
             libgles1-mesa libgles1-mesa-dev libgles2-mesa libgles2-mesa-dev libglapi-mesa \
             libgl-mesa-glx libgl-mesa-dri libgl-mesa-dev libosmesa libosmesa-dev mesa-vdpau-drivers \
             "

FILES_libxatracker = "${libdir}/libxatracker${SOLIBS}"
FILES_libxatracker-dev = " \
    ${includedir}/xa_* \
    ${libdir}/libxatracker${SOLIBSDEV} \
    ${libdir}/pkgconfig/xatracker.pc \
"
FILES_libgbm = " \
    ${libdir}/libgbm${SOLIBS} \
    ${libdir}/gbm/* \
"
FILES_libgbm-dev = " \
    ${includedir}/gbm.h \
    ${libdir}/libgbm${SOLIBSDEV} \
    ${libdir}/pkgconfig/gbm.pc \
"
FILES_libegl-mesa = "${libdir}/libEGL${SOLIBS}"
FILES_libegl-mesa-dev = " \
    ${includedir}/EGL \
    ${includedir}/KHR \
    ${libdir}/libEGL${SOLIBSDEV} \
    ${libdir}/libwayland-egl${SOLIBSDEV} \
    ${libdir}/pkgconfig/*egl.pc \
"
FILES_libegl-mesa-drivers = "${libdir}/egl/*.so"
FILES_libwayland-egl-mesa = "${libdir}/libwayland-egl${SOLIBS}"
FILES_libopenvg-mesa = "${libdir}/libOpenVG${SOLIBS}"
FILES_libopenvg-mesa-dev = " \
    ${includedir}/VG \
    ${libdir}/libOpenVG${SOLIBSDEV} \
    ${libdir}/pkgconfig/vg.pc \
"
FILES_libgles1-mesa = "${libdir}/libGLESv1_CM${SOLIBS}"
FILES_libgles1-mesa-dev = " \
    ${includedir}/GLES \
    ${libdir}/libGLESv1_CM${SOLIBSDEV} \
    ${libdir}/pkgconfig/glesv1_cm.pc \
"
FILES_libgles2-mesa = "${libdir}/libGLESv2${SOLIBS}"
FILES_libgles2-mesa-dev = " \
    ${includedir}/GLES2 \
    ${includedir}/GLES3 \
    ${libdir}/libGLESv2${SOLIBSDEV} \
    ${libdir}/pkgconfig/glesv2.pc \
"
FILES_libglapi-mesa = "${libdir}/libglapi${SOLIBS}"
FILES_libgl-mesa-glx = "${libdir}/libGL${SOLIBS}"
FILES_libgl-mesa-dri = " \
    ${sysconfdir}/drirc \
    ${libdir}/dri/* \
"
FILES_libgl-mesa-dev = " \
    ${libdir}/libGL${SOLIBSDEV} \
    ${libdir}/pkgconfig/gl.pc \
"
FILES_libosmesa = "${libdir}/libOSMesa${SOLIBS}"
FILES_libosmesa-dev = " \
    ${includedir}/GL/osmesa.h \
    ${libdir}/libOSMesa${SOLIBSDEV} \
    ${libdir}/pkgconfig/osmesa.pc \
"
FILES_mesa-vdpau-drivers = "${libdir}/vdpau/*${SOLIBS}"

FILES_${PN}-dbg += "${libdir}/*/.debug"
FILES_${PN}-dev += "${libdir}/*/*.la ${libdir}/vdpau/*${SOLIBSDEV}"

# Remove the mesa dependency on mesa-dev, as mesa is empty
RDEPENDS_${PN}-dev = ""

# dependencies between mesa packages
RDEPENDS_libxatracker-dev += "libxatracker"
RDEPENDS_libgbm-dev += "libgbm"
RDEPENDS_libelg-mesa-dev += "libegl-mesa libegl-mesa-drivers"
RDEPENDS_libegl-mesa-drivers += " \
    libegl-mesa libglapi-mesa \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'libwayland-egl-mesa', '', d)} \
"
RDEPENDS_libwayland-egl-mesa += "libegl-mesa"
RDEPENDS_libopenvg-mesa-dev += "libopenvg-mesa libegl-mesa-dev"
RDEPENDS_libgles1-mesa += "libglapi-mesa"
RDEPENDS_libgles1-mesa-dev += "libgles1-mesa libegl-mesa-dev"
RDEPENDS_libgles2-mesa += "libglapi-mesa"
RDEPENDS_libgles2-mesa-dev += "libgles2-mesa libegl-mesa-dev"
RDEPENDS_libgl-mesa-glx += "libglapi-mesa"
RDEPENDS_libgl-mesa-dev += "mesa-common-dev libgl-mesa-glx"
RDEPENDS_libosmesa += "libglapi-mesa"
RDEPENDS_libosmesa-dev += "libosmesa mesa-common-dev"

DEBIANNAME_${PN}-dev = "mesa-common-dev"
DEBIANNAME_libegl-mesa = "libegl1-mesa"
DEBIANNAME_libegl-mesa-dev = "libegl1-mesa-dev"
DEBIANNAME_libegl-mesa-drivers = "libegl1-mesa-drivers"
DEBIANNAME_libwayland-egl-mesa = "libwayland-egl1-mesa"
DEBIANNAME_libopenvg-mesa = "libopenvg1-mesa"
DEBIANNAME_libopenvg-mesa-dev = "libopenvg1-mesa-dev"
DEBIANNAME_libgl-mesa-glx = "libgl1-mesa-glx"
DEBIANNAME_libgl-mesa-dri = "libgl1-mesa-dri"
DEBIANNAME_libgl-mesa-dev = "libgl1-mesa-dev"
DEBIANNAME_libosmesa = "libosmesa6"
DEBIANNAME_libosmesa-dev = "libosmesa6-dev"
DEBIAN_NOAUTONAME_libgles1-mesa = "1"
DEBIAN_NOAUTONAME_libgles1-mesa-dev = "1"
DEBIAN_NOAUTONAME_libgles2-mesa = "1"
DEBIAN_NOAUTONAME_libgles2-mesa-dev = "1"
DEBIAN_NOAUTONAME_libglapi-mesa = "1"
RPROVIDES_${PN}-dev = "mesa-common-dev"
