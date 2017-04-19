#
# base recipe: meta-qt5/recipes-qt/qt5/qtbase-native_5.3.2.bb
# base commit: c9a1041cb956d94c04cbf635b00ca19725ffc129
#

require qtbase-opensource-src.inc
PR = "${INC_PR}.0"

DEPENDS = "zlib-native dbus-native"
QT_MODULE = "qtbase"

inherit native

SRC_URI += " \
    file://0012-Always-build-uic.patch \
"

QT_CONF_PATH = "${B}/qt.conf"

do_generate_qt_config_file() {
	:
}

EXTRA_OECONF = " \
    -prefix ${prefix} \
    -sysroot ${STAGING_DIR_NATIVE} \
    -no-gcc-sysroot \
    -system-zlib \
    -no-libjpeg \
    -no-libpng \
    -no-gif \
    -no-accessibility \
    -no-cups \
    -no-nis \
    -no-gui \
    -no-qml-debug \
    -no-sql-mysql \
    -no-sql-sqlite \
    -no-opengl \
    -no-openssl \
    -no-xcb \
    -no-icu \
    -verbose \
    -release \
    -prefix ${OE_QMAKE_PATH_PREFIX} \
    -bindir ${OE_QMAKE_PATH_BINS} \
    -libdir ${OE_QMAKE_PATH_LIBS} \
    -headerdir ${OE_QMAKE_PATH_HEADERS} \
    -archdatadir ${OE_QMAKE_PATH_ARCHDATA} \
    -datadir ${OE_QMAKE_PATH_DATA} \
    -docdir ${OE_QMAKE_PATH_DOCS} \
    -sysconfdir ${OE_QMAKE_PATH_SETTINGS} \
    -no-glib \
    -no-iconv \
    -silent \
    -nomake examples \
    -nomake tests \
    -no-rpath \
    -platform linux-oe-g++ \
"

do_configure_prepend() {
	(echo o; echo yes) | ${S}/configure ${EXTRA_OECONF} || die "Configuring qt failed. EXTRA_OECONF was ${EXTRA_OECONF}"
	bin/qmake ${OE_QMAKE_DEBUG_OUTPUT} ${S} -o Makefile || die "Configuring qt with qmake failed. EXTRA_OECONF was ${EXTRA_OECONF}"
}

do_install() {
	# Fix install paths for all
	find -name "Makefile*" | xargs sed -i "s,(INSTALL_ROOT)${STAGING_DIR_NATIVE}${STAGING_DIR_NATIVE},(INSTALL_ROOT)${STAGING_DIR_NATIVE},g"

	oe_runmake install INSTALL_ROOT=${D}

	if [ -d ${D}${STAGING_DIR_NATIVE}${STAGING_DIR_NATIVE} ] ; then
		echo "Some files are installed in wrong directory ${D}${STAGING_DIR_NATIVE}"
		cp -ra ${D}${STAGING_DIR_NATIVE}${STAGING_DIR_NATIVE}/* ${D}${STAGING_DIR_NATIVE}
		rm -rf ${D}${STAGING_DIR_NATIVE}${STAGING_DIR_NATIVE}
		# remove empty dirs
		TMP=`dirname ${D}/${STAGING_DIR_NATIVE}${STAGING_DIR_NATIVE}`
		while test ${TMP} != ${D}${STAGING_DIR_NATIVE}; do
			rmdir ${TMP}
			TMP=`dirname ${TMP}`;
		done
	fi

	# for modules which are still using syncqt and call qtPrepareTool(QMAKE_SYNCQT, syncqt)
	# e.g. qt3d, qtwayland
	ln -sf syncqt.pl ${D}${OE_QMAKE_PATH_QT_BINS}/syncqt
}

PROVIDES += "qtbase-native"
