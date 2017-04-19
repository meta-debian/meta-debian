#
# Base recipe: meta/recipes-core/ncurses/ncurses_5.9.bb
# Base branch: daisy
# Base commit: 9e4aad97c3b4395edeb9dc44bfad1092cdf30a47
# 

SUMMARY = "The New Curses library"
DESCRIPTION = "SVr4 and XSI-Curses compatible curses library and terminfo tools including tic, infocmp, captoinfo. Supports color, multiple highlights, forms-drawing characters, and automatic recognition of keypad and function-key sequences. Extensions include resizable windows and mouse support on both xterm and Linux console using the gpm library."
HOMEPAGE = "http://www.gnu.org/software/ncurses/ncurses.html"

inherit debian-package autotools binconfig multilib_header update-alternatives
PV = "5.9+20140913"

PR = "r1"
DEPENDS = "ncurses-native"
DEPENDS_class-native = ""

LICENSE = "MIT"
LIC_FILES_CHKSUM = "\
file://ncurses/base/version.c;beginline=1;endline=27;md5=cbc180a8c44ca642e97c35452fab5f66\
"

SRC_URI += " \
	file://tic-hang.patch \
	file://config.cache \
	file://exclude-host-includedir-from-CXX-test.patch \
"

EXTRA_AUTORECONF = "-I m4"
CONFIG_SITE =+ "${WORKDIR}/config.cache"

# Whether to enable separate widec libraries; must be 'true' or 'false'
#
# TODO: remove this variable when widec is supported in every setup?
ENABLE_WIDEC = "true"

# _GNU_SOURCE is required for widec stuff and is detected automatically
# for target objects.  But it must be set manually for native and sdk
# builds.
BUILD_CPPFLAGS += "-D_GNU_SOURCE"

# natives don't generally look in base_libdir
base_libdir_class-native = "${libdir}"
base_libdir_class-nativesdk = "${libdir}"

# Display corruption occurs on 64 bit hosts without these settings
# This was derrived from the upstream debian ncurses which uses
# these settings for 32 and 64 bit hosts.
EXCONFIG_ARGS = ""
EXCONFIG_ARGS_class-native = " \
                --disable-lp64 \
                --with-chtype='long' \
                --with-mmask-t='long'"
EXCONFIG_ARGS_class-nativesdk = " \
                --disable-lp64 \
                --with-chtype='long' \
                --with-mmask-t='long'"

# Fall back to the host termcap / terminfo for -nativesdk and -native
# The reality is a work around for strange problems with things like
# "bitbake -c menuconfig busybox" where it cannot find the terminfo
# because the sstate had a hard coded search path.  Until this is fixed
# another way this is deemed good enough.
EX_TERMCAP = ""
EX_TERMCAP_class-native = ":/etc/termcap:/usr/share/misc/termcap"
EX_TERMCAP_class-nativesdk = ":/etc/termcap:/usr/share/misc/termcap"
EX_TERMINFO = ""
EX_TERMINFO_class-native = ":/etc/terminfo:/usr/share/terminfo:/usr/share/misc/terminfo:/lib/terminfo"
EX_TERMINFO_class-nativesdk = ":/etc/terminfo:/usr/share/terminfo:/usr/share/misc/terminfo:/lib/terminfo"

# Helper function for do_configure to allow multiple configurations
# $1 the directory to run configure in
# $@ the arguments to pass to configure
ncurses_configure() {
	mkdir -p $1
	cd $1
	shift
	oe_runconf \
		--disable-static \
		--without-debug \
		--without-ada \
		--without-gpm \
		--enable-hard-tabs \
		--enable-xmc-glitch \
		--enable-colorfgbg \
		--with-default-terminfo-dir='${sysconfdir}/terminfo' \
		--with-terminfo-dirs='${sysconfdir}/terminfo:${base_libdir}/terminfo${EX_TERMINFO}' \
		--with-termpath='${sysconfdir}/termcap:${datadir}/misc/termcap${EX_TERMCAP}' \
		--with-shared \
		--disable-big-core \
		--program-prefix= \
		--with-ticlib=tic \
		--with-termlib=tinfo \
		--with-xterm-kbs=del \
		--without-profile \
		--enable-sigwinch \
		--enable-pc-files \
		--disable-rpath-hack \
		--disable-rpath \
		--enable-echo \
		--enable-const \
		--enable-symlinks \
		--without-tests \
		--disable-termcap \
		${EXCONFIG_ARGS} \
		--with-manpage-format=normal \
		"$@" || return 1
	cd ..
}

# Override the function from the autotools class; ncurses requires a
# patched autoconf213 to generate the configure script. This autoconf
# is not available so that the shipped script will be used.
do_configure() {
	# check does not work with cross-compiling and is generally
	# broken because it requires stdin to be pollable (which is
	# not the case for /dev/null redirections)
	export cf_cv_working_poll=yes

	( cd ${S}; gnu-configize --force )
	ncurses_configure "narrowc" || \
		return 1
	! ${ENABLE_WIDEC} || \
		ncurses_configure "widec" "--enable-widec" "--without-progs" \
					"--disable-overwrite" 
}

# Install headers to /usr/include instead of /usr/include/ncurses
# non-native recipe didn't have this problem because of oe_multilib_header
do_configure_append_class-nativesdk () {
	ncurses_configure "narrowc" "--enable-overwrite"
}

do_configure_append_class-native () {
	ncurses_configure "narrowc" "--enable-overwrite"
}

do_compile() {
	oe_runmake -C narrowc libs
	oe_runmake -C narrowc/progs

	! ${ENABLE_WIDEC} || \
		oe_runmake -C widec libs
}

# set of expected differences between narrowc and widec header
#
# TODO: the NCURSES_CH_T difference can cause real problems :(
_unifdef_cleanup = " \
  -e '\!/\* \$Id: curses.wide,v!,\!/\* \$Id: curses.tail,v!d' \
  -e '/^#define NCURSES_CH_T /d' \
  -e '/^#include <wchar.h>/d' \
  -e '\!^/\* .* \*/!d' \
"

do_test[depends] = "unifdef-native:do_populate_sysroot"
do_test[dirs] = "${S}"
do_test() {
	${ENABLE_WIDEC} || return 0

	# make sure that the narrow and widec header are compatible
	# and differ only in minor details.
	unifdef -k narrowc/include/curses.h | \
		sed ${_unifdef_cleanup} > curses-narrowc.h
	unifdef -k widec/include/curses.h | \
		sed ${_unifdef_cleanup} > curses-widec.h

	diff curses-narrowc.h curses-widec.h
}

# Split original _install_opts to two parts.
# One is the options to install contents, the other is the parameters \
# when running command "make install"
_install_opts = "\
  install.libs install.includes install.man \
"
_install_cfgs = "\
  DESTDIR='${D}' \
  PKG_CONFIG_LIBDIR='${libdir}/pkgconfig' \
"
do_install() {
	# Order of installation is important; widec installs a 'curses.h'
	# header with more definitions and must be installed last hence.
	# Compatibility of these headers will be checked in 'do_test()'.
	oe_runmake -C narrowc ${_install_cfgs} ${_install_opts} \
		install.progs

	# The install.data should run after install.libs, otherwise
	# there would be a race issue in a very critical conditon, since
	# tic will be run by install.data, and tic needs libtinfo.so
	# which would be regenerated by install.libs.
	oe_runmake -C narrowc ${_install_cfgs} \
		install.data

	! ${ENABLE_WIDEC} || \
		oe_runmake -C widec ${_install_cfgs} ${_install_opts}

	cd narrowc

	# i think we can use xterm-color as default xterm
	if [ -e ${D}${sysconfdir}/terminfo/x/xterm-color ]
	then
		ln -sf xterm-color ${D}${sysconfdir}/terminfo/x/xterm
	fi

	# create linker scripts for libcurses.so and libncurses to
	# link against -ltinfo when needed. Some builds might break
	# else when '-Wl,--no-copy-dt-needed-entries' has been set in
	# linker flags.
	for i in libncurses libncursesw; do
		f=${D}${libdir}/$i.so
		test -h $f || continue
		rm -f $f
		echo '/* GNU ld script */'  >$f
		echo "INPUT($i.so.5 AS_NEEDED(-ltinfo))" >>$f
	done

	# Make sure that libcurses is linked so that it gets -ltinfo
	# also, this should be addressed upstream really.
	ln -sf libncurses.so ${D}${libdir}/libcurses.so

	# create libtermcap.so linker script for backward compatibility
	f=${D}${libdir}/libtermcap.so
	echo '/* GNU ld script */' >$f
	echo 'INPUT(AS_NEEDED(-ltinfo))' >>$f

	if [ ! -d "${D}${base_libdir}" ]; then
		# Setting base_libdir to libdir as is done in the -native
		# case will skip this code
		mkdir ${D}${base_libdir}
		mv ${D}${libdir}/libncurses.so.* ${D}${base_libdir}
		! ${ENABLE_WIDEC} || \
			mv ${D}${libdir}/libncursesw.so.* ${D}${base_libdir}

		mv ${D}${libdir}/libtinfo.so.* ${D}${base_libdir}
		rm ${D}${libdir}/libtinfo.so

		# Use lnr to ensure this is a relative link despite absolute paths
		# (as we can't know the relationship between base_libdir and libdir).
		# At some point we can rely on coreutils 8.16 which has ln -r.
		lnr ${D}${base_libdir}/libtinfo.so.5 ${D}${libdir}/libtinfo.so
	fi

	oe_multilib_header curses.h
}

BADTERMINFO = "5/5250 f/fbterm i/iterm i/iterm-am i/iterm-color j/jfbterm \
k/kon k/kon2 l/linux-5250 s/stterm s/stterm-256color"

LIBTERMINFOFILES = "a/ansi c/cons25 c/cons25-debian c/cygwin d/dumb h/hurd l/linux \
m/mach m/mach-bold m/mach-color m/mach-gnu m/mach-gnu-color p/pcansi \
r/rxvt r/rxvt-m r/rxvt-basic r/rxvt-unicode \
s/screen s/screen-bce s/screen-s s/screen-w s/sun v/vt100 v/vt102 \
v/vt220 v/vt52 x/xterm x/xterm-debian x/xterm-xfree86 x/xterm-color \
x/xterm-mono x/xterm-r5 x/xterm-r6 x/xterm-vt220 \
x/xterm-256color s/screen-256color s/screen-256color-bce \
w/wsvt25 w/wsvt25m E/Eterm E/Eterm-color"

# Follow Debian rules
do_install_append() {
	#rm -f ${D}${libdir}/libncurses.so
	#echo "INPUT(libncurses.so.5 -ltinfo)" > ${D}${libdir}/libncurses.so
	#rm -f ${D}${libdir}/libncursesw.so
	#echo "INPUT(libncursesw.so.5 -ltinfo)" > ${D}${libdir}/libncursesw.so

	mv ${D}${sysconfdir}/terminfo ${D}${datadir}/terminfo

	cd ${D}${datadir}/terminfo && rm -f ${BADTERMINFO}
	rm -f ${D}${libdir}/terminfo \
		${D}${libdir}/libcurses.so \
		${D}${libdir}/libtinfo.so

	set -e; \
	for ti in xterm hurd rxvt rxvt-unicode; do \
		TERMINFO=${D}${datadir}/terminfo \
		tic -x ${S}/debian/$ti.ti; \
	done

	set -e; \
	for f in ${LIBTERMINFOFILES}; do \
		dir=${D}${base_libdir}/terminfo/$(dirname $f); \
		mkdir -p $dir; \
		mv ${D}${datadir}/terminfo/$f $dir; \
	done

	install -d ${D}${sysconfdir}/terminfo
	install --mode 644 \
		${S}/debian/README.etc ${D}${sysconfdir}/terminfo/README

	install -d ${D}${libdir}/valgrind
	install -m 0644 ${S}/misc/ncurses.supp ${D}${libdir}/valgrind/

	# Create link according to Debian package files
	ln -sf libncurses.so ${D}${libdir}/libcurses.so

	# In case libdir is not the same as base_libdir
	# needed to create symlink to /lib instead of /usr/lib
	if [ "${base_libdir}" != "${libdir}" ]; then
		ln -sf ../../lib/libtinfo.so.5 ${D}${libdir}/libtinfo.so
	else
		ln -sf libtinfo.so.5 ${D}${libdir}/libtinfo.so
	fi

	ln -sf libtinfo.a ${D}${libdir}/libtermcap.a
	ln -sf ../../../../lib/terminfo/c/cons25 ${D}${datadir}/terminfo/c/cons25
	ln -sf ../../../../lib/terminfo/s/sun ${D}${datadir}/terminfo/s/sun
	ln -sf ../../../../lib/terminfo/v/vt100 ${D}${datadir}/terminfo/v/vt100
	ln -sf ../../../../lib/terminfo/v/vt220 ${D}${datadir}/terminfo/v/vt220
	ln -sf ../../../../lib/terminfo/x/xterm-color ${D}${datadir}/terminfo/x/xterm-color
	ln -sf ../../../../lib/terminfo/x/xterm-r6 ${D}${datadir}/terminfo/x/xterm-r6
}

ALTERNATIVE_PRIORITY = "100"

ALTERNATIVE_ncurses-tools_class-target = "clear reset"

# Re-set packages, list of files for each package
# based on Debian package
PACKAGES = " \
  libncurses5 \
  libncurses5-dev \
  libncurses5-staticdev \
  libncursesw5 \
  libncursesw5-dev \
  libncursesw5-staticdev \
  libtinfo-dev \
  libtinfo-staticdev \
  libtinfo5 \
  ncurses-base \
  ncurses-bin \
  ncurses-doc \
  ncurses-examples \
  ncurses-term \
  ncurses-dbg \
"

FILES_libncurses5 = " \
  ${base_libdir}/libncurses.so.* \
  ${libdir}/libform.so.* \
  ${libdir}/libmenu.so.* \
  ${libdir}/libpanel.so.* \
"

FILES_libncurses5-dev = " \
  ${bindir}/ncurses5-config \
  ${includedir}/*.h \
  ${libdir}/pkgconfig/form.pc \
  ${libdir}/pkgconfig/menu.pc \
  ${libdir}/pkgconfig/ncurses++.pc \
  ${libdir}/pkgconfig/ncurses.pc \
  ${libdir}/pkgconfig/panel.pc \
  ${datadir}/pkgconfig \
  ${datadir}/aclocal \
  ${libdir}/libcurses.so \
  ${libdir}/libform.so \
  ${libdir}/libmenu.so \
  ${libdir}/libncurses.so \
  ${libdir}/libpanel.so \
  ${libdir}/${BPN}/*.la \
  ${base_libdir}/*.la \
"

FILES_libncurses5-staticdev = " \
  ${libdir}/libcurses.a \
  ${libdir}/libform.so \
  ${libdir}/libform.a \
  ${libdir}/libmenu.a \
  ${libdir}/libncurses++.a \
  ${libdir}/libncurses.a \
  ${libdir}/libpanel.a \
"

FILES_libncursesw5 = " \
  ${base_libdir}/libncursesw.so.* \
  ${libdir}/libformw.so.* \
  ${libdir}/libmenuw.so.* \
  ${libdir}/libpanelw.so.* \
"

FILES_libncursesw5-dev = "\
  ${bindir}/ncursesw5-config \
  ${includedir}/ncursesw/ \
  ${libdir}/pkgconfig/formw.pc \
  ${libdir}/pkgconfig/menuw.pc \
  ${libdir}/pkgconfig/ncurses++w.pc \
  ${libdir}/pkgconfig/ncursesw.pc \
  ${libdir}/pkgconfig/panelw.pc \
  ${libdir}/*w.la \
  ${libdir}/*w.so \
"

FILES_libncursesw5-staticdev = " \
  ${libdir}/*w.a \
"

FILES_libtinfo-dev = " \
  ${libdir}/valgrind/ \
  ${libdir}/libtermcap.so \
  ${libdir}/libtic.so \
  ${libdir}/libtinfo.so \
  ${libdir}/pkgconfig/tic.pc \
  ${libdir}/pkgconfig/tinfo.pc \
"

FILES_libtinfo-staticdev = " \
  ${libdir}/libtermcap.a \
  ${libdir}/libtic.a \
  ${libdir}/libtinfo.a \
"

FILES_libtinfo5 = " \
  ${base_libdir}/libtinfo.so.* \
  ${libdir}/libtic.so.* \
"

FILES_ncurses-base = " \
  ${sysconfdir}/terminfo \
  ${base_libdir}/terminfo \
  ${datadir}/tabset \
"

FILES_ncurses-term = " \
  ${datadir}/terminfo \
"

FILES_${PN}-doc = " \
  ${docdir} \
  ${mandir} \
"

FILES_ncurses-examples = " \
  ${base_libdir}/ncurses/examples \
"

DOTDEBUG-dbg = "${bindir}/.debug ${sbindir}/.debug ${libexecdir}/.debug ${libdir}/.debug \
            ${base_bindir}/.debug ${base_sbindir}/.debug ${base_libdir}/.debug ${libdir}/${BPN}/.debug \
            ${libdir}/matchbox-panel/.debug /usr/src/debug"

DEBUGFILEDIRECTORY-dbg = "/usr/lib/debug /usr/src/debug"

FILES_${PN}-dbg = "${@d.getVar(['DOTDEBUG-dbg', 'DEBUGFILEDIRECTORY-dbg'][d.getVar('PACKAGE_DEBUG_SPLIT_STYLE', True) == 'debug-file-directory'], True)}"

RDEPENDS_libncurses5 += "libtinfo5"
RDEPENDS_libncurses5-dev += "libtinfo-dev ncurses-bin"
RDEPENDS_libncursesw5 += "libtinfo5"
RDEPENDS_libncursesw5-dev += "libtinfo-dev ncurses-bin"
RDEPENDS_ncurses-term += "ncurses-base"

BBCLASSEXTEND = "native nativesdk"
