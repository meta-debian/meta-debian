stash_locale_sysroot_cleanup() {
        stash_locale_cleanup ${SYSROOT_DESTDIR}
        # We don't want to ship an empty /usr/share
        rmdir --ignore-fail-on-non-empty ${SYSROOT_DESTDIR}${datadir}
}
stash_locale_package_cleanup() {
        stash_locale_cleanup ${PKGD}
        # We don't want to ship an empty /usr/share
        rmdir --ignore-fail-on-non-empty ${PKGD}${datadir}
}
