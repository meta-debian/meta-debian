#
# base recipe: meta-qt5/recipes-qt/qt5/qtbase_5.3.2.bb
# base commit: c9a1041cb956d94c04cbf635b00ca19725ffc129
#

require qtbase-opensource-src.inc
PR = "${INC_PR}.1"

inherit qmake5 pkgconfig

B = "${S}"

# If Qt5 (qtbase) is machine specific, then everything will be,
# because the (initial) qtbase configuration becomes part of Qt5/qmake
python __anonymous() {
    barch = d.getVar("BUILD_ARCH", True) or ''
    tarch = d.getVar("TARGET_ARCH", True) or ''
    # do not do anything if we are building a native package
    if barch != tarch:
        tarch = d.getVar("QT_PACKAGES_ARCH", True) or ''
        if tarch:
            d.setVar("PACKAGE_ARCH", tarch)
}

PATH_prepend = "${STAGING_DIR_NATIVE}${OE_QMAKE_PATH_QT_BINS}:"

# specific for qtbase
SRC_URI += "\
    file://0012-qmake-don-t-build-it-in-configure-but-allow-to-build.patch \
    file://use-pkg-config-for-psql-detection.patch \
"

DEPENDS += "qtbase-opensource-src-native"

# separate some parts of PACKAGECONFIG which are often changed
# be aware that you need to add icu to build qtwebkit, default
# PACKAGECONFIG is kept rather minimal for people who don't need
# stuff like webkit (and it's easier to add options than remove)

# gl or gles needs to be enabled in order to build qtdeclarative
# http://qt.gitorious.org/qt/qtdeclarative/commit/e988998a08b1420ed10bd02d9d4b3b8ed2289df9
PACKAGECONFIG_GL ?= "${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'gl kms', '', d)}"
PACKAGECONFIG_GL_arm ?= "${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'gles2 kms', '', d)}"
PACKAGECONFIG_FB ?= "${@bb.utils.contains('DISTRO_FEATURES', 'directfb', 'directfb', '', d)}"
PACKAGECONFIG_X11 ?= "${@bb.utils.contains('DISTRO_FEATURES', 'x11', \
	'xcb xvideo xsync xshape xrender xrandr xfixes xinput2 xinput xinerama xcursor gtkstyle xkb', '', d)}"
PACKAGECONFIG_FONTS ?= "freetype fontconfig"
PACKAGECONFIG_SYSTEM ?= "jpeg libpng zlib harfbuzz"
PACKAGECONFIG_MULTIMEDIA ?= "${@bb.utils.contains('DISTRO_FEATURES', 'pulseaudio', 'pulseaudio', '', d)}"
PACKAGECONFIG_DISTRO ?= "sql-mysql sql-psql sql-odbc sql-sqlite sql-tds \
                         icu glib accessibility examples linuxfb cups mtdev"
# Either release or debug, can be overridden in bbappends
PACKAGECONFIG_RELEASE ?= "release"
PACKAGECONFIG_OPENSSL ?= "openssl"
PACKAGECONFIG_DEFAULT ?= "dbus udev evdev widgets tools libs"

PACKAGECONFIG ?= " \
    ${PACKAGECONFIG_RELEASE} \
    ${PACKAGECONFIG_DEFAULT} \
    ${PACKAGECONFIG_OPENSSL} \
    ${PACKAGECONFIG_GL} \
    ${PACKAGECONFIG_FB} \
    ${PACKAGECONFIG_X11} \
    ${PACKAGECONFIG_FONTS} \
    ${PACKAGECONFIG_SYSTEM} \
    ${PACKAGECONFIG_MULTIMEDIA} \
    ${PACKAGECONFIG_DISTRO} \
"

PACKAGECONFIG[release] = "-release,-debug"
PACKAGECONFIG[developer] = "-developer-build"
PACKAGECONFIG[sm] = "-sm,-no-sm"
PACKAGECONFIG[tests] = "-make tests,-nomake tests"
PACKAGECONFIG[examples] = "-make examples -compile-examples,-nomake examples"
PACKAGECONFIG[tools] = "-make tools,-nomake tools"
# only for completeness, configure will add libs even if you try to explicitly remove it
PACKAGECONFIG[libs] = "-make libs,-nomake libs"
PACKAGECONFIG[accessibility] = "-accessibility,-no-accessibility"
PACKAGECONFIG[glib] = "-glib,-no-glib,glib-2.0"
# use either system freetype or bundled freetype, if you disable freetype completely
# fontdatabases/basic/qbasicfontdatabase.cpp will fail to build and system freetype
# works only together with fontconfig
PACKAGECONFIG[freetype] = "-system-freetype,-freetype,freetype"
PACKAGECONFIG[jpeg] = "-system-libjpeg,-no-libjpeg,libjpeg-turbo"
PACKAGECONFIG[libpng] = "-system-libpng,-no-libpng,libpng"
PACKAGECONFIG[harfbuzz] = "-system-harfbuzz,-no-harfbuzz,harfbuzz"
PACKAGECONFIG[zlib] = "-system-zlib,-qt-zlib,zlib"
PACKAGECONFIG[pcre] = "-system-pcre,-qt-pcre,pcre"
PACKAGECONFIG[gl] = "-opengl desktop,,virtual/libgl"
PACKAGECONFIG[gles2] = "-opengl es2,,virtual/libgles2 virtual/egl"
PACKAGECONFIG[tslib] = "-tslib,-no-tslib,tslib"
PACKAGECONFIG[dbus] = "-dbus -dbus-linked,-no-dbus,dbus"
PACKAGECONFIG[xcb] = "-xcb -xcb-xlib -system-xcb -qpa xcb,-no-xcb, \
                      libxcb xcb-util-wm xcb-util-image xcb-util-keysyms xcb-util-renderutil"
PACKAGECONFIG[sql-ibase] = "-plugin-sql-ibase,-no-sql-ibase"
PACKAGECONFIG[sql-mysql] = "-plugin-sql-mysql,-no-sql-mysql,mysql"
PACKAGECONFIG[sql-psql] = "-plugin-sql-psql,-no-sql-psql,postgresql"
PACKAGECONFIG[sql-odbc] = "-plugin-sql-odbc,-no-sql-odbc,unixodbc"
PACKAGECONFIG[sql-oci] = "-plugin-sql-oci,-no-sql-oci"
PACKAGECONFIG[sql-tds] = "-plugin-sql-tds,-no-sql-tds,freetds"
PACKAGECONFIG[sql-db2] = "-plugin-sql-db2,-no-sql-db2"
PACKAGECONFIG[sql-sqlite2] = "-plugin-sql-sqlite2,-no-sql-sqlite2,sqlite"
PACKAGECONFIG[sql-sqlite] = "-plugin-sql-sqlite -system-sqlite,-no-sql-sqlite,sqlite3"
PACKAGECONFIG[xcursor] = "-xcursor,-no-xcursor,libxcursor"
PACKAGECONFIG[xinerama] = "-xinerama,-no-xinerama,libxinerama"
PACKAGECONFIG[xinput] = "-xinput,-no-xinput"
PACKAGECONFIG[xinput2] = "-xinput2,-no-xinput2,libxi"
PACKAGECONFIG[xfixes] = "-xfixes,-no-xfixes,libxfixes"
PACKAGECONFIG[xrandr] = "-xrandr,-no-xrandr,libxrandr"
PACKAGECONFIG[xrender] = "-xrender,-no-xrender,libxrender"
PACKAGECONFIG[xshape] = "-xshape,-no-xshape"
PACKAGECONFIG[xsync] = "-xsync,-no-xsync"
PACKAGECONFIG[xvideo] = "-xvideo,-no-xvideo"
PACKAGECONFIG[openvg] = "-openvg,-no-openvg"
PACKAGECONFIG[iconv] = "-iconv,-no-iconv,virtual/libiconv"
PACKAGECONFIG[xkb] = "-xkb,-no-xkb -no-xkbcommon,libxkbcommon"
PACKAGECONFIG[evdev] = "-evdev,-no-evdev"
PACKAGECONFIG[mtdev] = "-mtdev,-no-mtdev,mtdev"
# depends on glib
PACKAGECONFIG[fontconfig] = "-fontconfig,-no-fontconfig,fontconfig"
PACKAGECONFIG[gtkstyle] = "-gtkstyle,-no-gtkstyle,gtk+"
PACKAGECONFIG[directfb] = "-directfb,-no-directfb,directfb"
PACKAGECONFIG[linuxfb] = "-linuxfb,-no-linuxfb"
PACKAGECONFIG[mitshm] = "-mitshm,-no-mitshm,mitshm"
PACKAGECONFIG[kms] = "-kms,-no-kms,virtual/mesa virtual/egl"
PACKAGECONFIG[icu] = "-icu,-no-icu,icu"
PACKAGECONFIG[udev] = "-libudev,-no-libudev,udev"
# use -openssl-linked here to ensure that RDEPENDS for libcrypto and libssl are detected
PACKAGECONFIG[openssl] = "-openssl -openssl-linked,-no-openssl,openssl"
PACKAGECONFIG[alsa] = "-alsa,-no-alsa,alsa-lib"
PACKAGECONFIG[pulseaudio] = "-pulseaudio,-no-pulseaudio,pulseaudio"
PACKAGECONFIG[nis] = "-nis,-no-nis"
PACKAGECONFIG[widgets] = "-widgets,-no-widgets"
PACKAGECONFIG[cups] = "-cups,-no-cups,cups"

do_generate_qt_config_file_append() {
	cat >> ${QT_CONF_PATH} <<EOF

[EffectivePaths]
Prefix=..
EOF
}

QMAKE_MKSPEC_PATH = "${B}"

# we need to run bin/qmake, because EffectivePaths are relative to qmake location
OE_QMAKE_QMAKE_ORIG = "${STAGING_DIR_NATIVE}${OE_QMAKE_PATH_BINS}/qmake"
OE_QMAKE_QMAKE = "bin/qmake"

# Base on debian/rules
# Compile without sse2 support on i386
# Do not use pre compiled headers in order to be able to rebuild the gui
# submodule.
cpu_opt = ""
cpu_opt_x86 = "-no-sse2 -no-pch"
# with -march=core2, __SSE2__ is predefined
# we need remove this definition or building will fail
OE_QMAKE_CFLAGS_append_x86 = " -U__SSE2__"
OE_QMAKE_CXXFLAGS_append_x86 = " -U__SSE2__"

do_configure() {
	# we need symlink in path relative to source, because
	# EffectivePaths:Prefix is relative to qmake location
	if [ ! -e ${B}/bin/qmake ]; then
		mkdir -p ${B}/bin
		ln -sf ${OE_QMAKE_QMAKE_ORIG} ${B}/bin/qmake
	fi

	oe_config="-sysroot ${STAGING_DIR_TARGET} \
	           -no-gcc-sysroot \
	           -external-hostbindir ${OE_QMAKE_PATH_EXTERNAL_HOST_BINS}
	           -xplatform linux-oe-g++ \
	           "
	${S}/configure \
	            -confirm-license \
	            -prefix "${OE_QMAKE_PATH_PREFIX}" \
	            -bindir "${OE_QMAKE_PATH_BINS}" \
	            -libdir "${OE_QMAKE_PATH_LIBS}" \
	            -docdir "${OE_QMAKE_PATH_DOCS}" \
	            -headerdir "${OE_QMAKE_PATH_HEADERS}" \
	            -datadir "${OE_QMAKE_PATH_DATA}" \
	            -archdatadir "${OE_QMAKE_PATH_ARCHDATA}" \
	            -hostdatadir "${OE_QMAKE_PATH_HOST_DATA}" \
	            -plugindir "${OE_QMAKE_PATH_PLUGINS}" \
	            -importdir "${OE_QMAKE_PATH_IMPORTS}" \
	            -translationdir "${OE_QMAKE_PATH_TRANSLATIONS}" \
	            -sysconfdir "${OE_QMAKE_PATH_SETTINGS}" \
	            -examplesdir "${OE_QMAKE_PATH_EXAMPLES}" \
	            -opensource \
	            -plugin-sql-psql \
	            -platform ${OE_QMAKESPEC} \
	            -no-rpath \
	            -verbose \
	            -optimized-qmake \
	            -reduce-relocations \
	            -no-strip \
	            -no-separate-debug-info \
	            ${cpu_opt} \
	            $oe_config \
	            ${EXTRA_OECONF}

	qmake5_base_do_configure
}

do_install_append() {
	# Remove native qmake
	rm -f ${D}/${libdir}/${QT_DIR_NAME}/bin/qmake

	if [ "${DPKG_ARCH}" = "i386" ]; then
		# Rebuild the necessary libs with SSE2 support.
		# Create the destination directory.
		install -d ${D}${OE_QMAKE_PATH_LIBS}/sse2/

		# corelib needs make clean first to be able to rebuild.
		cd ${B}/src/corelib; make clean; ${B}/bin/qmake -config sse2
		oe_runmake CC="${CC}" CXX="${CXX}"
		cp -av ${B}/lib/libQt5Core.so.* ${D}${OE_QMAKE_PATH_LIBS}/sse2/

		# gui on turn doesn't needs it, and actually fails if done.
		cd ${B}/src/gui; ${B}/bin/qmake -config sse2
		oe_runmake CC="${CC}" CXX="${CXX}"
		cp -av ${B}/lib/libQt5Gui.so.* ${D}${OE_QMAKE_PATH_LIBS}/sse2/
	fi

	# Add a configuration for qtchooser
	mkdir -p ${D}${OE_QMAKE_PATH_DATA}/qtchooser
	echo "${OE_QMAKE_PATH_BINS}" > ${D}${OE_QMAKE_PATH_DATA}/qtchooser/qt5.conf
	echo "${OE_QMAKE_PATH_LIBS}" >> ${D}${OE_QMAKE_PATH_DATA}/qtchooser/qt5.conf

	# Ship 5.conf and qt5.conf for this arch, and a default.conf.
	# 5.conf makes calling qtchooser prettier.
	mkdir -p ${D}${OE_QMAKE_PATH_LIBS}/qtchooser
	ln -sf ${OE_QMAKE_PATH_DATA}/qtchooser/qt5.conf ${D}${OE_QMAKE_PATH_LIBS}/qtchooser/5.conf
	ln -sf ${OE_QMAKE_PATH_DATA}/qtchooser/qt5.conf ${D}${OE_QMAKE_PATH_LIBS}/qtchooser/qt5.conf
	ln -sf ${OE_QMAKE_PATH_DATA}/qtchooser/qt5.conf ${D}${OE_QMAKE_PATH_LIBS}/qtchooser/default.conf

	# Remove libtool-like files
	rm -f ${D}${OE_QMAKE_PATH_LIBS}/*.la

	# A user of Qt built by a distro doesn't need to find where the plugins
	# are via CMake, so don't install them.
	rm -fv ${D}${OE_QMAKE_PATH_LIBS}/cmake/Qt5*/Q*Plugin.cmake

	# Remove bogus exec bits from some data files in mkspecs, docs, examples
	find ${D}${OE_QMAKE_PATH_DATA} ${D}${OE_QMAKE_PATH_ARCHDATA} \
		-perm /u+x,g+x,o+x -type f \
		-regex '.*\.\(app\|conf\|cpp\|h\|js\|php\|png\|pro\|xml\|xsl\)$' \
		-exec chmod a-x {} \;

	sed -i -e "s:${STAGING_DIR_TARGET}::g" ${D}${libdir}/pkgconfig/*.pc
}

# libpnp_basictools.a is used as example, but not a development file
INSANE_SKIP_qtbase5-examples += "staticdev"

PACKAGES = "${PN}-dbg ${PN}-staticdev \
             qt5-default qt5-qmake qtbase5-dev-tools qtbase5-examples \
             libqt5concurrent libqt5core libqt5dbus libqt5gui \
             libqt5network libqt5opengl libqt5printsupport \
             libqt5sql-ibase libqt5sql5-mysql libqt5sql5-odbc \
             libqt5sql5-psql libqt5sql5-sqlite libqt5sql5-tds \
             libqt5sql libqt5test libqt5widgets libqt5xml \
             qtbase5-private-dev libqt5opengl-dev ${PN}-dev \
             "

FILES_qt5-default = "${OE_QMAKE_PATH_LIBS}/qtchooser/default.conf"
FILES_qt5-qmake = " \
    ${OE_QMAKE_PATH_BINS}/qmake \
    ${OE_QMAKE_PATH_ARCHDATA}/mkspecs \
"
FILES_qtbase5-dev-tools = "${OE_QMAKE_PATH_BINS}/*"
FILES_qtbase5-examples = "${OE_QMAKE_PATH_EXAMPLES}"
FILES_libqt5concurrent = "${OE_QMAKE_PATH_LIBS}/libQt5Concurrent${SOLIBS}"
FILES_libqt5core = " \
    ${OE_QMAKE_PATH_LIBS}/libQt5Core${SOLIBS} \
    ${OE_QMAKE_PATH_LIBS}/qtchooser \
    ${OE_QMAKE_PATH_LIBS}/sse2/libQt5Core${SOLIBS} \
    ${OE_QMAKE_PATH_LIBS}/sse2/libQt5Core${SOLIBS} \
    ${OE_QMAKE_PATH_DATA}/qtchooser \
"
FILES_libqt5dbus = "${OE_QMAKE_PATH_LIBS}/libQt5DBus${SOLIBS}"
FILES_libqt5gui = " \
    ${OE_QMAKE_PATH_LIBS}/libQt5Gui${SOLIBS} \
    ${OE_QMAKE_PATH_PLUGINS}/generic/* \
    ${OE_QMAKE_PATH_PLUGINS}/imageformats/* \
    ${OE_QMAKE_PATH_PLUGINS}/platforminputcontexts/* \
    ${OE_QMAKE_PATH_PLUGINS}/platformthemes/* \
    ${OE_QMAKE_PATH_PLUGINS}/platforms/* \
    ${OE_QMAKE_PATH_LIBS}/sse2/libQt5Gui${SOLIBS} \
"
FILES_libqt5network = " \
    ${OE_QMAKE_PATH_LIBS}/libQt5Network${SOLIBS} \
    ${OE_QMAKE_PATH_PLUGINS}/bearer/* \
"
FILES_libqt5opengl = "${OE_QMAKE_PATH_LIBS}/libQt5OpenGL${SOLIBS}"
FILES_libqt5printsupport = " \
    ${OE_QMAKE_PATH_LIBS}/libQt5PrintSupport${SOLIBS} \
    ${OE_QMAKE_PATH_PLUGINS}/printsupport/* \
"
FILES_libqt5sql-ibase = "${OE_QMAKE_PATH_PLUGINS}/sqldrivers/libqsqlibase.so"
FILES_libqt5sql = "${OE_QMAKE_PATH_LIBS}/libQt5Sql${SOLIBS}"
FILES_libqt5sql5-mysql = "${OE_QMAKE_PATH_PLUGINS}/sqldrivers/libqsqlmysql.so"
FILES_libqt5sql5-odbc = "${OE_QMAKE_PATH_PLUGINS}/sqldrivers/libqsqlodbc.so"
FILES_libqt5sql5-psql = "${OE_QMAKE_PATH_PLUGINS}/sqldrivers/libqsqlpsql.so"
FILES_libqt5sql5-sqlite = "${OE_QMAKE_PATH_PLUGINS}/sqldrivers/libqsqlite.so"
FILES_libqt5sql5-tds = "${OE_QMAKE_PATH_PLUGINS}/sqldrivers/libqsqltds.so"
FILES_libqt5test = "${OE_QMAKE_PATH_LIBS}/libQt5Test${SOLIBS}"
FILES_libqt5widgets = " \
    ${OE_QMAKE_PATH_LIBS}/libQt5Widgets${SOLIBS} \
    ${OE_QMAKE_PATH_PLUGINS}/accessible/* \
"
FILES_libqt5xml = "${OE_QMAKE_PATH_LIBS}/libQt5Xml${SOLIBS}"
FILES_qtbase5-private-dev = " \
    ${OE_QMAKE_PATH_HEADERS}/*/*/*/private \
    ${OE_QMAKE_PATH_HEADERS}/*/*/*/qpa/qplatformopenglcontext.h \
    ${OE_QMAKE_PATH_HEADERS}/*/*/*/qpa/qplatform[a-np-z]*.h \
    ${OE_QMAKE_PATH_HEADERS}/*/*/*/qpa/qwindow* \
    ${OE_QMAKE_PATH_HEADERS}/QtPlatformSupport \
    ${OE_QMAKE_PATH_LIBS}/libQt5PlatformSupport.prl \
    ${OE_QMAKE_PATH_LIBS}/libQt5PlatformSupport.so \
    ${OE_QMAKE_PATH_LIBS}/pkgconfig/Qt5PlatformSupport.pc \
"
FILES_libqt5opengl-dev = " \
    ${OE_QMAKE_PATH_HEADERS}/QtOpenGL/*.h \
    ${OE_QMAKE_PATH_HEADERS}/QtOpenGL/QGL* \
    ${OE_QMAKE_PATH_HEADERS}/QtOpenGL/QtOpenGL \
    ${OE_QMAKE_PATH_HEADERS}/QtOpenGL/QtOpenGLVersion \
    ${OE_QMAKE_PATH_LIBS}/libQt5OpenGL.prl \
    ${OE_QMAKE_PATH_LIBS}/libQt5OpenGL.so \
    ${OE_QMAKE_PATH_LIBS}/pkgconfig/Qt5OpenGL.pc \
"
FILES_${PN}-dev += " \
    ${OE_QMAKE_PATH_HEADERS}/* \
    ${OE_QMAKE_PATH_LIBS}/*.prl \
    ${OE_QMAKE_PATH_LIBS}/cmake/* \
    ${OE_QMAKE_PATH_DOCS}/global \
"
FILES_${PN}-dbg += " \
    ${OE_QMAKE_PATH_BINS}/.debug \
    ${OE_QMAKE_PATH_LIBS}/*/.debug \
    ${OE_QMAKE_PATH_PLUGINS}/*/.debug \
    ${OE_QMAKE_PATH_EXAMPLES}/*/*/.debug \
    ${OE_QMAKE_PATH_EXAMPLES}/*/*/*/.debug \
    ${OE_QMAKE_PATH_EXAMPLES}/*/*/*/*/.debug \
    ${OE_QMAKE_PATH_EXAMPLES}/*/*/*/*/*/.debug \
"
FILES_${PN}-doc += "${OE_QMAKE_PATH_DOCS}"

DEBIANNAME_libqt5core = "libqt5core5a"
DEBIANNAME_libqt5opengl-dev = "libqt5opengl5-dev"
DEBIANNAME_${PN}-dev = "qtbase5-dev"

RPROVIDES_${PN}-dev += "qtbase5-dev"

# As Debian, qt5-default depends on qtbase5-dev
# skip checking dev-deps for qt5-default on do_package_qa
INSANE_SKIP_qt5-default += "dev-deps"

RDEPENDS_${PN}-dev = "qt5-qmake qtbase5-dev-tools"
RDEPENDS_qtbase5-private-dev += "${PN}-dev"
RDEPENDS_qt5-default += "${PN}-dev"
RDEPENDS_libqt5opengl-dev += "${PN}-dev"
