#
# base recipe: meta/recipes-devtools/python/python-pygtk_2.24.0.bb
# base branch: jethro
#

SUMMARY = "Python bindings for the GTK+ widget set"
DESCRIPTION = "This archive contains modules that allow you to use GTK+ in Python programs."
HOMEPAGE = "http://www.pygtk.org/"

PR = "r0"

inherit debian-package
PV = "2.24.0"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=a916467b91076e631dd8edb7424769c7"

DEPENDS = "gtk+ libglade pycairo python-pygobject"
RDEPENDS_${PN} = "python-shell pycairo python-pygobject"

# add-gtk-types.defs-into-gdk.c-dependence.patch:
# 	gdk.c depends on gtk-types.defs, so add gtk-types.defs into gdk.c dependences
# update-dependences-of-defs.c.patch:
# 	Add gdk-types.defs and gtk-types.defs to defs.c dependeces
# 	to avoid error when parallel compile.
# 	    | IOError: [Errno 2] No such file or directory: 'gtk-types.defs'
SRC_URI += " \
    file://add-gtk-types.defs-into-gdk.c-dependence.patch \
    file://update-dependences-of-defs.c.patch \
"

inherit autotools pkgconfig distutils-base distro_features_check

ANY_OF_DISTRO_FEATURES = "${GTK2DISTROFEATURES}"

export HOST_SYS
export BUILD_SYS

do_configure_prepend() {
	sed -i -e s:'`$PKG_CONFIG --variable defsdir pygobject-2.0`':\"${STAGING_DATADIR}/pygobject/2.0/defs\":g \
	       -e s:'`$PKG_CONFIG --variable=pygtkincludedir pygobject-2.0`':\"${STAGING_INCDIR}/pygtk-2.0\":g \
	       -e s:'`$PKG_CONFIG --variable=datadir pygobject-2.0`':\"${STAGING_DATADIR}\":g \
	       -e s:'`$PKG_CONFIG --variable codegendir pygobject-2.0`':\"${STAGING_DATADIR}/pygobject/2.0/codegen\":g \
	       -e s:'`$PKG_CONFIG --variable=fixxref pygobject-2.0`':\"${STAGING_DATADIR}/pygobject/xsl/fixxref.py\":g \
	       ${S}/configure.ac
}

do_install_append() {
	sed -i -e '1s|^#!.*python|#!/usr/bin/env python|' ${D}${bindir}/pygtk-demo

	# Follow debian/python-gtk2-doc.install
	mv ${D}${libdir}/pygtk/2.0/* ${D}${datadir}/pygtk/2.0/

	# Follow debian/python-gtk2-doc.examples
	mkdir -p ${D}${docdir}/python-gtk2-doc/examples
	olddir=`pwd`
	cd ${S}/examples
	cp -r atk glade gtk ide pango simple ${D}${docdir}/python-gtk2-doc/examples/
	cd $olddir

	# Follow debian/python-gtk2-doc.links
	ln -s ../gtk-doc/html/pygtk ${D}${docdir}/python-gtk2-doc/html
	ln -s ../../../pygtk/2.0/demos ${D}${docdir}/python-gtk2-doc/examples/demo

	find ${D} -name '*.py[co]' -print0 | xargs -0 rm -f
	find ${D} -name '*.la' -print0 | xargs -0 rm -f
}

PACKAGES =+ "python-glade2"
RDEPENDS_python-glade2 += "${PN}"

FILES_python-glade2 = "${libdir}/python*/*-packages/gtk-2.0/gtk/glade.so"
FILES_${PN}-dev += " \
    ${bindir}/pygtk-codegen-2.0 \
    ${datadir}/pygtk/2.0/defs/* \
"
FILES_${PN}-doc += " \
    ${bindir}/pygtk-demo \
    ${datadir}/pygtk/2.0/demos/* \
    ${datadir}/pygtk/2.0/pygtk-demo.py \
"
FILES_${PN}-dbg += " \
    ${libdir}/${PYTHON_DIR}/dist-packages/*/.debug \
    ${libdir}/${PYTHON_DIR}/dist-packages/*/*/.debug \
"

PKG_${PN} = "python-gtk2"
PKG_${PN}-dev = "python-gtk2-dev"
PKG_${PN}-doc = "python-gtk2-doc"
