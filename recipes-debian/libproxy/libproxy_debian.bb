#
# base recipe: meta/recipes-support/libproxy/libproxy_0.4.11.bb
# base branch: jethro
#

SUMMARY = "automatic proxy configuration management library"
DESCRIPTION = "libproxy is a lightweight library which makes it easy to develop \
applications proxy-aware with a simple and stable API."
HOMEPAGE = "http://code.google.com/p/libproxy/"

PR = "r0"

inherit debian-package
PV = "0.4.11"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=4fbd65380cdd255951079008b364516c \
    file://utils/proxy.c;endline=18;md5=55152a1006d7dafbef32baf9c30a99c0 \
"

DEPENDS = "glib-2.0"

inherit cmake pkgconfig pythonnative distutils-base

# Configure follow debian/rules
SHLIBVER = "0.4.11"
EXTRA_OECMAKE = " \
    -DWITH_VALA=ON -DWITH_GNOME3=ON \
    -DCMAKE_SKIP_RPATH=ON \
    -DBIPR=0 \
    -DLIB_INSTALL_DIR=${libdir} \
    -DLIBEXEC_INSTALL_DIR=${libdir}/${DPN}/${SHLIBVER} \
    -DPYTHON_SITEPKG_DIR=${PYTHON_SITEPACKAGES_DIR} \
"

# Currently, we don't support for webkit, webkit gtk3, kde4, mozjs
EXTRA_OECMAKE += "-DWITH_WEBKIT=OFF -DWITH_WEBKIT3=OFF -DWITH_KDE4=OFF -DWITH_MOZJS=OFF"

# need to export these variables for python-config to work
export BUILD_SYS
export HOST_SYS
export STAGING_INCDIR
export STAGING_LIBDIR

python() {
    if incompatible_license_contains("GPLv3", "x", "", d) == "x" or bb.utils.contains("DISTRO_FEATURES", "x11", "x", "", d) == "":
        d.setVar("EXTRA_OECMAKE", d.getVar("EXTRA_OECMAKE", False).replace("-DWITH_GNOME3=ON", "-DWITH_GNOME3=OFF -DWITH_GNOME=OFF"))
}

PACKAGES =+ "${PN}-tools ${PN}-plugin-gsettings python-${PN}"

FILES_${PN}-tools = "${bindir}/*"
FILES_${PN}-plugin-gsettings = " \
    ${libdir}/${DPN}/${SHLIBVER}/modules/config_gnome3.so \
    ${libdir}/${DPN}/${SHLIBVER}/pxgsettings \
"
FILES_python-${PN} = "${PYTHON_SITEPACKAGES_DIR}/libproxy.py"
FILES_${PN} += "${datadir}/cmake"
FILES_${PN}-dev += "${datadir}/vala/vapi"
FILES_${PN}-dbg += " \
    ${libdir}/${DPN}/${SHLIBVER}/modules/.debug \
    ${libdir}/${DPN}/${SHLIBVER}/.debug \
"

RDEPENDS_${PN}-plugin-gsettings += "${PN}"
RDEPENDS_python-${PN} += "${PN}"

DEBIANNAME_${PN}-plugin-gsettings = "${PN}1-plugin-gsettings"
