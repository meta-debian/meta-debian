#
# base on meta-qt5/classes/populate_sdk_qt5_base.bbclass
#

inherit qmake5_paths

# inherit debian-multiarch to get value of DEB_HOST_MULTIARCH
# which is used in OE_QMAKE_PATH* variables.
inherit debian-multiarch

create_sdk_files_prepend () {
    # make a symbolic link to mkspecs for compatibility with QTCreator
    (cd ${SDK_OUTPUT}/${SDKPATHNATIVE}; \
         ln -sf ${SDKTARGETSYSROOT}${OE_QMAKE_PATH_QT_ARCHDATA}/mkspecs mkspecs;)

    # Generate a qt.conf file to be deployed with the SDK
    qtconf=${SDK_OUTPUT}/${SDKPATHNATIVE}${OE_QMAKE_PATH_HOST_BINS_SDK}/qt.conf
    touch $qtconf
    echo '[Paths]' >> $qtconf
    echo 'Prefix = ${SDKTARGETSYSROOT}' >> $qtconf
    echo 'Headers = ${SDKTARGETSYSROOT}${OE_QMAKE_PATH_QT_HEADERS}' >> $qtconf
    echo 'Libraries = ${SDKTARGETSYSROOT}${OE_QMAKE_PATH_LIBS}' >> $qtconf
    echo 'ArchData = ${SDKTARGETSYSROOT}${OE_QMAKE_PATH_QT_ARCHDATA}' >> $qtconf
    echo 'Data = ${SDKTARGETSYSROOT}${OE_QMAKE_PATH_QT_DATA}' >> $qtconf
    echo 'Binaries = ${SDKTARGETSYSROOT}${OE_QMAKE_PATH_QT_BINS}' >> $qtconf
    echo 'LibraryExecutables = ${SDKTARGETSYSROOT}${OE_QMAKE_PATH_LIBEXECS}' >> $qtconf
    echo 'Plugins = ${SDKTARGETSYSROOT}${OE_QMAKE_PATH_PLUGINS}' >> $qtconf
    echo 'Imports = ${SDKTARGETSYSROOT}${OE_QMAKE_PATH_IMPORTS}' >> $qtconf
    echo 'Qml2Imports = ${SDKTARGETSYSROOT}${OE_QMAKE_PATH_QML}' >> $qtconf
    echo 'Translations = ${SDKTARGETSYSROOT}${OE_QMAKE_PATH_QT_TRANSLATIONS}' >> $qtconf
    echo 'Documentation = ${SDKTARGETSYSROOT}${OE_QMAKE_PATH_QT_DOCS}' >> $qtconf
    echo 'Settings = ${SDKTARGETSYSROOT}${OE_QMAKE_PATH_QT_SETTINGS}' >> $qtconf
    echo 'Examples = ${SDKTARGETSYSROOT}${OE_QMAKE_PATH_QT_EXAMPLES}' >> $qtconf
    echo 'Tests = ${SDKTARGETSYSROOT}${OE_QMAKE_PATH_QT_TESTS}' >> $qtconf
    echo 'HostPrefix = ${SDKPATHNATIVE}' >> $qtconf
    echo 'HostBinaries = ${SDKPATHNATIVE}${OE_QMAKE_PATH_HOST_BINS_SDK}' >> $qtconf
}
