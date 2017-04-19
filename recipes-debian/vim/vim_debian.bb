#
# base recipe: meta-oe/recipes-support/vim/vim_7.4.481.bb
# from meta-openembedded
#

SUMMARY = "Vi IMproved - enhanced vi editor"

PR = "r1"

inherit debian-package
PV = "7.4.488"

# vimdiff doesn't like busybox diff
RSUGGESTS_${PN} = "diffutils"

LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://README.txt;md5=92d522c8ffff037663e0cad91ce5c123"

S = "${WORKDIR}/git/src"

DEPENDS = "ncurses gettext-native acl"

inherit autotools update-alternatives
inherit autotools-brokensep

variant = "basic"

CFGFLAGS = " \
	--prefix=${prefix} \
	--mandir=${mandir} \
	--without-local-dir \
	--enable-fail-if-missing"

OPTFLAGS = " \
	--enable-cscope \
	--enable-gpm \
	--disable-smack \
	--with-features=huge \
	--enable-multibyte \
	--enable-acl"

NOXFLAGS = " \
	--without-x \
	--enable-gui=no"

NOINTERPFLAGS = " \
	--disable-luainterp \
	--disable-mzschemeinterp \
	--disable-perlinterp \
	--disable-pythoninterp \
	--disable-python3interp \
	--disable-rubyinterp \
	--disable-tclinterp"

EXTRA_OECONF = "${CFGFLAGS} ${OPTFLAGS} ${NOXFLAGS} ${NOINTERPFLAGS}"

# Configure to pass the cross-compiling.
EXTRA_OECONF += "vim_cv_toupper_broken=yes \
		vim_cv_terminfo=yes \
		vim_cv_tty_group=world \
		vim_cv_getcwd_broken=no \
		vim_cv_stat_ignores_slash=no \
		vim_cv_memmove_handles_overlap=yes \
		--with-tlib=ncurses \
		"
# Configuration to disable strip
# --disable-selinux: Don't use selinux support
EXTRA_OECONF += "ac_cv_prog_STRIP=/bin/true --disable-selinux"

# vim configure.in contains functions which got 'dropped' by autotools.bbclass
do_configure () {
	rm -f auto/*
	touch auto/config.mk
	aclocal
	autoconf
	oe_runconf
	touch auto/configure
	touch auto/config.mk auto/config.h
}

#Available PACKAGECONFIG options are acl
PACKAGECONFIG ??= ""
PACKAGECONFIG += "${@bb.utils.contains('DISTRO_FEATURES', 'acl', 'acl', '', d)}"

PACKAGECONFIG[acl] = "--enable-acl,--disable-acl,acl,"

# Install package follow debian
do_install_append () {
	install -m 0755 ${S}/../debian/helpztags ${D}${bindir}
	install -d ${D}${sysconfdir}/vim
	install -m 0644 ${S}/../debian/runtime/vimrc ${D}${sysconfdir}/vim
	install -d ${D}${libdir}/mime/packages
	install -m 0644 ${S}/../debian/vim-common.mime ${D}${libdir}/mime/packages/vim-common
	install -d ${D}/var/lib/vim/addons
	mv ${D}${bindir}/vim ${D}${bindir}/vim.${variant}
}

# parallel install are not supported
PARALLEL_MAKEINST = ""

PACKAGES =+ "${PN}-common ${PN}-runtime"
FILES_${PN}-runtime = "${bindir}/vimtutor"
FILES_${PN}-common = " \
	${sysconfdir}/vim/vimrc \
	${bindir}/xxd \
	${bindir}/helpztags \
	${libdir}/mime/packages/vim-common \
	${localstatedir}"

# Recommend that runtime data is installed along with vim
RRECOMMENDS_${PN} = "${PN}-common ${PN}-runtime"

# Create alternative links follow Debian
ALTERNATIVE_${PN} = "vim vimdiff rvim rview vi view ex editor "
ALTERNATIVE_TARGET = "${bindir}/vim.${variant}"
ALTERNATIVE_LINK_NAME[vim] = "${bindir}/vim"
ALTERNATIVE_LINK_NAME[vimdiff] = "${bindir}/vimdiff"
ALTERNATIVE_LINK_NAME[rvim] = "${bindir}/rvim"
ALTERNATIVE_LINK_NAME[rview] = "${bindir}/rview"
ALTERNATIVE_LINK_NAME[vi] = "${bindir}/vi"
ALTERNATIVE_LINK_NAME[view] = "${bindir}/view"
ALTERNATIVE_LINK_NAME[ex] = "${bindir}/ex"
ALTERNATIVE_LINK_NAME[editor] = "${bindir}/editor"
ALTERNATIVE_PRIORITY = "30"
