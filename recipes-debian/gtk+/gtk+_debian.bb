require gtk+.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=3bf50002aefd002f49e7bb854063f7e7 \
                    file://gtk/gtk.h;endline=27;md5=c59e0b4490dd135a5726ebf851f9b17f \
                    file://gdk/gdk.h;endline=27;md5=07db285ec208fb3e0bf7d861b0614202 \
                    file://tests/testgtk.c;endline=27;md5=262db5db5f776f9863e56df31423e24c"
#SRC_URI = "http://ftp.gnome.org/pub/gnome/sources/gtk+/2.24/gtk+-${PV}.tar.xz \
#           file://xsettings.patch \
#           file://hardcoded_libtool.patch \
#           file://toggle-font.diff;striplevel=0 \
#           file://doc-fixes.patch \
#           file://strict-prototypes.patch \
#          "

SRC_URI[md5sum] = "b1e890e15602c5bcb40d85b13fe046fd"
SRC_URI[sha256sum] = "20cb10cae43999732a9af2e9aac4d1adebf2a9c2e1ba147050976abca5cd24f4"

EXTRA_OECONF = "--enable-xkb --disable-glibtest --disable-cups --disable-xinerama"

LIBV = "2.10.0"

PACKAGES_DYNAMIC += "^gtk-immodule-.* ^gtk-printbackend-.*"

python populate_packages_prepend () {
    gtk_libdir = d.expand('${libdir}/gtk-2.0/${LIBV}')
    immodules_root = os.path.join(gtk_libdir, 'immodules')
    printmodules_root = os.path.join(gtk_libdir, 'printbackends');

    d.setVar('GTKIMMODULES_PACKAGES', ' '.join(do_split_packages(d, immodules_root, '^im-(.*)\.so$', 'gtk-immodule-%s', 'GTK input module for %s')))
    do_split_packages(d, printmodules_root, '^libprintbackend-(.*)\.so$', 'gtk-printbackend-%s', 'GTK printbackend module for %s')

    if (d.getVar('DEBIAN_NAMES', True)):
        d.setVar(d.expand('PKG_${PN}'), '${MLPREFIX}libgtk-2.0')
}

#
#Meta-debian
#
inherit debian-package
PV = "2.24.25"
DPN = "gtk-2.0"

SRC_URI += " \
	   file://remove_docs.patch \
	   file://disable_HAVE_INTROSPECTION.patch \
           file://xsettings.patch \
           file://hardcoded_libtool.patch \
           file://toggle-font.diff;striplevel=0 \
           file://doc-fixes.patch \
           file://strict-prototypes.patch \
          "

do_configure_prepend() {
	sed -i -e "s/GOBJECT_INTROSPECTION_CHECK/#GOBJECT_INTROSPECTION_CHECK/g" ${S}/configure.ac
	sed -i -e "s/GTK_DOC_CHECK/#GTK_DOC_CHECK/g" ${S}/configure.ac
}

BBCLASSEXTEND = "native"
