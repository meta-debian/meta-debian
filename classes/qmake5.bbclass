#
# base class: meta-qt5/classes/qmake5.bbclass
# base commit: c9a1041cb956d94c04cbf635b00ca19725ffc129
#

#
# QMake variables for Qt
#
inherit qmake5_base

QT5TOOLSDEPENDS ?= "qtbase-opensource-src-native"
DEPENDS_prepend = "${QT5TOOLSDEPENDS} "

do_configure() {
    qmake5_base_do_configure
}

do_install() {
    qmake5_base_do_install
}
