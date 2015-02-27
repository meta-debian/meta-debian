require eglibc_${PV}.bb
require recipes-core/eglibc/eglibc-initial.inc

DPR = "1"

# main eglibc recipes muck with TARGET_CPPFLAGS to point into
# final target sysroot but we
# are not there when building eglibc-initial
# so reset it here

TARGET_CPPFLAGS = ""

# Specify add-ons for glibc: nptl, libidn, ports
do_configure () {
        sed -ie 's,{ (exit 1); exit 1; }; },{ (exit 0); }; },g' ${S}/configure
        chmod +x ${S}/configure
        (cd ${S} && gnu-configize) || die "failure in running gnu-configize"
        find ${S} -name "configure" | xargs touch
        ${S}/configure --host=${TARGET_SYS} --build=${BUILD_SYS} \
                --prefix=/usr \
                --without-cvs --disable-sanity-checks \
                --with-headers=${STAGING_DIR_TARGET}${includedir} \
                --with-kconfig=${STAGING_BINDIR_NATIVE} \
                --enable-hacker-mode --enable-add-ons=nptl,libidn,ports
}
