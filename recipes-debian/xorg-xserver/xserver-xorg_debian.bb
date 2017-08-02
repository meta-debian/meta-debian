#
# base recipe: meta/recipes-graphics/xorg-xserver/xserver-xorg.inc
# base branch: morty
#

SUMMARY = "Xorg X server"

inherit debian-package
PV = "1.16.4"
DPN = "xorg-server"

LICENSE = "MIT-X"
LIC_FILES_CHKSUM = "file://COPYING;md5=15b5bf9327341c81089137aec4830bfd"

# xf86-*-* packages depend on an X server built with the xfree86 DDX
# so we have a virtual to represent that:
# deprecated, we should use virtual/xserver instead
PROVIDES = "virtual/xserver-xf86"

# Other packages tend to just care that there is *an* X server:
PROVIDES += "virtual/xserver"

DEBIAN_PATCH_TYPE = "quilt"

# macro_tweak.patch:
#   correct a include directory path for sysroot
#   Without this patch, do_qa_configure of other X packages
#   that use xorg-server.m4 may fail because of the following warning:
#   "/usr/include/xorg" is unsafe for cross-compilation
SRC_URI += "file://macro_tweak.patch"

inherit autotools pkgconfig

inherit distro_features_check
REQUIRED_DISTRO_FEATURES = "x11"

PROTO_DEPS = "randrproto renderproto fixesproto damageproto xextproto xproto \
              xf86dgaproto xf86vidmodeproto compositeproto recordproto resourceproto \
              videoproto scrnsaverproto  xineramaproto fontsproto kbproto inputproto \
              bigreqsproto xcmiscproto presentproto dri2proto"
LIB_DEPS = "pixman libxfont xtrans libxau libxext libxdmcp libdrm libxkbfile \
            libpciaccess openssl libgcrypt"
DEPENDS = "${PROTO_DEPS} ${LIB_DEPS} font-util"

# Configure base on debian/rules
EXTRA_OECONF = "--disable-silent-rules \
                --disable-static \
                --without-dtrace \
                --disable-strict-compilation \
                --disable-debug \
                --disable-unit-tests \
                --with-int10=x86emu \
                --with-os-vendor="Debian" \
                --with-builderstring="${DPN} ${PV}" \
                --with-xkb-path=${datadir}/X11/xkb \
                --with-xkb-output=${localstatedir}/lib/xkb \
                --with-shared-memory-dir=/dev/shm \
                --disable-install-libxf86config \
                --enable-mitshm \
                --enable-xres \
                --disable-tslib \
                --enable-dbe \
                --disable-xf86bigfont \
                --enable-dpms \
                --disable-config-hal \
                --enable-xorg \
                --disable-linux-acpi \
                --disable-linux-apm \
                --disable-xquartz \
                --disable-xwin \
                --disable-xfake \
                --disable-xfbdev \
                --disable-install-setuid \
                --with-default-font-path="${datadir}/fonts/X11/misc,${datadir}/fonts/X11/cyrillic,${datadir}/fonts/X11/100dpi/:unscaled,${datadir}/fonts/X11/75dpi/:unscaled,${datadir}/fonts/X11/Type1,${datadir}/fonts/X11/100dpi,${datadir}/fonts/X11/75dpi,built-ins" \
                --enable-aiglx \
                --enable-registry \
                --enable-composite \
                --enable-record \
                --enable-xv \
                --enable-xvmc \
                --enable-dga \
                --enable-screensaver \
                --enable-xdmcp \
                --enable-xdm-auth-1 \
                --enable-present \
                --enable-xf86vidmode \
                --enable-xace \
                --enable-xfree86-utils \
                --enable-xvfb \
                --enable-xnest \
                --enable-kdrive \
                --enable-xcsecurity \
                "

PACKAGECONFIG ??= "dmx dri2 dri3 udev xephyr xinerama xshmfence ${XORG_CRYPTO} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'dri glamor glx', '', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'xselinux', '', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'xwayland', '', d)} \
                   "
PACKAGECONFIG[dmx] = "--enable-dmx,--disable-dmx,libdmx libxtst libxres"
PACKAGECONFIG[dri] = "--enable-dri,--disable-dri,glproto virtual/mesa xf86driproto"
PACKAGECONFIG[dri2] = "--enable-dri2,--disable-dri2,dri2proto"
PACKAGECONFIG[dri3] = "--enable-dri3,--disable-dri3,x11proto-dri3 libxshmfence"
PACKAGECONFIG[glamor] = "--enable-glamor,--disable-glamor,libepoxy virtual/egl ,libegl-mesa"
PACKAGECONFIG[glx] = "--enable-glx --enable-glx-tls,--disable-glx,glproto virtual/libgl virtual/libx11"
PACKAGECONFIG[udev] = "--enable-config-udev,--disable-config-udev,udev"
PACKAGECONFIG[xephyr] = "--enable-xephyr,--disable-xephyr,libxcb xcb-util xcb-util-image xcb-util-keysyms xcb-util-wm"
PACKAGECONFIG[xinerama] = "--enable-xinerama,--disable-xinerama,xineramaproto"
PACKAGECONFIG[xselinux] = "--enable-xselinux,--disable-xselinux,libselinux"
PACKAGECONFIG[xwayland] = "--enable-xwayland,--disable-xwayland,wayland libepoxy"

# Xorg requires a SHA1 implementation, pick one.
# Debian choosed "gcrypt".
XORG_CRYPTO ??= "gcrypt"
PACKAGECONFIG[openssl] = "--with-sha1=libcrypto,,openssl"
PACKAGECONFIG[nettle] = "--with-sha1=libnettle,,nettle"
PACKAGECONFIG[gcrypt] = "--with-sha1=libgcrypt,,libgcrypt"

do_install_append () {
	install -m 755 ${S}/debian/local/xvfb-run ${D}${bindir}/
	install ${S}/debian/local/xvfb-run.1 ${D}${mandir}/man1/

	install -d ${D}${base_libdir}/udev/rules.d
	install -m 644 ${S}/debian/local/64-xorg-xkb.rules ${D}${base_libdir}/udev/rules.d/

	install -m 755 ${S}/debian/local/dh_xsf_substvars ${D}${bindir}/
	install -D -m 644 ${S}/debian/local/xsf.pm ${D}${datadir}/perl5/Debian/Debhelper/Sequence/xsf.pm
}

# Add runtime provides for the ABI versions of the video and input subsystems,
# so that drivers can depend on the relevant version.
python populate_packages_prepend() {
    import subprocess

    # Set PKG_CONFIG_PATH so pkg-config looks at the .pc files that are going
    # into the new package, not the staged ones.
    newenv = dict(os.environ)
    newenv["PKG_CONFIG_PATH"] = d.expand("${PKGD}${libdir}/pkgconfig/")

    def get_abi(name):
        abis = {
          "video": "abi_videodrv",
          "input": "abi_xinput"
        }
        p = subprocess.Popen(args="pkg-config --variable=%s xorg-server" % abis[name],
                             shell=True, env=newenv, stdout=subprocess.PIPE)
        stdout, stderr = p.communicate()
        output = stdout.decode("utf-8").split(".")[0]
        mlprefix = d.getVar('MLPREFIX', True) or ''
        return "%sxorg-abi-%s-%s" % (mlprefix, name, output)

    pn = d.getVar("PN", True)
    d.appendVar("RPROVIDES_" + pn, " " + get_abi("input"))
    d.appendVar("RPROVIDES_" + pn, " " + get_abi("video"))
}

PACKAGES =+ "xdmx xdmx-tools xnest ${DPN}-source xserver-common xserver-xephyr xvfb xwayland"
FILES_xdmx = "${bindir}/Xdmx"
FILES_xdmx-tools = "${bindir}/dmx* \
                    ${bindir}/vdltodmx \
                    ${bindir}/xdmxconfig \
                    "
FILES_xnest = "${bindir}/Xnest"
FILES_${DPN}-source = "${prefix}/src"
FILES_xserver-common = "${libdir}/xorg/protocol.txt"
FILES_xserver-xephyr = "${bindir}/Xephyr"
FILES_xvfb = "${bindir}/Xvfb \
              ${bindir}/xvfb-run \
              "
FILES_xwayland = "${bindir}/Xwayland"
FILES_${PN} += "${base_libdir}/udev/rules.d \
                ${libdir}/xorg/modules \
                ${datadir}/X11/xorg.conf.d \
                "
FILES_${PN}-dev += "${bindir}/dh_xsf_substvars \
                    ${libdir}/xorg/modules/*.la \
                    ${libdir}/xorg/modules/*/*.la \
                    ${datadir}/perl5/Debian/Debhelper/Sequence/xsf.pm \
                    "
FILES_${PN}-doc += "${localstatedir}/lib/xkb/README.compiled"

RDEPENDS_${PN} += "xserver-common keyboard-configuration"
RDEPENDS_xdmx += "xserver-common"
RDEPENDS_xdmx-tools += "xdmx"
RDEPENDS_xnest += "xserver-common"
RDEPENDS_xvfb += "xserver-common"
RDEPENDS_xserver-xephyr += "xserver-common"
RDEPENDS_xwayland += "xserver-common"
RDEPENDS_xserver-common += "xkb-data xkbcomp"

PKG_${PN} = "${PN}-core"
PKG_${PN}-dbg = "${PN}-core-dbg"
RPROVIDES_${PN} = "${PN}-core"
RPROVIDES_${PN}-dbg = "${PN}-core-dbg"


# Split out some modules and extensions from the main package
# These aren't needed for basic operations and only take up space:
#  76k	libexa.so
#  196k	libglx.so
#  124k	libint10.so
#  168k	libwfb.so
PACKAGES =+ "${PN}-multimedia-modules \
             ${PN}-extension-glx \
             ${PN}-module-libint10 \
             ${PN}-module-libwfb \
             ${PN}-module-exa \
             "

FILES_${PN}-multimedia-modules = "${libdir}/xorg/modules/multimedia/*drv*"
FILES_${PN}-extension-glx = "${libdir}/xorg/modules/extensions/libglx.so"
FILES_${PN}-module-libint10 = "${libdir}/xorg/modules/libint10.so"
FILES_${PN}-module-libwfb = "${libdir}/xorg/modules/libwfb.so"
FILES_${PN}-module-exa = "${libdir}/xorg/modules/libexa.so"
