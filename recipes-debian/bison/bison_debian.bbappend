do_install_append_class-nativesdk() {
        create_wrapper ${D}/${bindir}/bison \
                BISON_PKGDATADIR=${datadir}/bison
}

