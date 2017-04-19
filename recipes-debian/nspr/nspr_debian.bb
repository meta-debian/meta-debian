#1
# Base recipe: meta/recipes-support/nspr/nspr_4.10.3.bb
# Base branch: daisy
# Base commit: 9e4aad97c3b4395edeb9dc44bfad1092cdf30a47
#

SUMMARY = "Netscape Portable Runtime Library"
HOMEPAGE =  "http://www.mozilla.org/projects/nspr/"
LICENSE = "GPLv2.0 & MPL-2.0 & LGPLv2.1"
LIC_FILES_CHKSUM = "\
 file://configure.in;beginline=3;endline=6;md5=90c2fdee38e45d6302abcfe475c8b5c5 \
 file://Makefile.in;beginline=4;endline=38;md5=beda1dbb98a515f557d3e58ef06bca99\
"

PR = "r0"
RDEPENDS_${PN}-dev += "perl"
TARGET_CC_ARCH += "${LDFLAGS}"

inherit autotools-brokensep debian-package
PV = "4.12"

S = "${WORKDIR}/git/nspr"

SRC_URI += "\
        file://remove-rpath-from-tests.patch \
        file://fix-build-on-x86_64_debian.patch \
        file://trickly-fix-build-on-x86_64_debian.patch \
        file://nspr.pc.in \
"

TESTS = "runtests.pl \
    runtests.sh \
    accept \
    acceptread \
    acceptreademu \
    affinity \
    alarm \
    anonfm \
    atomic \
    attach \
    bigfile \
    cleanup \
    cltsrv  \
    concur \
    cvar \
    cvar2 \
    dlltest \
    dtoa \
    errcodes \
    exit \
    fdcach \
    fileio \
    foreign \
    formattm \
    fsync \
    gethost \
    getproto \
    i2l \
    initclk \
    inrval \
    instrumt \
    intrio \
    intrupt \
    io_timeout \
    ioconthr \
    join \
    joinkk \
    joinku \
    joinuk \
    joinuu \
    layer \
    lazyinit \
    libfilename \
    lltest \
    lock \
    lockfile \
    logfile \
    logger \
    many_cv \
    multiwait \
    nameshm1 \
    nblayer \
    nonblock \
    ntioto \
    ntoh \
    op_2long \
    op_excl \
    op_filnf \
    op_filok \
    op_nofil \
    parent \
    parsetm \
    peek \
    perf \
    pipeping \
    pipeping2 \
    pipeself \
    poll_nm \
    poll_to \
    pollable \
    prftest \
    primblok \
    provider \
    prpollml \
    ranfile \
    randseed \
    reinit \
    rwlocktest \
    sel_spd \
    selct_er \
    selct_nm \
    selct_to \
    selintr \
    sema \
    semaerr \
    semaping \
    sendzlf \
    server_test \
    servr_kk \
    servr_uk \
    servr_ku \
    servr_uu \
    short_thread \
    sigpipe \
    socket \
    sockopt \
    sockping \
    sprintf \
    stack \
    stdio \
    str2addr \
    strod \
    switch \
    system \
    testbit \
    testfile \
    threads \
    timemac \
    timetest \
    tpd \
    udpsrv \
    vercheck \
    version \
    writev \
    xnotify \
    zerolen"

do_configure() {
        oe_runconf
}

do_compile_prepend() {
        oe_runmake CROSS_COMPILE=1 CFLAGS="-DXP_UNIX" LDFLAGS="-Wl,--as-needed" CC=gcc -C config export
}

do_compile_append() {
        oe_runmake -C pr/tests
}

do_install_append() {
        install -D ${WORKDIR}/nspr.pc.in ${D}${libdir}/pkgconfig/nspr.pc
        sed -i s:OEPREFIX:${prefix}:g ${D}${libdir}/pkgconfig/nspr.pc
        sed -i s:OELIBDIR:${libdir}:g ${D}${libdir}/pkgconfig/nspr.pc
        sed -i s:OEINCDIR:${includedir}:g ${D}${libdir}/pkgconfig/nspr.pc
        sed -i s:OEEXECPREFIX:${exec_prefix}:g ${D}${libdir}/pkgconfig/nspr.pc
        cd ${S}/pr/tests
        mkdir -p ${D}${libdir}/nspr/tests
        install -m 0755 ${TESTS} ${D}${libdir}/nspr/tests

        # delete compile-et.pl and perr.properties from ${bindir} because these are
        # only used to generate prerr.c and prerr.h files from prerr.et at compile
        # time
        rm ${D}${bindir}/compile-et.pl ${D}${bindir}/prerr.properties

        # Install softlink for libnspr4-0d package
        ln -sf libnspr4.so ${D}${libdir}/libnspr4.so.0d
        ln -sf libplc4.so ${D}${libdir}/libplc4.so.0d
        ln -sf libplds4.so ${D}${libdir}/libplds4.so.0d

        # Install softlink for libnspr4-dev package
        ln -sf nspr.pc ${D}${libdir}/pkgconfig/xulrunner-nspr.pc
}

# Correct list of packages according to list of packages build from
# nspr source in Debian Jessie
PACKAGES = "lib${PN} lib${PN}-0d lib${PN}-dev lib${PN}-staticdev lib${PN}-dbg"

# Set files for debug package
DOTDEBUG-dbg = "${bindir}/.debug ${sbindir}/.debug ${libexecdir}/.debug ${libdir}/.debug \
            ${base_bindir}/.debug ${base_sbindir}/.debug ${base_libdir}/.debug ${libdir}/${BPN}/.debug \
            ${libdir}/matchbox-panel/.debug /usr/src/debug"

DEBUGFILEDIRECTORY-dbg = "/usr/lib/debug /usr/src/debug"

FILES_lib${PN} = "${libdir}/lib*.so"
FILES_lib${PN}-0d = "${libdir}/lib*.so.0d"
FILES_lib${PN}-staticdev = "${libdir}/*.a ${base_libdir}/*.a ${libdir}/${BPN}/*.a"
FILES_lib${PN}-dev = "${bindir}/* ${libdir}/nspr/tests/* ${libdir}/pkgconfig \
                ${includedir}/* ${datadir}/aclocal/* "
FILES_lib${PN}-dbg = "${@d.getVar(['DOTDEBUG-dbg', 'DEBUGFILEDIRECTORY-dbg'] \
			[d.getVar('PACKAGE_DEBUG_SPLIT_STYLE', True) == 'debug-file-directory'], True)} \
			${libdir}/nspr/tests/.debug/*"

PKG_lib${PN} = "lib${PN}4"
PKG_lib${PN}-0d = "lib${PN}4-0d"
PKG_lib${PN}-dev = "lib${PN}4-dev"
PKG_lib${PN}-dbg = "lib${PN}4-dbg"

BBCLASSEXTEND = "native nativesdk"
