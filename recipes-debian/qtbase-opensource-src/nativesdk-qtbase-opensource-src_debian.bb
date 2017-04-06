#
# base recipe: meta-qt5/recipes-qt/qt5/nativesdk-qtbase_5.3.2.bb
# base commit: c9a1041cb956d94c04cbf635b00ca19725ffc129
#

require qtbase-opensource-src.inc
PR = "${INC_PR}.0"

DESCRIPTION = "SDK version of Qt/[X11|Mac|Embedded]"
DEPENDS = "nativesdk-zlib nativesdk-dbus qtbase-native"

# OEQt5Toolchain.cmake:
# 	CMake's toolchain configuration of nativesdk-qtbase
SRC_URI += " \
    file://0012-Always-build-uic.patch \
    file://0013-configure-preserve-built-qmake-and-swap-with-native-.patch \
    file://OEQt5Toolchain.cmake \
"

inherit nativesdk

PACKAGE_DEBUG_SPLIT_STYLE = "debug-without-src"

FILES_${PN}-dev += " \
    ${OE_QMAKE_PATH_ARCHDATA}/mkspecs \
"

FILES_${PN}-dbg += " \
    ${OE_QMAKE_PATH_BINS}/.debug \
"

FILES_${PN} += " \
    ${OE_QMAKE_PATH_BINS}/* \
    ${SDKPATHNATIVE}/environment-setup.d \
    ${datadir}/cmake \
"

QT_CONFIG_FLAGS += " \
    -shared \
    -silent \
    -no-pch \
    -no-rpath \
    -pkg-config \
    ${EXTRA_OECONF} \
"

# qtbase is exception, as these are used as install path for sysroots
OE_QMAKE_PATH_HOST_DATA = "${libdir}/${QT_DIR_NAME}"
OE_QMAKE_PATH_HOST_LIBS = "${libdir}"

do_generate_qt_config_file() {
    cat > ${QT_CONF_PATH} <<EOF
[Paths]
Prefix = ${OE_QMAKE_PATH_PREFIX}
Headers = ${OE_QMAKE_PATH_HEADERS}
Libraries = ${OE_QMAKE_PATH_LIBS}
ArchData = ${OE_QMAKE_PATH_ARCHDATA}
Data = ${OE_QMAKE_PATH_DATA}
Binaries = ${OE_QMAKE_PATH_BINS}
LibraryExecutables = ${OE_QMAKE_PATH_LIBEXECS}
Plugins = ${OE_QMAKE_PATH_PLUGINS}
Imports = ${OE_QMAKE_PATH_IMPORTS}
Qml2Imports = ${OE_QMAKE_PATH_QML}
Translations = ${OE_QMAKE_PATH_TRANSLATIONS}
Documentation = ${OE_QMAKE_PATH_DOCS}
Settings = ${OE_QMAKE_PATH_SETTINGS}
Examples = ${OE_QMAKE_PATH_EXAMPLES}
Tests = ${OE_QMAKE_PATH_TESTS}
HostBinaries = ${OE_QMAKE_PATH_HOST_BINS}
HostData = ${OE_QMAKE_PATH_HOST_DATA}
HostLibraries = ${OE_QMAKE_PATH_HOST_LIBS}
HostSpec = ${OE_QMAKESPEC}
TartgetSpec = ${OE_XQMAKESPEC}
ExternalHostBinaries = ${OE_QMAKE_PATH_EXTERNAL_HOST_BINS}
Sysroot =
EOF
}

do_generate_qt_config_file_append() {
    cat >> ${QT_CONF_PATH} <<EOF

[EffectivePaths]
Prefix=..
EOF
}

QMAKE_MKSPEC_PATH = "${B}"

# we need to run bin/qmake, because EffectivePaths are relative to qmake location
OE_QMAKE_QMAKE_ORIG = "${STAGING_LIBDIR_NATIVE}/${QT_DIR_NAME}/bin/qmake"
OE_QMAKE_QMAKE = "bin/qmake"

do_configure() {
    # we need symlink in path relative to source, because
    # EffectivePaths:Prefix is relative to qmake location
    # Also, configure expects qmake-native to swap with real one
    if [ ! -e ${B}/bin/qmake-native ]; then
        mkdir -p ${B}/bin
        ln -sf ${OE_QMAKE_QMAKE_ORIG} ${B}/bin/qmake-native
    fi

    ${S}/configure -v \
        -opensource -confirm-license \
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
        -verbose \
        -release \
        -prefix ${OE_QMAKE_PATH_PREFIX} \
        -bindir ${OE_QMAKE_PATH_BINS} \
        -libdir ${OE_QMAKE_PATH_LIBS} \
        -datadir ${OE_QMAKE_PATH_DATA} \
        -sysconfdir ${OE_QMAKE_PATH_SETTINGS} \
        -docdir ${OE_QMAKE_PATH_DOCS} \
        -headerdir ${OE_QMAKE_PATH_HEADERS} \
        -archdatadir ${OE_QMAKE_PATH_ARCHDATA} \
        -libexecdir ${OE_QMAKE_PATH_LIBEXECS} \
        -plugindir ${OE_QMAKE_PATH_PLUGINS} \
        -importdir ${OE_QMAKE_PATH_IMPORTS} \
        -qmldir ${OE_QMAKE_PATH_QML} \
        -translationdir ${OE_QMAKE_PATH_TRANSLATIONS} \
        -testsdir ${OE_QMAKE_PATH_TESTS} \
        -hostbindir ${OE_QMAKE_PATH_HOST_BINS} \
        -hostdatadir ${OE_QMAKE_PATH_HOST_DATA} \
        -external-hostbindir ${OE_QMAKE_PATH_EXTERNAL_HOST_BINS} \
        -no-glib \
        -no-iconv \
        -silent \
        -nomake examples \
        -nomake tests \
        -nomake libs \
        -no-compile-examples \
        -no-rpath \
        -platform ${OE_QMAKESPEC} \
        -xplatform linux-oe-g++ \
        ${QT_CONFIG_FLAGS}

    bin/qmake ${OE_QMAKE_DEBUG_OUTPUT} ${S} -o Makefile || die "Configuring qt with qmake failed. EXTRA_OECONF was ${EXTRA_OECONF}"
}

# Set the EXTRA_QTLIB variable to e.g. Xml, in order to not remove libQt5Xml.so.*
EXTRA_QTLIB ?= ""

python __anonymous () {
    templibs = ""
    for e in d.getVar("EXTRA_QTLIB", True).split():
        templibs = "%s -not -name 'libQt5%s.so*' -and" % (templibs, e)
    d.setVar("QTLIBSPRESERVE", templibs)
}

do_install() {
    # Fix install paths for all
    find -name "Makefile*" | xargs sed -i "s,(INSTALL_ROOT)${STAGING_DIR_NATIVE}${STAGING_DIR_NATIVE},(INSTALL_ROOT)${STAGING_DIR_NATIVE},g"

    oe_runmake install INSTALL_ROOT=${D}

    # replace the native qmake installed above with nativesdk version
    rm -rf ${D}${OE_QMAKE_PATH_HOST_BINS}/qmake
    install -m 755 ${B}/bin/qmake-real ${D}${OE_QMAKE_PATH_HOST_BINS}/qmake

    # for modules which are still using syncqt and call qtPrepareTool(QMAKE_SYNCQT, syncqt)
    # e.g. qt3d, qtwayland
    ln -sf syncqt.pl ${D}${OE_QMAKE_PATH_QT_BINS}/syncqt

    # remove things unused in nativesdk, we need the headers, Qt5Core
    # and Qt5Bootstrap.
    rm -rf ${D}${datadir} \
           ${D}/${OE_QMAKE_PATH_PLUGINS} \
           ${D}${libdir}/cmake \
           ${D}${libdir}/pkgconfig
    find ${D}${libdir} -maxdepth 1 -name 'lib*' -and \
                                   -not -name 'libQt5Core.so*' -and \
                                   ${QTLIBSPRESERVE} \
                                   -not -name 'libQt5Bootstrap.a' \
                                   -exec rm '{}' ';'

    # Install CMake's toolchain configuration
    mkdir -p ${D}${datadir}/cmake/OEToolchainConfig.cmake.d/
    install -m 644 ${WORKDIR}/OEQt5Toolchain.cmake ${D}${datadir}/cmake/OEToolchainConfig.cmake.d/
}

do_generate_qt_environment_file() {
    mkdir -p ${D}${SDKPATHNATIVE}/environment-setup.d/
    script=${D}${SDKPATHNATIVE}/environment-setup.d/qt5.sh

    echo 'export PATH=${OE_QMAKE_PATH_HOST_BINS}:$PATH' > $script
    echo 'export OE_QMAKE_CFLAGS="$CFLAGS"' >> $script
    echo 'export OE_QMAKE_CXXFLAGS="$CXXFLAGS"' >> $script
    echo 'export OE_QMAKE_LDFLAGS="$LDFLAGS"' >> $script
    echo 'export OE_QMAKE_CC=$CC' >> $script
    echo 'export OE_QMAKE_CXX=$CXX' >> $script
    echo 'export OE_QMAKE_LINK=$CXX' >> $script
    echo 'export OE_QMAKE_AR=$AR' >> $script
    echo 'export QT_CONF_PATH=${OE_QMAKE_PATH_HOST_BINS}/qt.conf' >> $script
    echo 'export OE_QMAKE_LIBDIR_QT=`qmake -query QT_INSTALL_LIBS`' >> $script
    echo 'export OE_QMAKE_INCDIR_QT=`qmake -query QT_INSTALL_HEADERS`' >> $script
    echo 'export OE_QMAKE_MOC=${OE_QMAKE_PATH_HOST_BINS}/moc' >> $script
    echo 'export OE_QMAKE_UIC=${OE_QMAKE_PATH_HOST_BINS}/uic' >> $script
    echo 'export OE_QMAKE_RCC=${OE_QMAKE_PATH_HOST_BINS}/rcc' >> $script
    echo 'export OE_QMAKE_QDBUSCPP2XML=${OE_QMAKE_PATH_HOST_BINS}/qdbuscpp2xml' >> $script
    echo 'export OE_QMAKE_QDBUSXML2CPP=${OE_QMAKE_PATH_HOST_BINS}/qdbusxml2cpp' >> $script
    echo 'export OE_QMAKE_QT_CONFIG=`qmake -query QT_INSTALL_LIBS`/${QT_DIR_NAME}/mkspecs/qconfig.pri' >> $script
    echo 'export OE_QMAKE_PATH_HOST_BINS=${OE_QMAKE_PATH_HOST_BINS}' >> $script
    echo 'export QMAKESPEC=`qmake -query QT_INSTALL_LIBS`/${QT_DIR_NAME}/mkspecs/linux-oe-g++' >> $script

    # Use relocable sysroot
    sed -i -e 's:${SDKPATHNATIVE}:$OECORE_NATIVE_SYSROOT:g' $script
}

addtask generate_qt_environment_file after do_install before do_package
